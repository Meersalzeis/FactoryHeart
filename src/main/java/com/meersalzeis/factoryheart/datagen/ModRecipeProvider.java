package com.meersalzeis.factoryheart.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.util.ModTags;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FACTORY_TOOTH.get())
            .pattern(" i ")
            .pattern("ibi")
            .pattern(" b ")
            .define('i', Items.IRON_NUGGET)
            .define('b', Items.BONE_MEAL)
            .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
            .unlockedBy("has_bone_meal", has(Items.BONE_MEAL))
            .save(pRecipeOutput);
        
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FACTORY_PASTE.get(), 4)
            .requires(Items.REDSTONE, 4)
            .requires(Items.GLOW_INK_SAC)
            .requires(Items.CLAY_BALL)
            .unlockedBy("has_redstone", has(Items.REDSTONE))
            .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FACTORY_SKIN.get(), 4)
            .pattern("nmn")
            .pattern("mpm")
            .pattern("nmn")
            .define('n', Tags.Items.NUGGETS)
            .define('m', Tags.Items.INGOTS)
            .define('p', ModItems.FACTORY_PASTE)
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLAZER.get())
            .pattern("srs")
            .pattern("rbr")
            .pattern("srs")
            .define('r', Items.BLAZE_ROD)
            .define('b', Blocks.BARREL)
            .define('s', ModBlocks.FACTORY_SKIN)
            .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .unlockedBy("has_factory_skin", has(ModBlocks.FACTORY_SKIN))
            .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.WRAPPER.get())
                .pattern("s s")
                .pattern("h h")
                .pattern("sps")
                .define('s', ModBlocks.FACTORY_SKIN)
                .define('h', Items.TRIPWIRE_HOOK)
                .define('p', Blocks.PISTON)
                .unlockedBy("has_factory_tooth", has(ModItems.FACTORY_TOOTH))
                .unlockedBy("has_hopper", has(Blocks.HOPPER))
                .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLUNGER.get())
            .pattern(" lr")
            .pattern(" ll")
            .pattern("s  ")
            .define('s', Items.STICK)
            .define('l', Items.LEATHER)
            .define('r', Items.RED_DYE)
            .unlockedBy("has_leather", has(Items.LEATHER))
            .unlockedBy("has_wrapper", has(ModBlocks.WRAPPER))
            .unlockedBy("has_red_dye", has(Items.RED_DYE))
            .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.EXTRACTOR.get())
            .pattern("sss")
            .pattern(" pm")
            .pattern("sds")
            .define('p', ModItems.PLUNGER)
            .define('m', Blocks.PISTON)
            .define('d', Blocks.DISPENSER)
            .define('s', ModBlocks.FACTORY_SKIN)
            .unlockedBy("has_piston", has(Blocks.PISTON))
            .unlockedBy("has_dispenser", has(Blocks.DISPENSER))
            .unlockedBy("has_factory_skin", has(ModBlocks.FACTORY_SKIN))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TESTER.get())
            .pattern("sds")
            .pattern("o o")
            .pattern("sls")
            .define('d', Blocks.DISPENSER)
            .define('o', Blocks.OBSERVER)
            .define('l', Blocks.DAYLIGHT_DETECTOR)
            .define('s', ModBlocks.FACTORY_SKIN)
            .unlockedBy("has_observer", has(Blocks.OBSERVER))
            .unlockedBy("has_daylight_detector", has(Blocks.DAYLIGHT_DETECTOR))
            .unlockedBy("has_factory_skin", has(ModBlocks.FACTORY_SKIN))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CONDENSER.get())
            .pattern("sps")
            .pattern("dhb")
            .pattern("sps")
            .define('d', Blocks.DISPENSER)
            .define('h', ModItems.SMART_HEART)
            .define('b', Blocks.IRON_BARS)
            .define('p', ModItems.HARD_PART)
            .define('s', ModBlocks.FACTORY_SKIN)
            .unlockedBy("has_hard_part", has(ModItems.HARD_PART))
            .unlockedBy("has_smart_heart", has(ModItems.SMART_HEART))
            .unlockedBy("has_factory_skin", has(ModBlocks.FACTORY_SKIN))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FACTORY_MAW.get())
            .pattern("ttt")
            .pattern(" h ")
            .pattern("ttt")
            .define('t', ModItems.FACTORY_TOOTH)
            .define('h', Blocks.HOPPER)
            .unlockedBy("has_factory_tooth", has(ModItems.FACTORY_TOOTH))
            .unlockedBy("has_hopper", has(Blocks.HOPPER))
            .save(pRecipeOutput);
    
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FACTORY_VEIN.get(), 6)
            .pattern("sss")
            .define('s', ModBlocks.FACTORY_SKIN)
            .unlockedBy("has_factory_skin", has(ModBlocks.FACTORY_SKIN))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FACTORY_HEART.get())
            .pattern("sps")
            .pattern("php")
            .pattern("sps")
            .define('s', ModBlocks.FACTORY_SKIN)
            .define('p', ModItems.FACTORY_PASTE)
            .define('h', ModTags.Items.HEART_ITEMS)
            .unlockedBy("has_factory_skin", has(ModBlocks.FACTORY_SKIN))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .unlockedBy("has_heart_of_the_sea", has(Items.HEART_OF_THE_SEA))
            .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.INK_SAC)
            .requires(Items.BLACK_DYE)
            .requires(Items.BLACK_DYE)
            .requires(Items.BLACK_DYE)
            .requires(Items.BLACK_DYE)
            .requires(Items.WATER_BUCKET)
            .unlockedBy("has_black_dye", has(Items.BLACK_DYE))
            .save(pRecipeOutput);
            
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CONTAINED_DEEPCOOL_CRYSTAL)
            .pattern("pcp")
            .pattern("pdp")
            .pattern("pcp")
            .define('p', Items.GLASS_PANE)
            .define('c', ModItems.CONTAINMENT_FIELD_PROJECTOR)
            .define('d', ModItems.DEEPCOOL_CRYSTAL)
            .unlockedBy("has_containment_filed_projector", has(ModItems.CONTAINMENT_FIELD_PROJECTOR))
            .unlockedBy("has_deepcool_crystal", has(ModItems.DEEPCOOL_CRYSTAL))
            .save(pRecipeOutput);
        
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CONTAINED_DEEPCOOL_CRYSTAL)
            .requires(ModItems.CONTAINMENT_CASING)
            .requires(ModItems.DEEPCOOL_CRYSTAL)
            .unlockedBy("has_containment_casing", has(ModItems.CONTAINMENT_CASING))
            .unlockedBy("has_deepcool_crystal", has(ModItems.DEEPCOOL_CRYSTAL))
            .save(pRecipeOutput, "contained_deepcool_crystal_shapeless");
            //.save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLAZEAR_PELLET, 6)
            .pattern("efe")
            .pattern("gbg")
            .pattern("efe")
            .define('e', ModItems.ENERGY_POWDER)
            .define('f', ModItems.FACTORY_PASTE)
            .define('g', Items.GLOWSTONE_DUST)
            .define('b', Items.BLAZE_POWDER)
            .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
            .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SODA_POWDER.get(), 4)
            .requires(ModItems.ENERGY_POWDER)
            .requires(Items.NETHER_WART)
            .requires(Items.SUGAR, 4)
            .requires(Items.SWEET_BERRIES)
            .requires(Items.GLOW_BERRIES)
            .unlockedBy("has_energy_powder", has(ModItems.ENERGY_POWDER))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CONTAINMENT_FIELD_PROJECTOR)
            .pattern("c  ")
            .pattern("gc ")
            .pattern("rgc")
            .define('c', ModItems.CONTAINMENT_SPOOL)
            .define('g', Items.GOLD_INGOT)
            .define('r', Items.REDSTONE)
            .unlockedBy("has_containment_spool", has(ModItems.CONTAINMENT_SPOOL))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.METAL_SHEET, 3)
            .pattern("ii")
            .pattern("ii")
            .define('i', Tags.Items.INGOTS)
            .unlockedBy("has_wrapper", has(ModBlocks.WRAPPER))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.METAL_DISC)
            .pattern(" n ")
            .pattern("nnn")
            .pattern(" n ")
            .define('n', Tags.Items.NUGGETS)
            .unlockedBy("has_wrapper", has(ModBlocks.WRAPPER))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RAW_BLAZE_CANDY)
            .pattern("shs")
            .pattern("pcm")
            .pattern("shs")
            .define('s', Items.SUGAR)
            .define('h', Items.HONEY_BOTTLE)
            .define('p', Items.PUMPKIN_PIE)
            .define('m', Blocks.MELON)
            .define('c', ModItems.COMBUSTION_CORE)
            .unlockedBy("has_combustion_core", has(ModItems.COMBUSTION_CORE))
            .save(pRecipeOutput);
        
        // ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLAZEAR_FUEL_ROD, 3)
        //     .pattern("nvn")
        //     .pattern(" v ")
        //     .pattern("nvn")
        //     .define('n', Tags.Items.NUGGETS)
        //     .define('v', ModItems.VOLATILE_BLAZEAR_ROD)
        //     .unlockedBy("has_volatile_blazear_rod", has(ModItems.VOLATILE_BLAZEAR_ROD))
        //     .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLAZEAR_ROD)
            .pattern("ebe")
            .pattern("pbp")
            .pattern("ebe")
            .define('e', ModItems.ENERGY_POWDER)
            .define('b', ModItems.BLAZEAR_PELLET)
            .define('p', ModItems.FACTORY_PASTE)
            .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
            .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ACTIVATED_BLAZEAR_ROD)
            .requires(ModItems.BLAZEAR_ROD)
            .requires(ModItems.ENERGY_DRINK)
            .unlockedBy("has_activated_blazear_rod", has(ModItems.BLAZEAR_ROD))
            .unlockedBy("has_energy_drink", has(ModItems.ENERGY_DRINK))
            .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CONTAINMENT_CASING)
            .pattern("ncn")
            .pattern("p p")
            .pattern("ncn")
            .define('p', Items.GLASS_PANE)
            .define('n', Items.IRON_NUGGET)
            .define('c', ModItems.CONTAINMENT_FIELD_PROJECTOR)
            .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
            .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
            .unlockedBy("has_containment_filed_projector", has(ModItems.CONTAINMENT_FIELD_PROJECTOR))
            .save(pRecipeOutput);
        
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFERNO_SHARD)
            .requires(ModItems.BLAZEAR_FUEL_ROD)
            .requires(ModItems.BLAZEAR_FUEL_ROD)
            .requires(ModItems.BLAZE_CANDY)
            .requires(ModItems.BLAZE_CANDY)
            .requires(ModItems.BLAZE_CANDY)
            .requires(ModItems.BLAZE_CANDY)
            .requires(Items.FIRE_CHARGE)
            .unlockedBy("has_blaze_candy", has(ModItems.BLAZE_CANDY))
            .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INFERNO_MATTER)
            .requires(ModItems.CONTAINMENT_CASING)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .requires(ModItems.INFERNO_SHARD)
            .unlockedBy("has_containment_casing", has(ModItems.CONTAINMENT_CASING))
            .unlockedBy("has_inferno_shard", has(ModItems.INFERNO_SHARD))
            .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ABZERO_MATTER)
            .requires(ModItems.CONTAINMENT_CASING)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .requires(ModItems.ABZERO_SHARD)
            .unlockedBy("has_containment_casing", has(ModItems.CONTAINMENT_CASING))
            .unlockedBy("has_abzero_shard", has(ModItems.ABZERO_SHARD))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HARD_PART_U)
            .pattern("o")
            .pattern("n")
            .pattern("i")
            .define('o', Blocks.OBSIDIAN)
            .define('n', Items.NETHERITE_SCRAP)
            .define('i', Items.IRON_INGOT)
            .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
            .unlockedBy("has_tester", has(ModBlocks.TESTER))
            .save(pRecipeOutput);

        oreSmelting(pRecipeOutput, List.of(ModItems.HARD_PART_F), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 0.25f, 200, getName());
        oreBlasting(pRecipeOutput, List.of(ModItems.HARD_PART_F), RecipeCategory.MISC, Items.NETHERITE_SCRAP, 0.25f, 100, getName());
        oreSmelting(pRecipeOutput, List.of(ModItems.LEATHERY_ROTTEN), RecipeCategory.MISC, Items.LEATHER, 0.1f, 200, getName());
        oreBlasting(pRecipeOutput, List.of(ModItems.LEATHERY_ROTTEN), RecipeCategory.MISC, Items.LEATHER, 0.1f, 100, getName());
    
        energyDrinkRecipe(pRecipeOutput);
    
    }

    private static void energyDrinkRecipe(RecipeOutput pRecipeOutput) {
        ItemStack strengthPotion = PotionContents.createItemStack(
            Items.POTION,
            BuiltInRegistries.POTION.getHolderOrThrow(Potions.STRENGTH.getKey())
        );
        Ingredient strengthPotionIngr = DataComponentIngredient.of(
                false,
                strengthPotion
        );

        ItemStack speedPotion = PotionContents.createItemStack(
            Items.POTION,
            BuiltInRegistries.POTION.getHolderOrThrow(Potions.SWIFTNESS.getKey())
        );
        Ingredient speedPotionIngr = DataComponentIngredient.of(
                false,
                speedPotion
        );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ENERGY_DRINK, 6)
            .pattern("msf")
            .pattern("eee")
            .pattern("eee")
            .define('e', ModItems.EMPTY_CAN) // m for muscle
            .define('m', strengthPotionIngr)
            .define('f', speedPotionIngr) // f for fast
            .define('s', ModItems.SODA_POWDER)
            .unlockedBy("has_empty_can", has(ModItems.EMPTY_CAN))
            .unlockedBy("has_soda_pwoder", has(ModItems.SODA_POWDER))
            .save(pRecipeOutput);
    }

    protected static void oreSmelting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }
}
