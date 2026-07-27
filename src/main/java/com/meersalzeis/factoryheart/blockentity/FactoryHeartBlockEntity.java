package com.meersalzeis.factoryheart.blockentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.meersalzeis.factoryheart.Config;
import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.crafting.BlazerBlock;
import com.meersalzeis.factoryheart.block.hearting.FactoryHeartBlock;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartNetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FactoryHeartBlockEntity extends BlockEntity {

    private static ArrayList<Item> fuel_T1 = new ArrayList<Item>();
    private static ArrayList<Item> fuel_T2 = new ArrayList<Item>();
    private static ArrayList<Item> fuel_T3 = new ArrayList<Item>();
    private static ArrayList<Item> fuel_T4 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T1 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T2 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T3 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T4 = new ArrayList<Item>();

    private static int resource_per_item;
    private static int max_resource;

    private int last_comp_output = 0;

    private int fuel_left = 0;
    private int coolant_left = 0;
    private int last_fuel_tier = 0;
    private int last_coolant_tier = 0;

    public FactoryHeartBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.HEART_BE.get(), pos, blockState);
    }

    public void onLoad() {
        if (level.isClientSide()) return;
        recheckTier(level, worldPosition);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("heart.fuel", fuel_left);
        pTag.putInt("heart.cool", coolant_left);
        pTag.putInt("heart.l_fuel", last_fuel_tier);
        pTag.putInt("heart.l_cool", last_coolant_tier);

        pTag.putInt("heart.l_comp", last_comp_output);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        fuel_left = pTag.getInt("heart.fuel");
        coolant_left = pTag.getInt("heart.cool");
        last_fuel_tier = pTag.getInt("heart.l_fuel");
        last_coolant_tier  = pTag.getInt("heart.l_cool");

        last_comp_output  = pTag.getInt("heart.l_comp");
    }

    public void tick(BlockPos pos, BlockState state) {
        if (level.isClientSide) return;
        drainBy(pos, state, 1);
    }

    public boolean drainBy(BlockPos pos, BlockState state, int amount) {
        boolean skipRecheckTier = true;

        fuel_left -= amount;
        if (fuel_left < 0) {
            fuel_left = 0;
            if (last_fuel_tier != 0) {
                last_fuel_tier = 0;
                skipRecheckTier = false;
            }
        }

        coolant_left -= amount;
        if (coolant_left < 0) {
            coolant_left = 0;
            if (last_coolant_tier != 0) {
                last_coolant_tier = 0;
                skipRecheckTier = false;
            }
        }

        if ((!skipRecheckTier) && state.getValue(FactoryHeartBlock.TIER) != 0) {
            recheckTier(level, pos);
        }

        checkCompOutput(pos);
        return skipRecheckTier;
    }

    public int calculateCurrentTier() {
        if (fuel_left <= 0 ) return 0;

        int supplyTier = Math.max(1, Math.min(last_coolant_tier, last_fuel_tier));
        if (coolant_left > 0) return supplyTier;

        switch (supplyTier) {
            case 1: return coolant_T1.isEmpty() ? 1 : 0;
            case 2: return coolant_T2.isEmpty() ? 2 : 0;
            case 3: return coolant_T3.isEmpty() ? 3 : 0;
            case 4: return coolant_T4.isEmpty() ? 4 : 0;
        }
        // should be dead code
        return -1;
    }

    // =============== Comparator Management =============== 

    public int getComparatorOutput() {
        return last_comp_output;
    }

    public void checkCompOutput(BlockPos pos) {
        int new_comp_out = calculateComparatorOutput();
        if (new_comp_out != last_comp_output) {
            level.updateNeighbourForOutputSignal(pos, ModBlocks.FACTORY_HEART.get());
            last_comp_output = new_comp_out;
            initiateSync();
        }
    }

    // 15 as max redstone output
    public int calculateComparatorOutput() {
        int combined_left = fuel_left + coolant_left;
        if (combined_left == 0) return 0;
        int res = Math.round(combined_left*1.0f/(max_resource*2)*15);
        return res;
    }

    // =============== Fuel Management =============== 

    public static void InitStaticVariables() {
        fuel_T1 = configStringListToItems(Config.TIER_1_FUEL_ITEMS.get());
        fuel_T2 = configStringListToItems(Config.TIER_2_FUEL_ITEMS.get());
        fuel_T3 = configStringListToItems(Config.TIER_3_FUEL_ITEMS.get());
        fuel_T4 = configStringListToItems(Config.TIER_4_FUEL_ITEMS.get());
        coolant_T1 = configStringListToItems(Config.TIER_1_COOLANT_ITEMS.get());
        coolant_T2 = configStringListToItems(Config.TIER_2_COOLANT_ITEMS.get());
        coolant_T3 = configStringListToItems(Config.TIER_3_COOLANT_ITEMS.get());
        coolant_T4 = configStringListToItems(Config.TIER_4_COOLANT_ITEMS.get());
        resource_per_item = Config.RESOURCE_PER_ITEM.get();
        max_resource = Config.MAX_RESOURCE.get();
    }

    private static ArrayList<Item> configStringListToItems(List<? extends String> configStringList) {
        ArrayList<Item> res = new ArrayList<Item>();
        for (String curItemString : configStringList) {
            res.add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(curItemString)));
        }
        return res;
    }

    public void netwGetsItemFed(Level level, BlockPos heartPos, ItemEntity itemEntity) {

        if (fuel_T4.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 4, heartPos, true);
        }
        if (fuel_T3.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 3, heartPos, true);
        }
        if (fuel_T2.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 2, heartPos, true);
        }
        if (fuel_T1.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 1, heartPos, true);
        }

        if (coolant_T4.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 4, heartPos, false);
        }
        if (coolant_T3.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 3, heartPos, false);
        }
        if (coolant_T2.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 2, heartPos, false);
        }
        if (coolant_T1.contains(itemEntity.getItem().getItem())) {
            feedOn(itemEntity, 1, heartPos, false);
        }

        // else nothing happens
    }

    private void feedOn(ItemEntity itemEntity, int tier, BlockPos pos, boolean isFuel) {
        int gaugeVal = isFuel ? fuel_left : coolant_left;
        int lastUsedTier = isFuel ? last_fuel_tier : last_coolant_tier;

        if (gaugeVal + resource_per_item > max_resource && tier <= lastUsedTier) return;

        if (lastUsedTier < tier) {
            // gets topped up by new 
            if (isFuel) {fuel_left = 0;}
            else {coolant_left = 0;}
        }

        if (isFuel) {
            fuel_left += resource_per_item;
            last_fuel_tier = tier;
        } else {
            coolant_left += resource_per_item;
            last_coolant_tier = tier;
        }

        ItemStack oldStack = itemEntity.getItem();
        oldStack.shrink(1);
        if (oldStack.getCount() == 0) {
            itemEntity.discard();
        }

        recheckTier(level, worldPosition);
        checkCompOutput(pos);
    }

    public void InitNetwork() {
        recheckTier(level, worldPosition);
    }

    private void recheckTier(Level level, BlockPos heartPos) {
        int newTier = calculateCurrentTier();
        setTier(level, heartPos, newTier);
    }

    private void setTier(Level level, BlockPos heartPos, int newTier) {
        BlockState state = level.getBlockState(heartPos);
        level.setBlockAndUpdate(heartPos, state.setValue(FactoryHeartBlock.TIER, newTier));
        last_comp_output = getComparatorOutput();
        HeartBeating.setTierOfNetw(level, heartPos, newTier);
    }

    @Override
    public String toString() {
        return "FHeart curTier: " + calculateCurrentTier() + ", fuelTier " + last_fuel_tier + ", coolantTier " + last_coolant_tier + " with " +fuel_left + " fuel left and " + coolant_left + " coolant left.";
    }

    // =============== Handle Client-Server Sync ==============

    public void initiateSync() {
        level.sendBlockUpdated(
            worldPosition,
            getBlockState(),
            getBlockState(),
            Block.UPDATE_CLIENTS
        );
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    // =============== Jade access ==============

    public int getFuelLeft() { return fuel_left; }
    public int getCoolantLeft() { return coolant_left; }

    public boolean usesCoolant(int currentTier) {
        switch (currentTier) {
            case 1: return !coolant_T1.isEmpty();
            case 2: return !coolant_T2.isEmpty();
            case 3: return !coolant_T3.isEmpty();
            case 4: return !coolant_T4.isEmpty();
            default: return false;
        }
    }
}
