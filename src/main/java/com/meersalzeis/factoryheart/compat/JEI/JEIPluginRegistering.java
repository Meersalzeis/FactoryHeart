package com.meersalzeis.factoryheart.compat.JEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.compat.JEI.Categories.BlazerRecipeCategory;
import com.meersalzeis.factoryheart.compat.JEI.Categories.CondenserRecipeCategory;
import com.meersalzeis.factoryheart.compat.JEI.Categories.ExtractorRecipeCategory;
import com.meersalzeis.factoryheart.compat.JEI.Categories.TesterRecipeCategory;
import com.meersalzeis.factoryheart.compat.JEI.Categories.WrapperRecipeCategory;
import com.meersalzeis.factoryheart.compat.JEI.Fuel.FuelInfoRecipe;
import com.meersalzeis.factoryheart.compat.JEI.Fuel.FuelInfoRecipeCategory;
import com.meersalzeis.factoryheart.compat.JEI.recipeDisplays.CondenserRecipeScreen;
import com.meersalzeis.factoryheart.recipe.BlazerRecipe;
import com.meersalzeis.factoryheart.recipe.CondenserRecipe;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipe;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.recipe.TesterRecipe;
import com.meersalzeis.factoryheart.recipe.WrapperRecipe;
import com.meersalzeis.factoryheart.gui.screens.BlazerScreen;
import com.meersalzeis.factoryheart.gui.screens.ExtractorScreen;
import com.meersalzeis.factoryheart.gui.screens.TesterScreen;
import com.meersalzeis.factoryheart.gui.screens.WrapperScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIPluginRegistering implements IModPlugin {
    
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new BlazerRecipeCategory(guiHelper));
        registration.addRecipeCategories(new WrapperRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ExtractorRecipeCategory(guiHelper));
        registration.addRecipeCategories(new TesterRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CondenserRecipeCategory(guiHelper));
        
        registration.addRecipeCategories(new FuelInfoRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<BlazerRecipe> blazerRecipes = recipeManager.getAllRecipesFor(ModRecipes.BLAZER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(BlazerRecipeCategory.BLAZER_RECIPE_TYPE, blazerRecipes);

        List<WrapperRecipe> wrapperRecipes = recipeManager.getAllRecipesFor(ModRecipes.WRAPPER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(WrapperRecipeCategory.WRAPPER_RECIPE_TYPE, wrapperRecipes);

        List<ExtractorRecipe> extractorRecipes = recipeManager.getAllRecipesFor(ModRecipes.EXTRACTOR_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ExtractorRecipeCategory.EXTRACTOR_RECIPE_TYPE, extractorRecipes);

        List<TesterRecipe> testerRecipes = recipeManager.getAllRecipesFor(ModRecipes.TESTER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(TesterRecipeCategory.TESTER_RECIPE_TYPE, testerRecipes);

        List<CondenserRecipe> condenserRecipes = recipeManager.getAllRecipesFor(ModRecipes.CONDENSER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CondenserRecipeCategory.CONDENSER_RECIPE_TYPE, condenserRecipes);

        List<FuelInfoRecipe> fuelInfoRecipes = FuelInfoRecipeCategory.getAllFuelInfoRecipes();
        registration.addRecipes(FuelInfoRecipeCategory.FUELINFO_RECIPE_TYPE, fuelInfoRecipes );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

        registration.addRecipeClickArea(
            BlazerScreen.class,
             70, 30, 25, 20,
            BlazerRecipeCategory.BLAZER_RECIPE_TYPE);
        
        registration.addRecipeClickArea(
            WrapperScreen.class,
             70, 30, 25, 20,
            WrapperRecipeCategory.WRAPPER_RECIPE_TYPE);
        
        registration.addRecipeClickArea(
            ExtractorScreen.class,
             70, 30, 25, 20,
            ExtractorRecipeCategory.EXTRACTOR_RECIPE_TYPE);
        
        registration.addRecipeClickArea(
            TesterScreen.class,
             70, 30, 25, 20,
            TesterRecipeCategory.TESTER_RECIPE_TYPE);

        registration.addRecipeClickArea(
            CondenserRecipeScreen.class,
             70, 30, 25, 20,
            CondenserRecipeCategory.CONDENSER_RECIPE_TYPE);

        // registration.addRecipeClickArea(
        //     FuelInfoRecipeScreen.class,
        //      70, 30, 25, 20,
        //     FuelInfoRecipeCategory.FUELINFO_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BlazerRecipeCategory.getRecipeCatalyst(), BlazerRecipeCategory.BLAZER_RECIPE_TYPE);
        registration.addRecipeCatalyst(WrapperRecipeCategory.getRecipeCatalyst(), WrapperRecipeCategory.WRAPPER_RECIPE_TYPE);
        registration.addRecipeCatalyst(ExtractorRecipeCategory.getRecipeCatalyst(), ExtractorRecipeCategory.EXTRACTOR_RECIPE_TYPE);
        registration.addRecipeCatalyst(TesterRecipeCategory.getRecipeCatalyst(), TesterRecipeCategory.TESTER_RECIPE_TYPE);

        registration.addRecipeCatalyst(CondenserRecipeCategory.getRecipeCatalyst(), CondenserRecipeCategory.CONDENSER_RECIPE_TYPE);

        registration.addRecipeCatalyst(FuelInfoRecipeCategory.getRecipeCatalyst(), FuelInfoRecipeCategory.FUELINFO_RECIPE_TYPE);
    }
}
