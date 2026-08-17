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
import com.meersalzeis.factoryheart.recipe.TesterRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TesterRecipeCategory implements IRecipeCategory<TesterRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "testing");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,
            "textures/gui/tester/smol_tester_gui.png");

    public static final RecipeType<TesterRecipe> TESTER_RECIPE_TYPE =
            new RecipeType<>(UID, TesterRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public TesterRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 118, 69);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TESTER.get()));
    }

    @Override
    public RecipeType<TesterRecipe> getRecipeType() {
        return TESTER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Tester");
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
        return new ItemStack(ModBlocks.TESTER);
    }

    @Override
    public void draw(TesterRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        String chance = (recipe.getSuccessChance()*100) + "%";
        graphics.drawString(
            mc.font,
            chance,
            14, 12,
            0x000000,
            false
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TesterRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 14, 26).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 64, 12).addItemStack(recipe.assembleSuccess());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 64, 41).addItemStack(recipe.assembleFailed());
    }
}
