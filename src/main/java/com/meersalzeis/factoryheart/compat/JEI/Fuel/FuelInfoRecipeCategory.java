package com.meersalzeis.factoryheart.compat.JEI.Fuel;

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
import com.meersalzeis.factoryheart.util.ModTags;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class FuelInfoRecipeCategory implements IRecipeCategory<FuelInfoRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, "fuelinfo");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/fuel/fuel_gui.png");

    public static final RecipeType<FuelInfoRecipe> FUELINFO_RECIPE_TYPE = new RecipeType<>(UID, FuelInfoRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public FuelInfoRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 114, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FACTORY_HEART.get()));
    }

    @Override
    public Component getTitle() {
        return Component.literal("Fuels");
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
        return new ItemStack(ModBlocks.FACTORY_HEART);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FuelInfoRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 18).addIngredients(Ingredient.of(recipe.fuels()));
        if (!hasAny(recipe.coolants())) {return;}
        builder.addSlot(RecipeIngredientRole.INPUT, 89, 18).addIngredients(Ingredient.of(recipe.coolants()));
    }

    private static boolean hasAny(TagKey<Item> tagKey) {
        boolean hasItem = false;
        Iterable<Holder<Item>> allContent = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getTagOrEmpty(tagKey);
        for (Holder<Item> cur : allContent) {
            hasItem = true;
            break;
        }
        return hasItem;
    }

    @Override
    public void draw(FuelInfoRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        DisplayHelper.renderTierDisplay(graphics, recipe.tier(), -85, -16);
        DisplayHelper.renderTierTooltip(graphics, mouseX, mouseY, -85, -16, recipe.tier(), true);

        Minecraft mc = Minecraft.getInstance();
        graphics.drawString(mc.font, "fuel:", 7, 6, 0x000000, false);
        graphics.drawString(mc.font, "coolant:", 70, 6, 0x000000, false);
    }

    @Override
    public RecipeType<FuelInfoRecipe> getRecipeType() {
        return FUELINFO_RECIPE_TYPE;
    }

    public static List<FuelInfoRecipe> getAllFuelInfoRecipes() {
        List<FuelInfoRecipe> fuels = List.of(
                new FuelInfoRecipe(ModTags.Items.T1_FUEL_ITEMS, 1, ModTags.Items.T1_COOL_ITEMS),
                new FuelInfoRecipe(ModTags.Items.T2_FUEL_ITEMS, 2, ModTags.Items.T2_COOL_ITEMS),
                new FuelInfoRecipe(ModTags.Items.T3_FUEL_ITEMS, 3, ModTags.Items.T3_COOL_ITEMS),
                new FuelInfoRecipe(ModTags.Items.T4_FUEL_ITEMS, 4, ModTags.Items.T4_COOL_ITEMS)
        );

        return fuels;
    }
}
