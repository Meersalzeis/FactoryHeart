package com.meersalzeis.factoryheart.util;

import com.meersalzeis.factoryheart.FHModMain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {
        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, name));
        }

        public static final TagKey<Block> HEART_NETWORK_BLOCKS = createTag("heart_network_blocks");
    }

    public static class Items {
        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, name));
        }

        public static final TagKey<Item> HEART_ITEMS = createTag("heart_items");
    }
}
