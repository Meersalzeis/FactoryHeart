package com.meersalzeis.factoryheart.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;

public record TesterRecipe(Ingredient inputItem, ItemStack successItem, ItemStack failedItem, float successChance, int requiredTier) implements Recipe<TesterRecipeInput> {
    
    public static final MapCodec<TesterRecipe> CODEC =
        RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(TesterRecipe::inputItem),
                ItemStack.CODEC.fieldOf("success").forGetter(TesterRecipe::successItem),
                ItemStack.CODEC.fieldOf("failed").forGetter(TesterRecipe::failedItem),
                Codec.FLOAT.fieldOf("successChance").forGetter(TesterRecipe::successChance),
                Codec.INT.fieldOf("requiredTier").forGetter(TesterRecipe::requiredTier)
        ).apply(inst, TesterRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TesterRecipe> STREAM_CODEC =
        StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, TesterRecipe::inputItem,
                ItemStack.STREAM_CODEC, TesterRecipe::successItem,
                ItemStack.STREAM_CODEC, TesterRecipe::failedItem,
                ByteBufCodecs.FLOAT, TesterRecipe::successChance,
                ByteBufCodecs.INT, TesterRecipe::requiredTier,
                TesterRecipe::new);
    
    public ItemStack assembleFailed() {
        return failedItem.copy();
    }

    public ItemStack assembleSuccess() {
        return successItem.copy();
    }

    public float getSuccessChance() {
        return successChance;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(TesterRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return inputItem.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(TesterRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return successItem.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return successItem;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TESTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TESTER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TesterRecipe> {

        public static final MapCodec<TesterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(TesterRecipe::inputItem),
                ItemStack.CODEC.fieldOf("success").forGetter(TesterRecipe::successItem),
                ItemStack.CODEC.fieldOf("failed").forGetter(TesterRecipe::failedItem),
                Codec.FLOAT.fieldOf("successChance").forGetter(TesterRecipe::successChance),
                Codec.INT.fieldOf("requiredTier").forGetter(TesterRecipe::requiredTier)
        ).apply(inst, TesterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TesterRecipe> STREAM_CODEC =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, TesterRecipe::inputItem,
                ItemStack.STREAM_CODEC, TesterRecipe::successItem,
                ItemStack.STREAM_CODEC, TesterRecipe::failedItem,
                ByteBufCodecs.FLOAT, TesterRecipe::successChance,
                ByteBufCodecs.INT, TesterRecipe::requiredTier,
                TesterRecipe::new);

        @Override
        public MapCodec<TesterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TesterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
