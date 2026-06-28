package com.meersalzeis.factoryheart.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.data.tags.TagsProvider;
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

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.item.ModItems;

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
        
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BLAZER.get())
            .pattern("prp")
            .pattern("rbr")
            .pattern("prp")
            .define('r', Items.BLAZE_ROD)
            .define('b', Blocks.BARREL)
            .define('p', ModItems.FACTORY_PASTE)
            .unlockedBy("has_blaze_rod", has(Items.BLAZE_ROD))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.WRAPPER.get())
                .pattern("fif")
                .pattern("h h")
                .pattern("fpf")
                .define('i', Items.IRON_INGOT)
                .define('f', ModItems.FACTORY_PASTE)
                .define('h', Items.TRIPWIRE_HOOK)
                .define('p', Blocks.PISTON)
                .unlockedBy("has_factory_tooth", has(ModItems.FACTORY_TOOTH))
                .unlockedBy("has_hopper", has(Blocks.HOPPER))
                .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.EXTRACTOR.get())
            .pattern("d d")
            .pattern("fpf")
            .define('p', Blocks.PISTON)
            .define('d', Blocks.DISPENSER)
            .define('f', ModItems.FACTORY_PASTE)
            .unlockedBy("has_piston", has(Blocks.PISTON))
            .unlockedBy("has_dispenser", has(Blocks.DISPENSER))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TESTER.get())
            .pattern("fpf")
            .pattern("d d")
            .pattern("fpf")
            .define('p', Blocks.PISTON)
            .define('d', Blocks.DISPENSER)
            .define('f', ModItems.FACTORY_PASTE)
            .unlockedBy("has_piston", has(Blocks.PISTON))
            .unlockedBy("has_dispenser", has(Blocks.DISPENSER))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
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
            .pattern("iii")
            .pattern("ppp")
            .pattern("iii")
            .define('i', Items.IRON_NUGGET)
            .define('p', ModItems.FACTORY_PASTE)
            .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .save(pRecipeOutput);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FACTORY_HEART.get())
            .pattern("ipi")
            .pattern("php")
            .pattern("ipi")
            .define('i', Items.IRON_NUGGET)
            .define('p', ModItems.FACTORY_PASTE)
            .define('h', Items.HEART_OF_THE_SEA)
            .unlockedBy("has_iron_nugget", has(Items.IRON_NUGGET))
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .unlockedBy("has_heart_of_the_sea", has(Items.HEART_OF_THE_SEA))
            .save(pRecipeOutput);

            
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_WIRE, 6)
            .pattern("ccc")
            .define('c', Items.COPPER_INGOT)
            .unlockedBy("has_factory_paste", has(ModItems.FACTORY_PASTE))
            .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
            .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FACTORY_PASTE.get(), 4)
            .requires(Items.REDSTONE, 4)
            .requires(Items.GLOW_INK_SAC)
            .requires(Items.CLAY_BALL)
            .unlockedBy("has_redstone", has(Items.REDSTONE))
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
        
         ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GLOW_INK_SAC)
            .requires(Items.INK_SAC)
            .requires(Items.GLOWSTONE_DUST)
            .unlockedBy("has_ink_sac", has(Items.INK_SAC))
            .unlockedBy("has_glowstone_dust", has(Items.GLOWSTONE_DUST))
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
