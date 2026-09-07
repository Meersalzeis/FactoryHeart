package com.meersalzeis.factoryheart.compat.JEI.Fuel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FuelInfoRecipeInput(int tier) implements RecipeInput {
    
    @Override
    public ItemStack getItem(int pIndex) {
        return null;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public int size() {
        return 1;
    }
}