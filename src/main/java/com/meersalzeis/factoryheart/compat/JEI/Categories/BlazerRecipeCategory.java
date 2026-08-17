package com.meersalzeis.factoryheart.compat.JEI.Categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.gui.renderer.DisplayHelper;
import com.meersalzeis.factoryheart.recipe.BlazerRecipe;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BlazerRecipeCategory implements IRecipeCategory<BlazerRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "blazing");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/basic_gui.png");

    public static final RecipeType<BlazerRecipe> BLAZER_RECIPE_TYPE =
            new RecipeType<>(UID, BlazerRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public BlazerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 114, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BLAZER.get()));
    }

    @Override
    public RecipeType<BlazerRecipe> getRecipeType() {
        return BLAZER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Blazer");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    // Means which blocks/things are used as crafting station
    public static ItemStack getRecipeCatalyst() {
        return new ItemStack(ModBlocks.BLAZER);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlazerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 18).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 18).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(BlazerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x = -45;
        int y = -16;
        
        int tier = recipe.requiredTier();

        DisplayHelper.renderTierDisplay(guiGraphics, tier, x, y);
        DisplayHelper.renderTierTooltip(guiGraphics, mouseX, mouseY, x, y, tier, false);
    }
}
