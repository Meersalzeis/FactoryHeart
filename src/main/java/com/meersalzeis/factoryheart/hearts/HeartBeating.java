package com.meersalzeis.factoryheart.hearts;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
import com.meersalzeis.factoryheart.util.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;

public class HeartBeating {

    public static Hashtable<DimensionType, List<HeartNetwork>> allNetworks = new Hashtable<DimensionType, List<HeartNetwork>>();

    // =============== Block interaction =============== 

    public static void TryAddBlock(Level level, BlockPos pos, boolean isHeart) {
        HeartNetwork netw = getNetworkOrNew(level, pos, isHeart);
        
        if (isHeart && !netw.HasHeart()) {
            netw.heart = pos;
            return;
        }

        // String result = allNetworks.values().stream()
        // .flatMap(List::stream)          // List<Y> -> Y
        // .flatMap(curNetw -> curNetw.blockPositions.stream()) // Y -> Z
        // .map(BlockPos::toShortString)     // Z -> String
        // .collect(Collectors.joining(", "));

        // FHModClient.debugMessageToAll(result);
    }

    /** Has to be called after removing the block!*/
    public static void DeregisterBlock(Level level, BlockPos pos) {
        HeartNetwork netw = tryGetNetwork(level, pos);

        // is null when blocks are loaded but no interaction with the block happened
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

    public static int getTierOfNetw(Level level, BlockPos pos) {
        var heartEntity = HeartNetwork.getHeartEntity(level, pos);
    
        if (heartEntity == null) return 0;
        else return heartEntity.calculateCurrentTier();
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
        if (isHeart) {
            for (HeartNetwork network : GetAllNetworks(level)) {
                if (network.HasHeart() && network.heart.equals(pos)) {
                    return network;
                }
            }
        } else {
            for (HeartNetwork network : GetAllNetworks(level)) {
                for (BlockPos lookAtPos : network.blockPositions) {
                    if (lookAtPos.equals(pos)) {
                        return network;
                    }
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
        
        // Decision
        int randomIndex = FHModMain.rnd.nextInt(allHearts.size());
        BlockPos heartToRemove = null;
        Iterator<BlockPos> iterator = allHearts.iterator();
        FHModClient.debugMessageToAll("rolled "+randomIndex+" out of "+allHearts.size());
        for (int i = 0; i <= randomIndex; i++) {
            heartToRemove = iterator.next();
        }

        level.destroyBlock(heartToRemove, true);

        // Check which hearts are fixed
        causeRecursiveConflictChecks(level, allHearts);
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
    }

    public static void MawGetsItemFed(Level level, BlockPos mawPos, ItemEntity itemFed) {
        var netw = getNetworkOrNew(level, mawPos, false);
        if (netw.HasHeart()) {
            var heartEntity = netw.getHeartEntity(level, mawPos);
            if (heartEntity != null) heartEntity.netwGetsItemFed(level, netw.heart, itemFed);
        }
    }
}
