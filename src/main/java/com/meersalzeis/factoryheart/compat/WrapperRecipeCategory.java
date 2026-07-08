package com.meersalzeis.factoryheart.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.recipe.WrapperRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WrapperRecipeCategory implements IRecipeCategory<WrapperRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "wrapping");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,
            "textures/gui/wrapper/wrapper_gui.png");

    public static final RecipeType<WrapperRecipe> WRAPPER_RECIPE_TYPE =
            new RecipeType<>(UID, WrapperRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public WrapperRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WRAPPER.get()));
    }

    @Override
    public RecipeType<WrapperRecipe> getRecipeType() {
        return WRAPPER_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Wrapper");
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
        return new ItemStack(ModBlocks.WRAPPER);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WrapperRecipe recipe, IFocusGroup focuses) {
        ItemStack centerpiece = recipe.getIngredients().get(0).getItems()[0];
        centerpiece.setCount(recipe.getCenterpieceCount());

        ItemStack wrappings = recipe.getIngredients().get(1).getItems()[0];
        wrappings.setCount(recipe.getWrappingsCount());

        builder.addSlot(RecipeIngredientRole.INPUT, 54, 34).addItemStack(centerpiece);
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 34).addItemStack(wrappings);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 34).addItemStack(recipe.getResultItem(null));
    }
}
