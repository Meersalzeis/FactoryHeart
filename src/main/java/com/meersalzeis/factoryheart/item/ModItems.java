package com.meersalzeis.factoryheart.item;

import net.neoforged.bus.api.IEventBus;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.item.custom.EnchantEffectItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FHModMain.MOD_ID);

    public static final DeferredItem<Item> ABZERO_MATTER = ITEMS.registerSimpleItem("abzero_matter");
    public static final DeferredItem<Item> CONTAINED_DEEPCOOL_CRYSTAL = ITEMS.registerSimpleItem("contained_deepcool_crystal");
    //public static final DeferredItem<Item> COPPER_WIRE = ITEMS.registerSimpleItem("copper_wire");
    public static final DeferredItem<Item> DEEPCOOL_CRYSTAL = ITEMS.registerSimpleItem("deepcool_crystal");
    public static final DeferredItem<Item> EMPTY_CAN = ITEMS.registerSimpleItem("empty_can");
    public static final DeferredItem<Item> ENERGY_DRINK = ITEMS.registerSimpleItem("energy_drink");
    public static final DeferredItem<Item> ENERGY_POWDER = ITEMS.registerSimpleItem("energy_powder");
    public static final DeferredItem<Item> FACTORY_PASTE = ITEMS.registerSimpleItem("factory_paste");
    public static final DeferredItem<Item> FACTORY_TOOTH = ITEMS.registerSimpleItem("factory_tooth");
    
    public static final DeferredItem<Item> METAL_DISC = ITEMS.registerSimpleItem("metal_disc");
    public static final DeferredItem<Item> METAL_SHEET = ITEMS.registerSimpleItem("metal_sheet");
    public static final DeferredItem<Item> SODA_POWDER = ITEMS.registerSimpleItem("soda_powder");

    public static final DeferredItem<Item> PLUNGER = ITEMS.registerSimpleItem("plunger");

    public static final DeferredItem<Item> CONTAINMENT_SPOOL = ITEMS.registerSimpleItem("containment_spool");
    public static final DeferredItem<Item> CONTAINMENT_FIELD_PROJECTOR = ITEMS.registerSimpleItem("containment_field_projector");
    public static final DeferredItem<Item> CONTAINMENT_CASING = ITEMS.registerSimpleItem("containment_casing");

    public static final DeferredItem<Item> SMART_SHARD = ITEMS.registerSimpleItem("smart_shard");
    public static final DeferredItem<Item> SMART_PART = ITEMS.registerSimpleItem("smart_part");
    public static final DeferredItem<Item> SMART_HEART = ITEMS.registerSimpleItem("smart_heart");
    public static final DeferredItem<Item> HARD_PART = ITEMS.registerSimpleItem("hard_part");

    public static final DeferredItem<Item> SMART_SHARD_U = ITEMS.registerSimpleItem("smart_shard_untested");
    public static final DeferredItem<Item> SMART_PART_U = ITEMS.registerSimpleItem("smart_part_untested");
    public static final DeferredItem<Item> SMART_HEART_U = ITEMS.registerSimpleItem("smart_heart_untested");
    public static final DeferredItem<Item> HARD_PART_U = ITEMS.registerSimpleItem("hard_part_untested");

    public static final DeferredItem<Item> SMART_SHARD_F = ITEMS.registerSimpleItem("smart_shard_faulty");
    public static final DeferredItem<Item> SMART_PART_F = ITEMS.registerSimpleItem("smart_part_faulty");
    public static final DeferredItem<Item> SMART_HEART_F = ITEMS.registerSimpleItem("smart_heart_faulty");
    public static final DeferredItem<Item> HARD_PART_F = ITEMS.registerSimpleItem("hard_part_faulty");

    public static final DeferredItem<Item> ABZERO_SHARD = ITEMS.registerSimpleItem("abzero_shard");
    public static final DeferredItem<Item> ABZERO_CRYSTAL = ITEMS.registerSimpleItem("abzero_crystal");

    public static final DeferredItem<Item> SLIMY_ROTTEN = ITEMS.registerSimpleItem("slimy_rotten_flesh");
    public static final DeferredItem<Item> LEATHERY_ROTTEN = ITEMS.registerSimpleItem("leathery_rotten_flesh");


    public static final DeferredItem<Item> BLAZEAR_PELLET = ITEMS.registerItem(
        "blazear_pellet",
        properties -> new FuelItem(properties, 2400),
        new Item.Properties()
    );
    public static final DeferredItem<Item> BLAZEAR_ROD = ITEMS.registerItem(
        "blazear_rod",
        properties -> new FuelItem(properties, 8000),
        new Item.Properties()
    );
    public static final DeferredItem<Item> ACTIVATED_BLAZEAR_ROD = ITEMS.registerItem(
        "activated_blazear_rod",
        properties -> new EnchantEffectItem(properties, 12000),
        new Item.Properties()
    );
    public static final DeferredItem<Item> BLAZEAR_FUEL_ROD = ITEMS.registerItem(
        "blazear_fuel_rod",
        properties -> new FuelItem(properties, 40000),
        new Item.Properties()
    );

    public static final DeferredItem<Item> COMBUSTION_CORE = ITEMS.registerItem(
        "combustion_core",
        properties -> new FuelItem(properties, 160000),
        new Item.Properties()
    );
    public static final DeferredItem<Item> RAW_BLAZE_CANDY = ITEMS.registerItem(
        "raw_blaze_candy",
        properties -> new FuelItem(properties, 160000),
        new Item.Properties()
    );
    public static final DeferredItem<Item> UNWRAPPED_BLAZE_CANDY = ITEMS.registerItem(
        "unwrapped_blaze_candy",
        properties -> new FuelItem(properties, 170000),
        new Item.Properties()
    );
    public static final DeferredItem<Item> BLAZE_CANDY = ITEMS.registerItem(
        "blaze_candy",
        properties -> new EnchantEffectItem(properties, 180000),
        new Item.Properties()
    );

    public static final DeferredItem<Item> INFERNO_SHARD = ITEMS.registerItem(
        "inferno_shard",
        properties -> new FuelItem(properties, 1000000),
        new Item.Properties()
    );
    public static final DeferredItem<Item> INFERNO_MATTER = ITEMS.registerItem(
        "inferno_matter",
        properties -> new FuelItem(properties, 10000000),
        new Item.Properties()
    );

    public static void register(IEventBus modEventBus) {
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
    }
}
