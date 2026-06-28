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

public record BlazerRecipe(Ingredient inputItem, int requiredTier, ItemStack output) implements Recipe<BlazerRecipeInput> {
    
    public static final MapCodec<BlazerRecipe> CODEC =
        RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient")
                        .forGetter(BlazerRecipe::inputItem),
                Codec.INT.fieldOf("requiredTier")
                        .forGetter(BlazerRecipe::requiredTier),
                ItemStack.CODEC.fieldOf("result")
                        .forGetter(BlazerRecipe::output)
        ).apply(inst, BlazerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlazerRecipe> STREAM_CODEC =
        StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, BlazerRecipe::inputItem,
                ByteBufCodecs.INT, BlazerRecipe::requiredTier,
                ItemStack.STREAM_CODEC, BlazerRecipe::output,
                BlazerRecipe::new);
    
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(BlazerRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return inputItem.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(BlazerRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BLAZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BLAZER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BlazerRecipe> {

        public static final MapCodec<BlazerRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(BlazerRecipe::inputItem),
            Codec.INT.fieldOf("requiredTier").forGetter(BlazerRecipe::requiredTier),
            ItemStack.CODEC.fieldOf("result").forGetter(BlazerRecipe::output)
        ).apply(inst, BlazerRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BlazerRecipe> STREAM_CODEC =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, BlazerRecipe::inputItem,
                ByteBufCodecs.INT, BlazerRecipe::requiredTier,
                ItemStack.STREAM_CODEC, BlazerRecipe::output,
                BlazerRecipe::new);

        @Override
        public MapCodec<BlazerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlazerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
