package com.recycler.recyclermod.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class IoOrderedHandler implements IItemHandler {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private final ItemStackHandler inv;
    public IoOrderedHandler(ItemStackHandler inv) { this.inv = inv; }

    @Override public int getSlots() { return inv.getSlots(); }
    @Override public @NotNull ItemStack getStackInSlot(int slot) { return inv.getStackInSlot(slot); }
    @Override public int getSlotLimit(int slot) { return inv.getSlotLimit(slot); }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot == INPUT_SLOT;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot != INPUT_SLOT) return stack;
        return inv.insertItem(INPUT_SLOT, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != OUTPUT_SLOT) return ItemStack.EMPTY;
        return inv.extractItem(OUTPUT_SLOT, amount, simulate);
    }
}
