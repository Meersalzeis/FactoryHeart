package com.meersalzeis.factoryheart.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                              CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, FHModMain.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Items.HEART_ITEMS)
            .add(ModItems.SMART_HEART.get())
            .add(Items.HEART_OF_THE_SEA);
        

        tag(ModTags.Items.T1_FUEL_ITEMS)
            .add(Items.COAL)
            .add(Items.CHARCOAL)
            .add(Items.DRIED_KELP_BLOCK);
        
        tag(ModTags.Items.T2_FUEL_ITEMS)
            .add(ModItems.BLAZEAR_FUEL_ROD.get());
        
        tag(ModTags.Items.T3_FUEL_ITEMS)
            .add(ModItems.BLAZE_CANDY.get());

        tag(ModTags.Items.T4_FUEL_ITEMS)
            .add(ModItems.INFERNO_MATTER.get());
        
        // Nothing in ModTags.Items.T1_COOL_ITEMS - it's only there for datapacks
        
        tag(ModTags.Items.T2_COOL_ITEMS)
            .add(Items.SNOWBALL);
        
        tag(ModTags.Items.T3_COOL_ITEMS)
            .add(ModItems.CONTAINED_DEEPCOOL_CRYSTAL.get());

        tag(ModTags.Items.T4_COOL_ITEMS)
            .add(ModItems.ABZERO_MATTER.get());
    }
}
