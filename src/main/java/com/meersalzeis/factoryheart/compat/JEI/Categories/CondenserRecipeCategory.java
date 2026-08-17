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
import com.meersalzeis.factoryheart.recipe.CondenserRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

public class CondenserRecipeCategory implements IRecipeCategory<CondenserRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "condensing");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "textures/gui/basic_gui.png");

    public static final RecipeType<CondenserRecipe> CONDENSER_RECIPE_TYPE =
            new RecipeType<>(UID, CondenserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private final int imageHeight = 50;
    private final int imageWidth = 114;

    public CondenserRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, imageWidth, imageHeight);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CONDENSER.get()));
    }

    @Override
    public RecipeType<CondenserRecipe> getRecipeType() {
        return CONDENSER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Condenser");
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
        return new ItemStack(ModBlocks.CONDENSER);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CondenserRecipe recipe, IFocusGroup focuses) {
        var generatingColor = new ItemStack(DyeItem.byColor(recipe.getHue()));
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 18).addItemStack(generatingColor);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 59, 18).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(CondenserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x = -45;
        int y = -16;
        
        int tier = recipe.requiredTier();
        var minecraft = Minecraft.getInstance();

        TierDisplay.renderTierDisplay(guiGraphics, tier, x, y);
        TierDisplay.renderTierTooltip(guiGraphics, mouseX, mouseY, x, y, minecraft.font, tier, false);
    }
}
