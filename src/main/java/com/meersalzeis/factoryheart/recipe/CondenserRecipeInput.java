package com.meersalzeis.factoryheart.recipe;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CondenserRecipeInput(DyeColor hue) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        throw new IndexOutOfBoundsException("Condenser recipes do not use item inputs.");
    }

    @Override
    public int size() {
        return 0;
    }

    public DyeColor getHue() { return hue; }
}
