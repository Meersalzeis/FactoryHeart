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
import com.meersalzeis.factoryheart.gui.renderer.TierDisplay;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ExtractorRecipeCategory implements IRecipeCategory<ExtractorRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "extracting");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/basic_gui.png");

    public static final RecipeType<ExtractorRecipe> EXTRACTOR_RECIPE_TYPE =
            new RecipeType<>(UID, ExtractorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ExtractorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 114, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.EXTRACTOR.get()));
    }

    @Override
    public RecipeType<ExtractorRecipe> getRecipeType() {
        return EXTRACTOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Extractor");
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
        return new ItemStack(ModBlocks.EXTRACTOR);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ExtractorRecipe recipe, IFocusGroup focuses) {
        ItemStack input = recipe.getIngredients().get(0).getItems()[0];
        input.setCount(recipe.getIngredientCount());

        builder.addSlot(RecipeIngredientRole.INPUT, 9, 18).addItemStack(input);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 18).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(ExtractorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x = -45;
        int y = -16;
        
        int tier = recipe.requiredTier();
        var minecraft = Minecraft.getInstance();

        TierDisplay.renderTierDisplay(guiGraphics, tier, x, y);
        TierDisplay.renderTierTooltip(guiGraphics, mouseX, mouseY, x, y, minecraft.font, tier, false);
    }
}
