package com.meersalzeis.factoryheart.recipe;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.compat.JEI.Fuel.FuelInfoRecipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, FHModMain.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
        DeferredRegister.create(Registries.RECIPE_TYPE, FHModMain.MOD_ID);
    


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BlazerRecipe>> BLAZER_SERIALIZER =
        SERIALIZERS.register("blazing", BlazerRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BlazerRecipe>> BLAZER_TYPE =
        TYPES.register("blazing", () -> new RecipeType<BlazerRecipe>() {
            @Override
            public String toString() {
                return "blazing";
            }
        });


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WrapperRecipe>> WRAPPER_SERIALIZER =
        SERIALIZERS.register("wrapping", WrapperRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WrapperRecipe>> WRAPPER_TYPE =
        TYPES.register("wrapping", () -> new RecipeType<WrapperRecipe>() {
            @Override
            public String toString() {
                return "wrapping";
            }
        });


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExtractorRecipe>> EXTRACTOR_SERIALIZER =
        SERIALIZERS.register("extracting", ExtractorRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<ExtractorRecipe>> EXTRACTOR_TYPE =
        TYPES.register("extracting", () -> new RecipeType<ExtractorRecipe>() {
            @Override
            public String toString() {
                return "extracting";
            }
        });
    

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TesterRecipe>> TESTER_SERIALIZER =
        SERIALIZERS.register("testing", TesterRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<TesterRecipe>> TESTER_TYPE =
        TYPES.register("testing", () -> new RecipeType<TesterRecipe>() {
            @Override
            public String toString() {
                return "testing";
            }
        });


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CondenserRecipe>> CONDENSER_SERIALIZER =
        SERIALIZERS.register("condensing", CondenserRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<CondenserRecipe>> CONDENSER_TYPE =
        TYPES.register("condensing", () -> new RecipeType<CondenserRecipe>() {
            @Override
            public String toString() {
                return "condensing";
            }
        });
    


     public static final DeferredHolder<RecipeType<?>, RecipeType<FuelInfoRecipe>> FUELINFO_TYPE =
        TYPES.register("fuelinfo", () -> new RecipeType<FuelInfoRecipe>() {
            @Override
            public String toString() {
                return "fuelinfo";
            }
        });
}
