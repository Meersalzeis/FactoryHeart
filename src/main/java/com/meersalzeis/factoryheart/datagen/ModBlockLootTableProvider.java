package com.meersalzeis.factoryheart.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

import com.meersalzeis.factoryheart.block.ModBlocks;

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
        dropSelf(ModBlocks.FACTORY_SKIN.get());
        dropSelf(ModBlocks.FACTORY_HEART.get());

        dropSelf(ModBlocks.CONDENSER.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
