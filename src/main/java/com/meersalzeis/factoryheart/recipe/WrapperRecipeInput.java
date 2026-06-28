package com.meersalzeis.factoryheart.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record WrapperRecipeInput(ItemStack centerpiece, ItemStack wrapping) implements RecipeInput {
    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> centerpiece;
            case 1 -> wrapping;
            default -> throw new IllegalArgumentException("No slot " + slot);
        };
    }

    @Override
    public int size() {
        return 2;
    }
}
