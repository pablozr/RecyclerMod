package com.recycler.recyclermod.content.menu;

import com.recycler.recyclermod.content.blockentity.RecyclerBlockEntity;
import com.recycler.recyclermod.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class RecyclerMenu extends AbstractContainerMenu {
    private final RecyclerBlockEntity be;

    public static final int IN0=0, IN1=1, IN2=2, IN3=3, IN4=4;
    public static final int OUT0=5, OUT1=6, OUT2=7, OUT3=8, OUT4=9;

    private static final int CONTAINER_W = 176;
    private static final int INV_Y = 84, HOTBAR_Y = 142;

    private static final int COLS = 5;
    private static final int SLOT_FRAME = 18;
    private static final int GAP = 6;
    private static final int STEP = SLOT_FRAME + GAP;
    private static final int GRID_W = SLOT_FRAME + (COLS - 1) * STEP;
    private static final int BASE_X = (CONTAINER_W - GRID_W) / 2;

    private final int[] progressClient = new int[5];
    private int energyStoredClient;
    private int energyCapacityClient;

    private static int colX(int i){ return BASE_X + i * STEP; }

    private static final int IN_Y  = 15;
    private static final int V_GAP_Y = 24;
    private static final int OUT_Y = IN_Y + SLOT_FRAME + V_GAP_Y;

    public RecyclerMenu(int id, Inventory pinv, FriendlyByteBuf buf) {
        this(id, pinv, (RecyclerBlockEntity) pinv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public RecyclerMenu(int id, Inventory pinv, RecyclerBlockEntity be) {
        super(ModMenus.RECYCLER_MENU.get(), id);
        this.be = be;

        this.addSlot(new SlotItemHandler(be.getInventory(), IN0, colX(0), IN_Y));
        this.addSlot(new SlotItemHandler(be.getInventory(), IN1, colX(1), IN_Y));
        this.addSlot(new SlotItemHandler(be.getInventory(), IN2, colX(2), IN_Y));
        this.addSlot(new SlotItemHandler(be.getInventory(), IN3, colX(3), IN_Y));
        this.addSlot(new SlotItemHandler(be.getInventory(), IN4, colX(4), IN_Y));

        this.addSlot(new SlotItemHandler(be.getInventory(), OUT0, colX(0), OUT_Y) { @Override public boolean mayPlace(ItemStack s){ return false; }});
        this.addSlot(new SlotItemHandler(be.getInventory(), OUT1, colX(1), OUT_Y) { @Override public boolean mayPlace(ItemStack s){ return false; }});
        this.addSlot(new SlotItemHandler(be.getInventory(), OUT2, colX(2), OUT_Y) { @Override public boolean mayPlace(ItemStack s){ return false; }});
        this.addSlot(new SlotItemHandler(be.getInventory(), OUT3, colX(3), OUT_Y) { @Override public boolean mayPlace(ItemStack s){ return false; }});
        this.addSlot(new SlotItemHandler(be.getInventory(), OUT4, colX(4), OUT_Y) { @Override public boolean mayPlace(ItemStack s){ return false; }});

        for (int i=0;i<5;i++){
            final int idx = i;
            addDataSlot(new DataSlot() {
                @Override public int get() { return be.getProgress(idx); }
                @Override public void set(int v) { progressClient[idx] = v; }
            });
        }

        addDataSlot(new DataSlot() {
            @Override public int get() { return be.getEnergy().getEnergyStored(); }
            @Override public void set(int v) { energyStoredClient = v; }
        });
        addDataSlot(new DataSlot() {
            @Override public int get() { return be.getEnergy().getMaxEnergyStored(); }
            @Override public void set(int v) { energyCapacityClient = v; }
        });

        addPlayerInventory(pinv);
        addPlayerHotbar(pinv);
    }

    private void addPlayerInventory(Inventory pinv) {
        for (int r = 0; r < 3; ++r)
            for (int c = 0; c < 9; ++c)
                this.addSlot(new Slot(pinv, c + r * 9 + 9, 8 + c * 18, INV_Y + r * 18));
    }

    private void addPlayerHotbar(Inventory pinv) {
        for (int c = 0; c < 9; ++c)
            this.addSlot(new Slot(pinv, c, 8 + c * 18, HOTBAR_Y));
    }

    public int getEnergyStored() {
        if (this.be.getLevel() != null && !this.be.getLevel().isClientSide) return be.getEnergy().getEnergyStored();
        return energyStoredClient;
    }
    public int getEnergyCapacity() {
        if (this.be.getLevel() != null && !this.be.getLevel().isClientSide) return be.getEnergy().getMaxEnergyStored();
        return energyCapacityClient;
    }

    public int getProgress(int i) {
        if (this.be.getLevel() != null && !this.be.getLevel().isClientSide) return be.getProgress(i);
        return progressClient[i];
    }

    public int getMaxProgress(){
        return be.getMaxProgress();
    }

    public int getEnergyScaled(int pixelHeight) {
        int cap = Math.max(1, getEnergyCapacity());
        return Math.min(pixelHeight, (int)Math.round((getEnergyStored() / (double)cap) * pixelHeight));
    }

    @Override public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ret = stack.copy();

        final int MACHINE = 10;
        final int INV_START = MACHINE;
        final int INV_END   = INV_START + 27;
        final int HOT_START = INV_END;
        final int HOT_END   = HOT_START + 9;

        if (index < MACHINE) {
            if (!this.moveItemStackTo(stack, INV_START, HOT_END, true)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(stack, IN0, IN4 + 1, false)) {
                if (index < INV_END) {
                    if (!this.moveItemStackTo(stack, HOT_START, HOT_END, false)) return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(stack, INV_START, INV_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return ret;
    }
}
