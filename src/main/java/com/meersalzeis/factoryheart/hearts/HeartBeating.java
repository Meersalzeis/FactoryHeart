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
import com.meersalzeis.factoryheart.util.ModTags;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public class HeartBeating {

    public static Hashtable<DimensionType, List<HeartNetwork>> allNetworks = new Hashtable<DimensionType, List<HeartNetwork>>();

    // =============== Block interaction =============== 

    public static void AddBlock(Level level, BlockPos pos, boolean isHeart) {
        HeartNetwork netw = GetNetworkOrNew(level, pos);
        netw.blockPositions.add(pos);

        FHModClient.debugMessageToAll("AddBlock to netw w.H." + netw.HasHeart() , false);

        if (!isHeart) {
            FHModClient.debugMessageToAll("netw tier is" + HeartFeeding.GetCurrentTier(netw), false);
            return;
        }

        if (!netw.HasHeart()) {
            netw.heart = pos;
            return;
        }
        
        if (!netw.heart.equals(pos)) {
            var relevantHearts = new HashSet<BlockPos>();
            relevantHearts.add(pos);
            relevantHearts.add(netw.heart);

            var allConnectedHearts = GetCrowdedHearts(level, relevantHearts);
            ResolveHeartConflict(level, allConnectedHearts);
        } else {
            FHModClient.debugMessageToAll("illegal state/argumetns for HeartBeating!", false);
        }
    }

    /** Has to be called after removing the block! */
    public static void DeregisterBlock(Level level, BlockPos pos, boolean deregisterHeart) {
        HeartNetwork netw = TryGetNetwork(level, pos);

        // is null when blocks are loaded but no interaction happened
        if (netw == null) {
            FHModClient.debugMessageToAll("ERR deregister block - cannot find old block!", false);
            return;
        }

        BlockPos blockPosToRemove = netw.blockPositions.stream()
            .filter(x -> x.equals(pos))
            .findFirst().get();
        netw.blockPositions.remove(blockPosToRemove);

        if (netw.blockPositions.isEmpty()) {
            allNetworks.remove(netw);
            FHModClient.debugMessageToAll("Remove Network bc empty!", false);
            return;
        }

        if (deregisterHeart) {
            FHModClient.debugMessageToAll("deregister heart!", false);
            netw.heart = null;
        }

        RedrawNetworksAfterRemoval(level, pos);
    }

    public static int GetTier(Level level, BlockPos pos) {
        return HeartFeeding.GetCurrentTier(GetNetworkOrNew(level, pos));
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

        if (looseEnds.size() < 2) return;
        
        DimensionType dimType = level.dimensionType();
        HeartNetwork oldNetw = TryGetNetwork(level, looseEnds.get(0));
        allNetworks.get(dimType).remove(oldNetw);

        FHModClient.debugMessageToAll("Checking loose ends", false);
        for (BlockPos looseEndPos : looseEnds) {
            // 100% new
            var looseEndNetw = GetNetworkOrNew(level, looseEndPos);

            // If this end has heart, preserve old fuel stats and references
            if (looseEndNetw.HasHeart()) {
                oldNetw.blockPositions = looseEndNetw.blockPositions;
                allNetworks.get(dimType).remove(looseEndNetw);
                AddToAll(oldNetw, level);
            }
            FHModClient.debugMessageToAll("Check loose end at "+looseEndPos.toShortString() + "assigned to netw with size " + GetNetworkOrNew(level, looseEndPos).blockPositions.size(), false);
        }
    }

    //=============== Heart interaction =============== 

    static void ResolveHeartConflict(Level level, HashSet<BlockPos> allHearts) {

        if (allHearts.isEmpty()) return;
        if (allHearts.size() == 1) {
            BlockPos onlyOne = allHearts.iterator().next();

            GetNetworkOrNew(level, onlyOne).heart = onlyOne;
            FHModClient.debugMessageToAll("Only 1 heart found", false);
            return;
        }

        FHModClient.debugMessageToAll("HeartConflict! ", false);
        
        HashSet<BlockPos> relevantHearts = allHearts;
        while (!relevantHearts.isEmpty()) {
            FHModClient.debugMessageToAll("HeartConflict new loop", false);

            // Decision
            int randomIndex = FHModMain.rnd.nextInt(relevantHearts.size());
            BlockPos heartToRemove = null;
            Iterator<BlockPos> iterator = allHearts.iterator();
            for (int i = 0; i <= randomIndex; i++) {
                heartToRemove = iterator.next();
            }

            FHModClient.debugMessageToAll("Removing one heart at " + heartToRemove.toShortString(), false);

            level.destroyBlock(heartToRemove, true);
            DeregisterBlock(level, heartToRemove, false);

            // Check which hearts are fixed
            relevantHearts = GetCrowdedHearts(level, relevantHearts);
        }
    }

    private static HashSet<BlockPos> GetCrowdedHearts(Level level, HashSet<BlockPos> allHearts) {
        HashSet<BlockPos> crowded = new HashSet<BlockPos>();

        for (BlockPos curHeart : allHearts) {
            HeartNetwork curNetw = GetNetworkOrNew(level, curHeart);
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
}
