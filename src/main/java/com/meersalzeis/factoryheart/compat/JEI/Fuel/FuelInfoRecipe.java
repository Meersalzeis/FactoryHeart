package com.meersalzeis.factoryheart.compat.JEI.Fuel;

import com.meersalzeis.factoryheart.recipe.ModRecipes;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record FuelInfoRecipe(TagKey<Item> fuels, int tier, TagKey<Item> coolants) implements Recipe<FuelInfoRecipeInput> {

    @Override
    public boolean matches(FuelInfoRecipeInput input, Level level) {
        return this.tier == input.getTier();
    }
    @Override
    public ItemStack assemble(FuelInfoRecipeInput input, Provider registries) {
        throw new UnsupportedOperationException("Unimplemented method 'assemble'");
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        throw new UnsupportedOperationException("Unimplemented method 'canCraftInDimensions'");
    }
    @Override
    public ItemStack getResultItem(Provider registries) {
        throw new UnsupportedOperationException("Unimplemented method 'getResultItem'");
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        throw new UnsupportedOperationException("Unimplemented method 'getSerializer'");
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FUELINFO_TYPE.get();
    }
}