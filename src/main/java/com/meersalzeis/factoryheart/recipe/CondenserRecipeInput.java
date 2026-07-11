package com.meersalzeis.factoryheart.recipe;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CondenserRecipeInput(DyeColor hue) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return new ItemStack(DyeItem.byColor(getHue()),1);
    }

    @Override
    public int size() {
        return 1;
    }

    public DyeColor getHue() { return hue; }
}
