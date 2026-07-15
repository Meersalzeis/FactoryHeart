package com.meersalzeis.factoryheart.blockentity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SingleSlotFilteredHandler implements IItemHandler {

    private final int slotIndex;
    private final ItemStackHandler parent;

    private final Predicate<ItemStack> insertFilter;
    private final boolean allowExtract;

    public SingleSlotFilteredHandler(
            ItemStackHandler parent,
            int slotIndex,
            Predicate<ItemStack> insertFilter,
            boolean allowExtract
    ) {
        this.parent = parent;
        this.slotIndex = slotIndex;
        this.insertFilter = insertFilter;
        this.allowExtract = allowExtract;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return parent.getStackInSlot(slotIndex);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!insertFilter.test(stack)) {
            return stack;
        }
        return parent.insertItem(slotIndex, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!allowExtract) {
            return ItemStack.EMPTY;
        }
        return parent.extractItem(slotIndex, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return parent.getSlotLimit(slotIndex);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return insertFilter.test(stack);
    }
}