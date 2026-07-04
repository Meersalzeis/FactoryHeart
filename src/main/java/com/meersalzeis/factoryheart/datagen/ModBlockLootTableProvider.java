package com.meersalzeis.factoryheart.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.item.ModItems;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.BLAZER.get());
        dropSelf(ModBlocks.WRAPPER.get());
        dropSelf(ModBlocks.EXTRACTOR.get());
        dropSelf(ModBlocks.TESTER.get());

        dropSelf(ModBlocks.FACTORY_MAW.get());
        dropSelf(ModBlocks.FACTORY_VEIN.get());
        dropSelf(ModBlocks.FACTORY_HEART.get());
        
        dropSelf(ModBlocks.CRYSTALLIZER.get());

        dropSelf(ModBlocks.CONDENSER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
