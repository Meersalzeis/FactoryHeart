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
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
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
        HeartNetwork netw = getNetworkOrNew(level, pos, isHeart);

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
        HeartNetwork netw = tryGetNetwork(level, pos);

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

    public static void setTierOfNetw(Level level, BlockPos pos, int newTier) {
        HeartNetwork netw = GetNetworkOrNew(level, pos);
        for (var curBlockPos : netw.blockPositions) {
            
            BlockEntity bEntity = level.getBlockEntity(curBlockPos);
            if (bEntity instanceof FHCraftStationEntity station) {
                station.data.set(2, newTier);
                station.setChanged();
                station.initiateSync();
            }
        }
    }

    // =============== Network Getter / Util =============== 

    static HeartNetwork tryGetNetwork(Level level, BlockPos pos) {
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

    static HeartNetwork getNetworkOrNew(Level level, BlockPos pos, boolean isHeart) {
        for (HeartNetwork network : GetAllNetworks(level)) {
            if (network.HasHeart() && network.heart.equals(pos)) {
                return network;
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
            return;
        }
        
        DimensionType dimType = level.dimensionType();
        HeartNetwork oldNetw = tryGetNetwork(level, looseEnds.get(0));
        allNetworks.get(dimType).remove(oldNetw);

        for (BlockPos looseEndPos : looseEnds) {
  
            var looseEndNetw = GetNetworkOrNew(level, looseEndPos);
            if (looseEndNetw.HasHeart()) {
                HeartNetwork.getHeartEntity(level, looseEndPos).InitNetwork();
            } else {
                HeartBeating.setTierOfNetw(level, looseEndPos, 0);
            }
            

            // If this end has heart, preserve old fuel stats and references
            // if (looseEndNetw.HasHeart()) {
            //     oldNetw.blockPositions = looseEndNetw.blockPositions;
            //     allNetworks.get(dimType).remove(looseEndNetw);
            //     AddToAll(oldNetw, level);
            // }
        }
    }

    //=============== Heart interaction =============== 

    static void ResolveHeartConflict(Level level, HashSet<BlockPos> allHearts) {
        if (allHearts.isEmpty()) return;
        if (allHearts.size() == 1) {
            BlockPos onlyOne = allHearts.iterator().next();

            getNetworkOrNew(level, onlyOne, true).heart = onlyOne;
            return;
        }
        
        FHModClient.debugMessageToAll("New serious heartconflict call");
        
        // Decision
        int randomIndex = FHModMain.rnd.nextInt(allHearts.size());
        BlockPos heartToRemove = null;
        Iterator<BlockPos> iterator = allHearts.iterator();
        FHModClient.debugMessageToAll("rolled "+randomIndex+" out of "+allHearts.size());
        for (int i = 0; i <= randomIndex; i++) {
            heartToRemove = iterator.next();
        }
        FHModClient.debugMessageToAll("Start heartconflict before remove - hearts:" + allHearts.toString());
        FHModClient.debugMessageToAll("Removing" + heartToRemove.toShortString());

        level.destroyBlock(heartToRemove, true);

        // Check which hearts are fixed
        FHModClient.debugMessageToAll("Start heartconflict after remove - hearts:" + allHearts.toString());
        causeRecursiveConflictChecks(level, allHearts);
        //allHearts = getCrowdedHearts(level, allHearts);
        //FHModClient.debugMessageToAll("Start heartconflict after getCrowded - hearts:" + allHearts.toString());
    }

    //private static HashSet<BlockPos> getCrowdedHearts
    private static void causeRecursiveConflictChecks(Level level, HashSet<BlockPos> allHearts) {
        DimensionType dimType = level.dimensionType();

        for (BlockPos curHeart : allHearts) {
            HeartNetwork oldNetw = tryGetNetwork(level, curHeart);
            if (oldNetw == null) continue;
            allNetworks.get(dimType).remove(oldNetw);
        }

        for (BlockPos curHeart : allHearts) {
            // Check if heart was removed by previous iteration of this very loop
            if (!level.getBlockState(curHeart).is(ModBlocks.FACTORY_HEART)) continue;
            
            // Causes new HeartConflict checks if needed
            HeartNetwork newNetw = getNetworkOrNew(level, curHeart, true);
        }

        // HashSet<BlockPos> crowded = new HashSet<BlockPos>();

        // for (BlockPos curHeart : allHearts) {
        //     HeartNetwork curNetw = GetNetworkOrNew(level, curHeart, true);
        //     boolean stillLonely = true;

        //     for (var curBlock : curNetw.blockPositions) {
        //         var blockState = level.getBlockState(curBlock);
        //         boolean isHeart = blockState.getBlock().equals(ModBlocks.FACTORY_HEART.get());
                
        //         if (isHeart) {
        //             crowded.add(curBlock);

        //             if (stillLonely) {
        //                 crowded.add(curHeart);
        //                 stillLonely = false;
        //             }
        //         }
        //     }

        //     if (stillLonely) curNetw.heart = curHeart;
        // }  
        // return crowded;
    }

    public static void MawGetsItemFed(Level level, BlockPos mawPos, ItemEntity itemFed) {
        var netw = getNetworkOrNew(level, mawPos, false);
        if (netw.HasHeart()) {
            var heartEntity = netw.getHeartEntity(level, mawPos);
            if (heartEntity != null) heartEntity.netwGetsItemFed(level, netw.heart, itemFed);
        }
    }
}
