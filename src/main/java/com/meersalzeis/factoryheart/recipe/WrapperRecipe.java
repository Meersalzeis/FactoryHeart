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

public record WrapperRecipe(Ingredient centerpiece, Ingredient wrappings,  ItemStack output) implements Recipe<WrapperRecipeInput> {
    
    public static final MapCodec<WrapperRecipe> CODEC =
        RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("centerpiece").forGetter(WrapperRecipe::centerpiece),
                Ingredient.CODEC_NONEMPTY.fieldOf("wrappings").forGetter(WrapperRecipe::wrappings),
                ItemStack.CODEC.fieldOf("result").forGetter(WrapperRecipe::output)
        ).apply(inst, WrapperRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WrapperRecipe> STREAM_CODEC =
        StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, WrapperRecipe::centerpiece,
                Ingredient.CONTENTS_STREAM_CODEC, WrapperRecipe::wrappings,
                ItemStack.STREAM_CODEC, WrapperRecipe::output,
                WrapperRecipe::new);
    
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(centerpiece);
        list.add(wrappings);
        return list;
    }

    @Override
    public boolean matches(WrapperRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        return centerpiece.test(pInput.getItem(0)) && wrappings.test(pInput.getItem(1));
    }

    @Override
    public ItemStack assemble(WrapperRecipeInput pInput, HolderLookup.Provider pRegistries) {
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
        return ModRecipes.WRAPPER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.WRAPPER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<WrapperRecipe> {

        public static final MapCodec<WrapperRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("centerpiece").forGetter(WrapperRecipe::centerpiece),
            Ingredient.CODEC_NONEMPTY.fieldOf("wrappings").forGetter(WrapperRecipe::centerpiece),
            ItemStack.CODEC.fieldOf("result").forGetter(WrapperRecipe::output)
        ).apply(inst, WrapperRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WrapperRecipe> STREAM_CODEC =
            StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, WrapperRecipe::centerpiece,
                Ingredient.CONTENTS_STREAM_CODEC, WrapperRecipe::wrappings,
                ItemStack.STREAM_CODEC, WrapperRecipe::output,
                WrapperRecipe::new);

        @Override
        public MapCodec<WrapperRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WrapperRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
