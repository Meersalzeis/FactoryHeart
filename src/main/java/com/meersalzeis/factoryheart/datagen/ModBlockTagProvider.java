package com.meersalzeis.factoryheart.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FHModMain.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.FACTORY_VEIN.get())
            .add(ModBlocks.FACTORY_SKIN.get())
            .add(ModBlocks.FACTORY_MAW.get())
            .add(ModBlocks.FACTORY_HEART.get())
            .add(ModBlocks.BLAZER.get())
            .add(ModBlocks.WRAPPER.get())
            .add(ModBlocks.EXTRACTOR.get())
            .add(ModBlocks.TESTER.get())
            .add(ModBlocks.CONDENSER.get());

        // this.tag(BlockTags.NEEDS_IRON_TOOL)
        //     .add(ModBlocks.RELICSTEEL_BLOCK.get());

        this.tag(ModTags.Blocks.HEART_NETWORK_BLOCKS)
            .add(ModBlocks.FACTORY_VEIN.get())
            .add(ModBlocks.FACTORY_SKIN.get())
            
            .add(ModBlocks.FACTORY_MAW.get())
            .add(ModBlocks.FACTORY_HEART.get())

            .add(ModBlocks.BLAZER.get())
            .add(ModBlocks.WRAPPER.get())
            .add(ModBlocks.EXTRACTOR.get())
            .add(ModBlocks.TESTER.get())

            .add(ModBlocks.CONDENSER.get());
    }
}
