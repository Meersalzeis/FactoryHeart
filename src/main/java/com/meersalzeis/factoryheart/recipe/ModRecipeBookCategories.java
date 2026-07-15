package com.meersalzeis.factoryheart.recipe;

import com.meersalzeis.factoryheart.FHModMain;

import net.minecraft.client.RecipeBookCategories;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

@EventBusSubscriber(modid = FHModMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModRecipeBookCategories {

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(
                ModRecipes.BLAZER_TYPE.get(),
                recipeHolder -> RecipeBookCategories.UNKNOWN
        );

        event.registerRecipeCategoryFinder(
                ModRecipes.WRAPPER_TYPE.get(),
                recipeHolder -> RecipeBookCategories.UNKNOWN
        );

        event.registerRecipeCategoryFinder(
                ModRecipes.EXTRACTOR_TYPE.get(),
                recipeHolder -> RecipeBookCategories.UNKNOWN
        );

        event.registerRecipeCategoryFinder(
                ModRecipes.TESTER_TYPE.get(),
                recipeHolder -> RecipeBookCategories.UNKNOWN
        );

        event.registerRecipeCategoryFinder(
                ModRecipes.CONDENSER_TYPE.get(),
                recipeHolder -> RecipeBookCategories.UNKNOWN
        );
    }
}