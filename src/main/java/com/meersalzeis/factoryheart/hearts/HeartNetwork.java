package com.meersalzeis.factoryheart.hearts;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.meersalzeis.factoryheart.Config;
import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.hearting.FactoryHeartBlock;
import com.meersalzeis.factoryheart.util.ModTags;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HeartNetwork {

    public BlockPos heart = null;
    public Belly belly = null;

    public HashSet<BlockPos> blockPositions = new HashSet<BlockPos>();


    // =============== Object Management =============

    public HeartNetwork() {}

    public boolean HasHeart() {
        return heart != null;
    }

    public static int GetTierOf(Level level, BlockPos pos) {
        var netw = HeartBeating.TryGetNetwork(level, pos);
        return (netw == null) ? -1 : HeartFeeding.GetCurrentTier(netw);
    }

    public static boolean GetCraftableAtTier(BlockPos pos, Level level, int requiredTier) {
        HeartNetwork netw = HeartBeating.GetNetworkOrNew(level, pos);
        return netw.HasHeart() && HeartFeeding.GetCurrentTier(netw) >= requiredTier;
    }

    // =============== Network Management =============

    void MergeWithNetworksAt(Level level, BlockPos pos) {
        var allHearts = new HashSet<BlockPos>();

        if (this.HasHeart()) {
            allHearts.add(this.heart);
        }
        MergeWithNetworksSubroutine(level, pos, new AtomicReference<>(allHearts));
        
        if (allHearts.size() == 1) this.heart = allHearts.iterator().next();
        HeartBeating.ResolveHeartConflict(level, allHearts);
    }

    // AtomicReference is used to pass the parameter by reference, not value.
    private void MergeWithNetworksSubroutine(Level level, BlockPos pos, AtomicReference<HashSet<BlockPos>> allHearts) {
        BlockPos[] connectedBlockPos = {
            pos.above(),
            pos.below(),
            pos.north(),
            pos.south(),
            pos.west(),
            pos.east()
        };

        for (var curBlockPos : connectedBlockPos) {
            var blockState = level.getBlockState(curBlockPos);
            if (blockState.is(ModTags.Blocks.HEART_NETWORK_BLOCKS)) {

                boolean isHeart = blockState.getBlock().equals(ModBlocks.FACTORY_HEART.get());
                if (isHeart) allHearts.get().add(curBlockPos);

                HeartNetwork otherBlockNetw = HeartBeating.TryGetNetwork(level, curBlockPos);
                
                if (otherBlockNetw == null) {
                    blockPositions.add(curBlockPos);
                    MergeWithNetworksSubroutine(level, curBlockPos, allHearts);
                    continue;
                }

                if (otherBlockNetw.equals(this)) {continue;}

                else {
                    if (otherBlockNetw.HasHeart() && !otherBlockNetw.heart.equals(curBlockPos)) allHearts.get().add(otherBlockNetw.heart);
                    blockPositions.addAll(otherBlockNetw.blockPositions);
                    HeartBeating.allNetworks.get(level.dimensionType()).remove(otherBlockNetw);
                }
                
            }
        }
    }

    // =============== Heart Management =============

    

    public static void DoTickFor(Level level, BlockPos pos) {
        HeartNetwork netw = HeartBeating.GetNetworkOrNew(level, pos);
        if (netw.belly.fuelGauge > 0) netw.belly.fuelGauge -= 1;
        if (netw.belly.coolantGauge > 0) netw.belly.coolantGauge -= 1;
    }

    // =============== Maw Management =============

    
}