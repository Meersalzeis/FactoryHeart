package com.meersalzeis.factoryheart.hearts;

import java.util.ArrayList;
import java.util.List;

import com.meersalzeis.factoryheart.Config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HeartFeeding {

    private static ArrayList<Item> fuel_T1 = new ArrayList<Item>();
    private static ArrayList<Item> fuel_T2 = new ArrayList<Item>();
    private static ArrayList<Item> fuel_T3 = new ArrayList<Item>();
    private static ArrayList<Item> fuel_T4 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T1 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T2 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T3 = new ArrayList<Item>();
    private static ArrayList<Item> coolant_T4 = new ArrayList<Item>();

    private static final int MAX_GAUGE = 2000;
    private static final int FUEL_PER_ITEM = 1000;

    public static void RegisterFuelsAndCoolants() {
        fuel_T1 = configStringListToItems(Config.TIER_1_FUEL_ITEMS.get());
        fuel_T2 = configStringListToItems(Config.TIER_2_FUEL_ITEMS.get());
        fuel_T3 = configStringListToItems(Config.TIER_3_FUEL_ITEMS.get());
        fuel_T4 = configStringListToItems(Config.TIER_4_FUEL_ITEMS.get());
        coolant_T1 = configStringListToItems(Config.TIER_1_COOLANT_ITEMS.get());
        coolant_T2 = configStringListToItems(Config.TIER_2_COOLANT_ITEMS.get());
        coolant_T3 = configStringListToItems(Config.TIER_3_COOLANT_ITEMS.get());
        coolant_T4 = configStringListToItems(Config.TIER_4_COOLANT_ITEMS.get());
    }

    private static ArrayList<Item> configStringListToItems(List<? extends String> configStringList) {
        ArrayList<Item> res = new ArrayList<Item>();
        for (String curItemString : configStringList) {
            res.add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(curItemString)));
        }
        return res;
    }

    static int GetCurrentTier(HeartNetwork netw) {
        if (!netw.HasHeart()) return 0;
        if (netw.belly.fuelGauge <= 0 ) return 0;

        int supplyTier = Math.min(netw.belly.lastUsedCoolantTier, netw.belly.lastUsedFuelTier);
        switch (supplyTier) {
            case 0: return 0;
            case 1: return (coolant_T1.isEmpty() || netw.belly.coolantGauge > 0) ? 1 : 0;
            case 2: return (coolant_T2.isEmpty() || netw.belly.coolantGauge > 0) ? 2 : 0;
            case 3: return (coolant_T3.isEmpty() || netw.belly.coolantGauge > 0) ? 3 : 0;
            case 4: return (coolant_T4.isEmpty() || netw.belly.coolantGauge > 0) ? 4 : 0;
        }
        // should be dead code
        return -1;
    }

    public static void MawGetsItemFed(Level level, BlockPos Pos, ItemEntity itemEntity) {
        //if (! GetNetworkOrNew(level, Pos).HasHeart()) return;

        if (fuel_T4.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 4, true);
        }
        if (fuel_T3.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 3, true);
        }
        if (fuel_T2.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 2, true);
        }
        if (fuel_T1.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 1, true);
        }

        if (coolant_T4.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 4, false);
        }
        if (coolant_T3.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 3, false);
        }
        if (coolant_T2.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 2, false);
        }
        if (coolant_T1.contains(itemEntity.getItem().getItem())) {
            FeedOn(level, Pos, itemEntity, 1, false);
        }

        // else nothing happens
    }

    private static void FeedOn(Level level, BlockPos pos, ItemEntity itemEntity, int tier, boolean isFuel) {
        HeartNetwork netw = HeartBeating.GetNetworkOrNew(level, pos);
        Belly belly = netw.belly;

        int gaugeVal = isFuel ? belly.fuelGauge : belly.coolantGauge;

        if (gaugeVal + FUEL_PER_ITEM <= MAX_GAUGE) {
            if (isFuel) {
                belly.fuelGauge += FUEL_PER_ITEM;
            } else {
                belly.coolantGauge+= FUEL_PER_ITEM;
            }

            ItemStack oldStack = itemEntity.getItem();
            if (oldStack.getCount() == 1) {
                itemEntity.kill();
                return;
            }
            // else only eat 1? per tick?
            itemEntity.setItem( new ItemStack(oldStack.getItem(), oldStack.getCount()-1));

            if (GetCurrentTier(netw) < tier) {
                if (isFuel) belly.lastUsedFuelTier = tier;
                else belly.lastUsedCoolantTier = tier;
            }
        }
    }
}
