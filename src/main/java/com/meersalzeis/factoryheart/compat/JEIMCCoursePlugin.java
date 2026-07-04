package com.meersalzeis.factoryheart.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.recipe.BlazerRecipe;
import com.meersalzeis.factoryheart.recipe.CondenserRecipe;
import com.meersalzeis.factoryheart.recipe.CrystallizerRecipe;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipe;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.recipe.TesterRecipe;
import com.meersalzeis.factoryheart.recipe.WrapperRecipe;
import com.meersalzeis.factoryheart.gui.screens.BlazerScreen;
import com.meersalzeis.factoryheart.gui.screens.CondenserScreen;
import com.meersalzeis.factoryheart.gui.screens.CrystallizerScreen;
import com.meersalzeis.factoryheart.gui.screens.ExtractorScreen;
import com.meersalzeis.factoryheart.gui.screens.TesterScreen;
import com.meersalzeis.factoryheart.gui.screens.WrapperScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIMCCoursePlugin implements IModPlugin {
    
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrystallizerRecipeCategory( registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new BlazerRecipeCategory(    registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new WrapperRecipeCategory( registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ExtractorRecipeCategory( registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new TesterRecipeCategory( registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CondenserRecipeCategory( registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<CrystallizerRecipe> crystallizerRecipes = recipeManager.getAllRecipesFor(ModRecipes.CRYSTALLIZER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CrystallizerRecipeCategory.CRYSTALLIZER_RECIPE_RECIPE_TYPE, crystallizerRecipes);

        List<BlazerRecipe> blazerRecipes = recipeManager.getAllRecipesFor(ModRecipes.BLAZER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(BlazerRecipeCategory.BLAZER_RECIPE_RECIPE_TYPE, blazerRecipes);

        List<WrapperRecipe> wrapperRecipes = recipeManager.getAllRecipesFor(ModRecipes.WRAPPER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(WrapperRecipeCategory.WRAPPER_RECIPE_RECIPE_TYPE, wrapperRecipes);

        List<ExtractorRecipe> extractorRecipes = recipeManager.getAllRecipesFor(ModRecipes.EXTRACTOR_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ExtractorRecipeCategory.EXTRACTOR_RECIPE_RECIPE_TYPE, extractorRecipes);

        List<TesterRecipe> testerRecipes = recipeManager.getAllRecipesFor(ModRecipes.TESTER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(TesterRecipeCategory.TESTER_RECIPE_RECIPE_TYPE, testerRecipes);

        List<CondenserRecipe> condenserRecipes = recipeManager.getAllRecipesFor(ModRecipes.CONDENSER_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CondenserRecipeCategory.CONDENSER_RECIPE_RECIPE_TYPE, condenserRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
            CrystallizerScreen.class,
             70, 30, 25, 20,
            CrystallizerRecipeCategory.CRYSTALLIZER_RECIPE_RECIPE_TYPE);

        registration.addRecipeClickArea(
            BlazerScreen.class,
             70, 30, 25, 20,
            BlazerRecipeCategory.BLAZER_RECIPE_RECIPE_TYPE);
        
        registration.addRecipeClickArea(
            WrapperScreen.class,
             70, 30, 25, 20,
            WrapperRecipeCategory.WRAPPER_RECIPE_RECIPE_TYPE);
        
        registration.addRecipeClickArea(
            ExtractorScreen.class,
             70, 30, 25, 20,
            ExtractorRecipeCategory.EXTRACTOR_RECIPE_RECIPE_TYPE);
        
        registration.addRecipeClickArea(
            TesterScreen.class,
             70, 30, 25, 20,
            TesterRecipeCategory.TESTER_RECIPE_RECIPE_TYPE);

        registration.addRecipeClickArea(
            CondenserScreen.class,
             70, 30, 25, 20,
            CondenserRecipeCategory.CONDENSER_RECIPE_RECIPE_TYPE);
    }
}
