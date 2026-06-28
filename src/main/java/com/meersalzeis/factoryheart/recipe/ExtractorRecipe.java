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

public record ExtractorRecipe(Ingredient inputItem, int requiredTier, ItemStack output) implements Recipe<ExtractorRecipeInput> {
    
    public static final MapCodec<ExtractorRecipe> CODEC =
        RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient")
                        .forGetter(ExtractorRecipe::inputItem),
                Codec.INT.fieldOf("requiredTier")
                        .forGetter(ExtractorRecipe::requiredTier),
                ItemStack.CODEC.fieldOf("result")
                        .forGetter(ExtractorRecipe::output)
        ).apply(inst, ExtractorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> STREAM_CODEC =
        StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ExtractorRecipe::inputItem,
                ByteBufCodecs.INT, ExtractorRecipe::requiredTier,
                ItemStack.STREAM_CODEC, ExtractorRecipe::output,
                ExtractorRecipe::new);
    
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(ExtractorRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return inputItem.test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(ExtractorRecipeInput pInput, HolderLookup.Provider pRegistries) {
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
        return ModRecipes.EXTRACTOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.EXTRACTOR_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ExtractorRecipe> {

        public static final MapCodec<ExtractorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ExtractorRecipe::inputItem),
            Codec.INT.fieldOf("requiredTier").forGetter(ExtractorRecipe::requiredTier),
            ItemStack.CODEC.fieldOf("result").forGetter(ExtractorRecipe::output)
        ).apply(inst, ExtractorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> STREAM_CODEC =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, ExtractorRecipe::inputItem,
                ByteBufCodecs.INT, ExtractorRecipe::requiredTier,
                ItemStack.STREAM_CODEC, ExtractorRecipe::output,
                ExtractorRecipe::new);

        @Override
        public MapCodec<ExtractorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExtractorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
