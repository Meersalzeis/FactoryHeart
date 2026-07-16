package com.meersalzeis.factoryheart.hearts;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

import com.meersalzeis.factoryheart.Config;
import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.hearting.FactoryHeartBlock;
import com.meersalzeis.factoryheart.blockentity.FHCraftingStation;
import com.meersalzeis.factoryheart.util.ModTags;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class HeartBeating {

    public static Hashtable<DimensionType, List<HeartNetwork>> allNetworks = new Hashtable<DimensionType, List<HeartNetwork>>();

    // =============== Block interaction =============== 

    public static void TryAddBlock(Level level, BlockPos pos, boolean isHeart) {

        // This already merges all connected networks, but does not resolve heart conflicts
        HeartNetwork netw = GetNetworkOrNew(level, pos, isHeart);

        // if (!isHeart) {
        //     return;
        // }

        // if (!netw.HasHeart()) {
        //     netw.heart = pos;
        //     return;
        // }

        // var relevantHearts = new HashSet<BlockPos>();
        // relevantHearts.add(pos);
        // relevantHearts.add(netw.heart);
        // var allConnectedHearts = GetCrowdedHearts(level, relevantHearts);

        // ResolveHeartConflict(level, allConnectedHearts);
    }

    /** Has to be called after removing the block! */
    public static void DeregisterBlock(Level level, BlockPos pos) {
        HeartNetwork netw = TryGetNetwork(level, pos);

        // is null when blocks are loaded but no interaction happened
        if (netw == null) {
            return;
        }

        if (netw.HasHeart() && netw.heart.equals(pos)) {
            netw.heart = null;
        }

        BlockPos blockPosToRemove = netw.blockPositions.stream()
            .filter(x -> x.equals(pos))
            .findFirst().get();
        netw.blockPositions.remove(blockPosToRemove);

        if (netw.blockPositions.isEmpty()) {
            allNetworks.remove(netw);
            return;
        }

        RedrawNetworksAfterRemoval(level, pos);
    }

    public static void changeTierOfHeart(Level level, BlockPos pos, int newTier) {
        HeartNetwork netw = GetNetworkOrNew(level, pos, true);
        for (var curBlockPos : netw.blockPositions) {
            
            BlockEntity bEntity = level.getBlockEntity(curBlockPos);
            if (bEntity instanceof FHCraftingStation station) {
                station.data.set(2, newTier);
                station.setChanged();
                station.initiateSync();
            }
        }
    }

    // =============== Network Getter / Util =============== 

    static HeartNetwork TryGetNetwork(Level level, BlockPos pos) {
        for (HeartNetwork network : GetAllNetworks(level)) {
            for (BlockPos lookAtPos : network.blockPositions) {
                if (lookAtPos.equals(pos)) {
                    return network;
                }
            }
        }
        return null;
    }

    static HeartNetwork GetNetworkOrNew(Level level, BlockPos pos) {
        for (HeartNetwork network : GetAllNetworks(level)) {
            for (BlockPos lookAtPos : network.blockPositions) {
                if (lookAtPos.equals(pos)) {
                    return network;
                }
            }
        }

        HeartNetwork newNet = new HeartNetwork();
        newNet.blockPositions.add(pos);
        AddToAll(newNet, level);
        
        newNet.MergeWithNetworksAt(level, pos);
        return newNet;
    }

    static HeartNetwork GetNetworkOrNew(Level level, BlockPos pos, boolean isHeart) {
        for (HeartNetwork network : GetAllNetworks(level)) {
            for (BlockPos lookAtPos : network.blockPositions) {
                if (lookAtPos.equals(pos)) {
                    return network;
                }
            }
        }

        HeartNetwork newNet = new HeartNetwork();
        newNet.blockPositions.add(pos);
        if (isHeart) newNet.heart = pos;
        AddToAll(newNet, level);
        
        newNet.MergeWithNetworksAt(level, pos);
        return newNet;
    }

    static void AddToAll(HeartNetwork netw, Level level) {
        DimensionType dimType = level.dimensionType();
        allNetworks.computeIfAbsent(dimType, k -> new ArrayList<>()).add(netw);
    }

    static List<HeartNetwork> GetAllNetworks(Level level) {
        DimensionType dimType = level.dimensionType();

        if (! allNetworks.containsKey(dimType)) {
            return new ArrayList<>();
        } else {
            return allNetworks.get(dimType);
        }
    }
    
    //=============== Network interaction =============== 

    private static void RedrawNetworksAfterRemoval(Level level, BlockPos pos) {
        BlockPos[] connectedBlockPos = {
            pos.above(),
            pos.below(),
            pos.north(),
            pos.south(),
            pos.west(),
            pos.east()
        };

        List<BlockPos> looseEnds = new ArrayList<BlockPos>();

        for (var curBlockPos : connectedBlockPos) {
            var blockState = level.getBlockState(curBlockPos);
            if (blockState.is(ModTags.Blocks.HEART_NETWORK_BLOCKS)) {
                looseEnds.add(curBlockPos);
            }
        }

        if (looseEnds.size() < 2) {
            FHModClient.debugMessageToAll("Only one (or no) loose ends - stop early", false);
            return;
        }
        
        DimensionType dimType = level.dimensionType();
        HeartNetwork oldNetw = TryGetNetwork(level, looseEnds.get(0));
        allNetworks.get(dimType).remove(oldNetw);

        // for (BlockPos looseEndPos : looseEnds) {
        //     // 100% new
        //     var looseEndNetw = GetNetworkOrNew(level, looseEndPos);

        //     // If this end has heart, preserve old fuel stats and references
        //     if (looseEndNetw.HasHeart()) {
        //         oldNetw.blockPositions = looseEndNetw.blockPositions;
        //         allNetworks.get(dimType).remove(looseEndNetw);
        //         AddToAll(oldNetw, level);
        //     }
        // }
    }

    //=============== Heart interaction =============== 

    static void ResolveHeartConflict(Level level, HashSet<BlockPos> allHearts) {

        if (allHearts.isEmpty()) return;
        if (allHearts.size() == 1) {
            BlockPos onlyOne = allHearts.iterator().next();

            GetNetworkOrNew(level, onlyOne, true).heart = onlyOne;
            return;
        }
        
        HashSet<BlockPos> relevantHearts = allHearts;
        while (! (relevantHearts.size() <= 1)) {

            // Decision
            int randomIndex = FHModMain.rnd.nextInt(relevantHearts.size());
            BlockPos heartToRemove = null;
            Iterator<BlockPos> iterator = allHearts.iterator();
            for (int i = 0; i <= randomIndex; i++) {
                heartToRemove = iterator.next();
            }

            HeartBeating.DeregisterBlock(level, heartToRemove);
            level.destroyBlock(heartToRemove, true);

            allHearts.remove(heartToRemove);

            // Check which hearts are fixed
            relevantHearts = GetCrowdedHearts(level, relevantHearts);
        }

        // At this point only 1 heart remains
        BlockPos onlyOne = allHearts.iterator().next();
        GetNetworkOrNew(level, onlyOne, true).heart = onlyOne;
    }

    private static HashSet<BlockPos> GetCrowdedHearts(Level level, HashSet<BlockPos> allHearts) {
        HashSet<BlockPos> crowded = new HashSet<BlockPos>();

        for (BlockPos curHeart : allHearts) {
            HeartNetwork curNetw = GetNetworkOrNew(level, curHeart, true);
            boolean stillLonely = true;

            for (var curBlock : curNetw.blockPositions) {
                var blockState = level.getBlockState(curBlock);
                boolean isHeart = blockState.getBlock().equals(ModBlocks.FACTORY_HEART.get());
                
                if (isHeart) {
                    crowded.add(curBlock);

                    if (stillLonely) {
                        crowded.add(curHeart);
                        stillLonely = false;
                    }
                }
            }

            if (stillLonely) curNetw.heart = curHeart;
        }  
        return crowded;
    }

    public static void MawGetsItemFed(Level level, BlockPos mawPos, ItemEntity itemFed) {
        var netw = GetNetworkOrNew(level, mawPos, false);
        if (netw.HasHeart()) netw.getHeartEntity(level, mawPos).netwGetsItemFed(level, netw.heart, itemFed);
    }
}
