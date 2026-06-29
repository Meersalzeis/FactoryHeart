package com.meersalzeis.factoryheart.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/** Represents the specific input parameters/availability of a specific crafting station */
public record ExtractorRecipeInput(ItemStack input, int ingredientStock, int availableTier) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }

    public int getIngredientStock() {
        return ingredientStock;
    }

    public int getAvailableTier() {
        return availableTier;
    }
}
