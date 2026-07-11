package com.meersalzeis.factoryheart.blockentity;

import java.util.ArrayList;
import java.util.List;

import com.meersalzeis.factoryheart.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    private int fuel_left = 0;
    private int coolant_left = 0;
    private int lastUsedFuelTier = 0;
    private int lastUsedCoolantTier = 0;

    public FactoryHeartBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.HEART_BE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("heart.fuel", fuel_left);
        pTag.putInt("heart.cool", coolant_left);
        pTag.putInt("heart.l_fuel", lastUsedFuelTier);
        pTag.putInt("heart.l_cool", lastUsedCoolantTier);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        fuel_left = pTag.getInt("heart.fuel");
        coolant_left = pTag.getInt("heart.cool");
        lastUsedFuelTier = pTag.getInt("heart.l_fuel");
        lastUsedCoolantTier  = pTag.getInt("heart.l_cool");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        drainBy(1);
    }

    public boolean drainBy(int amount) {
        boolean result = true;

        fuel_left -= amount;
        if (fuel_left < 0) {
            fuel_left = 0;
            result = false;
        }

        coolant_left -= amount;
        if (fuel_left < 0) {
            fuel_left = 0;
            result = false;
        }

        return result;
    }

    public int GetCurrentTier() {
        if (fuel_left <= 0 ) return 0;

        int supplyTier = Math.max(1, Math.min(lastUsedCoolantTier, lastUsedFuelTier));
        switch (supplyTier) {
            case 1: return (coolant_T1.isEmpty() || coolant_left > 0) ? 1 : 0;
            case 2: return (coolant_T2.isEmpty() || coolant_left > 0) ? 2 : 0;
            case 3: return (coolant_T3.isEmpty() || coolant_left > 0) ? 3 : 0;
            case 4: return (coolant_T4.isEmpty() || coolant_left > 0) ? 4 : 0;
        }
        // should be dead code
        return -1;
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

    public void MawGetsItemFed(ItemEntity itemEntity) {

        if (fuel_T4.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 4, true);
        }
        if (fuel_T3.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 3, true);
        }
        if (fuel_T2.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 2, true);
        }
        if (fuel_T1.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 1, true);
        }

        if (coolant_T4.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 4, false);
        }
        if (coolant_T3.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 3, false);
        }
        if (coolant_T2.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 2, false);
        }
        if (coolant_T1.contains(itemEntity.getItem().getItem())) {
            FeedOn(itemEntity, 1, false);
        }

        // else nothing happens
    }

    private void FeedOn(ItemEntity itemEntity, int tier, boolean isFuel) {
        int gaugeVal = isFuel ? fuel_left : coolant_left;

        if (gaugeVal + resource_per_item > max_resource) return;

        if (isFuel) {
            fuel_left += resource_per_item;
            lastUsedFuelTier = tier;
        } else {
            coolant_left += resource_per_item;
            lastUsedCoolantTier = tier;
        }
        ItemStack oldStack = itemEntity.getItem();
        oldStack.shrink(1);
        if (oldStack.getCount() == 1) {
            itemEntity.discard();
            return;
        }
    }

    @Override
    public String toString() {
        return "FHeart curTier: " + GetCurrentTier() + ", fuelTier " + lastUsedFuelTier + ", coolantTier " + lastUsedCoolantTier + " with " +fuel_left + " fuel left and " + coolant_left + " coolant left.";
    }
}
