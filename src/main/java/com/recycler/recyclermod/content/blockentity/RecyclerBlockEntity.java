package com.recycler.recyclermod.content.blockentity;

import com.recycler.recyclermod.content.menu.RecyclerMenu;
import com.recycler.recyclermod.registry.ModBlocksEntities;
import com.recycler.recyclermod.registry.ModItems;
import com.recycler.recyclermod.registry.ModSounds;
import com.recycler.recyclermod.utils.Range;
import com.recycler.recyclermod.utils.ScrapResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecyclerBlockEntity extends BlockEntity implements MenuProvider {
    public static final int IN0=0, IN1=1, IN2=2, IN3=3, IN4=4;
    public static final int OUT0=5, OUT1=6, OUT2=7, OUT3=8, OUT4=9;

    private final ItemStackHandler inventory = new ItemStackHandler(10) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    private final int[] progress = new int[5];
    private static final int MAX_PROGRESS = 280;
    private static final int ENERGY_PER_TICK_PER_PAIR = 20;
    private final EnergyStorage energy = new EnergyStorage( 8_000, 1_000, 1_000) {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            int r = super.receiveEnergy(maxReceive, simulate);
            if (r > 0 && !simulate) setChanged();
            return r;
        }
        @Override public int extractEnergy(int maxExtract, boolean simulate) {
            int e = super.extractEnergy(maxExtract, simulate);
            if (e > 0 && !simulate) setChanged();
            return e;
        }
    };

    static class IoOrderedHandler implements IItemHandler {
        private final ItemStackHandler inv;
        IoOrderedHandler(ItemStackHandler inv){ this.inv = inv; }

        @Override public int getSlots(){ return inv.getSlots(); }
        @Override public @NotNull ItemStack getStackInSlot(int slot){ return inv.getStackInSlot(slot); }
        @Override public int getSlotLimit(int slot){ return inv.getSlotLimit(slot); }
        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack){ return slot >= IN0 && slot <= IN4; }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            for (int s = IN0; s <= IN4 && !stack.isEmpty(); s++) stack = inv.insertItem(s, stack, simulate);
            return stack;
        }
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            for (int s = OUT0; s <= OUT4; s++) {
                ItemStack taken = inv.extractItem(s, amount, simulate);
                if (!taken.isEmpty()) return taken;
            }
            return ItemStack.EMPTY;
        }
    }

    private final LazyOptional<IItemHandler> itemsAutomation = LazyOptional.of(() -> new IoOrderedHandler(inventory));
    private final LazyOptional<IItemHandler> itemsPlayer     = LazyOptional.of(() -> inventory);
    private final LazyOptional<IEnergyStorage> energyOpt     = LazyOptional.of(() -> energy);

    private boolean wasWorking = false;
    private final RandomSource rand = RandomSource.create();

    public RecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocksEntities.RECYCLER.get(), pos, state);
    }

    private boolean canProcessPair(ItemStack in, ItemStack out) {
        if (in.isEmpty()) return false;
        Range range = ScrapResolver.getScrapRange(in.copyWithCount(1));
        int min = Math.max(0, range.min());
        int max = Math.max(min, range.max());
        int need = max;
        if (need <= 0) return false;

        if (out.isEmpty()) {
            int cap = new ItemStack(ModItems.SCRAP.get()).getMaxStackSize();
            return need <= cap;
        }
        if (!ItemStack.isSameItemSameTags(out, new ItemStack(ModItems.SCRAP.get()))) return false;
        return out.getCount() + need <= out.getMaxStackSize();
    }

    private static void stopLoopForNearby(ServerLevel sl, BlockPos pos) {
        ResourceLocation id = ModSounds.RECYCLER_LOOP.get().getLocation();
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        for (ServerPlayer p : sl.players()) {
            if (p.distanceToSqr(cx, cy, cz) <= 32*32) {
                p.connection.send(new ClientboundStopSoundPacket(id, SoundSource.BLOCKS));
            }
        }
    }

    public void stopLoopOnRemove(ServerLevel sl) {
        ResourceLocation id = ModSounds.RECYCLER_LOOP.get().getLocation();
        double cx = this.worldPosition.getX() + 0.5;
        double cy = this.worldPosition.getY() + 0.5;
        double cz = this.worldPosition.getZ() + 0.5;
        for (ServerPlayer p : sl.players()) {
            if (p.distanceToSqr(cx, cy, cz) <= 32 * 32) {
                p.connection.send(new ClientboundStopSoundPacket(id, SoundSource.BLOCKS));
            }
        }
        this.wasWorking = false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RecyclerBlockEntity be) {
        if (level.isClientSide) return;

        boolean working = false;

        for (int i = 0; i < 5; i++) {
            int inIdx  = IN0 + i;
            int outIdx = OUT0 + i;
            ItemStack in  = be.inventory.getStackInSlot(inIdx);
            ItemStack out = be.inventory.getStackInSlot(outIdx);

            boolean can = be.canProcessPair(in, out);
            if (!can) { be.progress[i] = 0; continue; }

            boolean hasEnergy = be.energy.extractEnergy(ENERGY_PER_TICK_PER_PAIR, true) == ENERGY_PER_TICK_PER_PAIR;
            if (!hasEnergy) continue;

            working = true;

            be.energy.extractEnergy(ENERGY_PER_TICK_PER_PAIR, false);
            be.progress[i]++;

            if (be.progress[i] >= MAX_PROGRESS) {
                Range range = ScrapResolver.getScrapRange(in.copyWithCount(1));
                int give = Math.max(0, range.sample(be.rand));

                if (give > 0) {
                    in.shrink(1);
                    ItemStack outNow = be.inventory.getStackInSlot(outIdx);
                    if (outNow.isEmpty()) {
                        be.inventory.setStackInSlot(outIdx, new ItemStack(ModItems.SCRAP.get(), give));
                    } else {
                        ItemStack copy = outNow.copy();
                        copy.grow(give);
                        be.inventory.setStackInSlot(outIdx, copy);
                    }
                }
                be.progress[i] = 0;
                be.setChanged();
            }
        }

        if (working && level instanceof ServerLevel sl) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.05D;
            double z = pos.getZ() + 0.5D;
            sl.sendParticles(ParticleTypes.SMOKE, x, y, z, 2, 0.15D, 0.05D, 0.15D, 0.01D);
        }

        if (state.hasProperty(BlockStateProperties.LIT)) {
            boolean litNow = state.getValue(BlockStateProperties.LIT);
            if (litNow != working) {
                BlockState newState = state.setValue(BlockStateProperties.LIT, working);
                level.setBlock(pos, newState, 3);
                level.sendBlockUpdated(pos, state, newState, 2);
            }
        }

        if (!working && be.wasWorking && level instanceof ServerLevel sl) {
            stopLoopForNearby(sl, pos);
        }
        be.wasWorking = working;
    }

    @OnlyIn(Dist.CLIENT)
    private Object clientSoundRef;

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(RecyclerBlockEntity be) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        var sm = mc.getSoundManager();
        boolean lit = be.getBlockState().hasProperty(BlockStateProperties.LIT)
                && be.getBlockState().getValue(BlockStateProperties.LIT);

        boolean hasSound = be.clientSoundRef != null && be.clientSoundRef instanceof com.recycler.recyclermod.client.sound.RecyclerLoopSound;
        com.recycler.recyclermod.client.sound.RecyclerLoopSound current = hasSound
                ? (com.recycler.recyclermod.client.sound.RecyclerLoopSound) be.clientSoundRef : null;

        if (lit) {
            if (current == null || current.isStopped()) {
                var snd = new com.recycler.recyclermod.client.sound.RecyclerLoopSound(be.getBlockPos());
                be.clientSoundRef = snd;
                sm.queueTickingSound(snd);
            }
        } else {
            if (current != null && !current.isStopped()) {
                current.requestStop();
            }
            be.clientSoundRef = null;
        }
    }

    public ItemStackHandler getInventory(){ return inventory; }
    public int getProgress(int i){ return progress[i]; }
    public int getMaxProgress(){ return MAX_PROGRESS; }
    public IEnergyStorage getEnergy(){ return energy; }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return (side == null ? itemsPlayer : itemsAutomation).cast();
        if (cap == ForgeCapabilities.ENERGY)       return energyOpt.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemsAutomation.invalidate();
        itemsPlayer.invalidate();
        energyOpt.invalidate();
    }

    @Override
    public void setRemoved() {
        if (this.level instanceof ServerLevel sl) {
            stopLoopOnRemove(sl);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("Energy",    energy.serializeNBT());
        tag.putInt("P0", progress[0]); tag.putInt("P1", progress[1]); tag.putInt("P2", progress[2]);
        tag.putInt("P3", progress[3]); tag.putInt("P4", progress[4]);
        tag.putBoolean("WasWork", wasWorking);
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) inventory.deserializeNBT(tag.getCompound("Inventory"));
        if (tag.contains("Energy"))    energy.deserializeNBT(tag.get("Energy"));
        for (int i=0;i<5;i++) progress[i] = tag.getInt("P"+i);
        wasWorking = tag.getBoolean("WasWork");
    }

    @Override
    public Component getDisplayName() { return Component.translatable("block.recycler.recycler"); }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory pinv, Player player) {
        return new RecyclerMenu(id, pinv, this);
    }
}
