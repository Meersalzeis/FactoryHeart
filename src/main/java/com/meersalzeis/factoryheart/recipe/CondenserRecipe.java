package com.meersalzeis.factoryheart.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import com.meersalzeis.factoryheart.FHModClient;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;

public record CondenserRecipe(DyeColor hue, int requiredTier, int requiredTicks, int requiredFuelPerTick, ItemStack output) implements Recipe<CondenserRecipeInput> {
    
    public static final MapCodec<CondenserRecipe> CODEC =
        RecordCodecBuilder.mapCodec(inst -> inst.group(
                DyeColor.CODEC.fieldOf("hue").forGetter(CondenserRecipe::hue),
                Codec.INT.fieldOf("requiredTier").forGetter(CondenserRecipe::requiredTier),
                Codec.INT.fieldOf("requiredTicks").forGetter(CondenserRecipe::requiredTicks),
                Codec.INT.fieldOf("requiredFuelPerTick").forGetter(CondenserRecipe::requiredFuelPerTick),
                ItemStack.CODEC.fieldOf("result").forGetter(CondenserRecipe::output)
        ).apply(inst, CondenserRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> STREAM_CODEC =
        StreamCodec.composite(
                DyeColor.STREAM_CODEC, CondenserRecipe::hue,
                ByteBufCodecs.INT, CondenserRecipe::requiredTier,
                ByteBufCodecs.INT, CondenserRecipe::requiredTier,
                ByteBufCodecs.INT, CondenserRecipe::requiredTier,
                ItemStack.STREAM_CODEC, CondenserRecipe::output,
                CondenserRecipe::new);
    
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        return list;
    }

    @Override
    public boolean matches(CondenserRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }

        FHModClient.debugMessageToAll("Comparing hues: this is"+hue+" against others "+pInput.getHue(), false);
        return hue.equals(pInput.getHue());
    }

    @Override
    public ItemStack assemble(CondenserRecipeInput pInput, HolderLookup.Provider pRegistries) {
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
        return ModRecipes.CONDENSER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CONDENSER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CondenserRecipe> {

        public static final MapCodec<CondenserRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            DyeColor.CODEC.fieldOf("hue").forGetter(CondenserRecipe::hue),
            Codec.INT.fieldOf("requiredTier").forGetter(CondenserRecipe::requiredTier),
            Codec.INT.fieldOf("requiredTicks").forGetter(CondenserRecipe::requiredTicks),
            Codec.INT.fieldOf("requiredFuelPerTick").forGetter(CondenserRecipe::requiredFuelPerTick),
            ItemStack.CODEC.fieldOf("result").forGetter(CondenserRecipe::output)
        ).apply(inst, CondenserRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> STREAM_CODEC =
            StreamCodec.composite(
                DyeColor.STREAM_CODEC, CondenserRecipe::hue,
                ByteBufCodecs.INT, CondenserRecipe::requiredTier,
                ByteBufCodecs.INT, CondenserRecipe::requiredTicks,
                ByteBufCodecs.INT, CondenserRecipe::requiredFuelPerTick,
                ItemStack.STREAM_CODEC, CondenserRecipe::output,
                CondenserRecipe::new
            );

        @Override
        public MapCodec<CondenserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CondenserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
