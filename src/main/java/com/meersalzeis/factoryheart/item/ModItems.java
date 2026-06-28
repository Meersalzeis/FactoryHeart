package com.meersalzeis.factoryheart.item;

import net.neoforged.bus.api.IEventBus;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.item.custom.VolatileBlazeRod;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FHModMain.MOD_ID);

    public static final DeferredItem<Item> ABZERO_MATTER = ITEMS.registerSimpleItem("abzero_matter");
    public static final DeferredItem<Item> ACTIVATED_BLAZE_ROD = ITEMS.registerSimpleItem("activated_blaze_rod");
    public static final DeferredItem<Item> BLAZEAR_FUEL_ROD = ITEMS.registerSimpleItem("blazear_fuel_rod");
    public static final DeferredItem<Item> BLAZEAR_PELLET = ITEMS.registerSimpleItem("blazear_pellet");
    public static final DeferredItem<Item> CONTAINED_DEEPCOOL_CRYSTAL = ITEMS.registerSimpleItem("contained_deepcool_crystal");
    public static final DeferredItem<Item> COPPER_WIRE = ITEMS.registerSimpleItem("copper_wire"); //craftable
    public static final DeferredItem<Item> DEEPCOOL_CRYSTAL = ITEMS.registerSimpleItem("deepcool_crystal"); //nj extractable
    public static final DeferredItem<Item> EMPTY_CAN = ITEMS.registerSimpleItem("empty_can"); //nj wrappable
    public static final DeferredItem<Item> ENERGY_DRINK = ITEMS.registerSimpleItem("energy_drink"); //craftable
    public static final DeferredItem<Item> ENERGY_POWDER = ITEMS.registerSimpleItem("energy_powder"); //craftable
    public static final DeferredItem<Item> FACTORY_PASTE = ITEMS.registerSimpleItem("factory_paste"); //craftable
    public static final DeferredItem<Item> FACTORY_TOOTH = ITEMS.registerSimpleItem("factory_tooth"); //craftable
    public static final DeferredItem<Item> INFERNO_MATTER = ITEMS.registerSimpleItem("inferno_matter");
    public static final DeferredItem<Item> METAL_DISC = ITEMS.registerSimpleItem("metal_disc"); // craftable
    public static final DeferredItem<Item> METAL_SHEET = ITEMS.registerSimpleItem("metal_sheet"); // craftable
    public static final DeferredItem<Item> SODA_POWDER = ITEMS.registerSimpleItem("soda_powder"); // craftable

    public static final DeferredItem<Item> CONTAINMENT_SPOOL = ITEMS.registerSimpleItem("containment_spool");
    public static final DeferredItem<Item> CONTAINMENT_FIELD_PROJECTOR = ITEMS.registerSimpleItem("containment_field_projector"); // craftable
    public static final DeferredItem<Item> CONTAINMENT_CASING = ITEMS.registerSimpleItem("containment_casing");


    public static final DeferredItem<Item> SMART_SHARD = ITEMS.registerSimpleItem("smart_shard");
    public static final DeferredItem<Item> SMART_PART = ITEMS.registerSimpleItem("smart_part");
    public static final DeferredItem<Item> SMART_HEART = ITEMS.registerSimpleItem("smart_heart");
    public static final DeferredItem<Item> HARD_PART = ITEMS.registerSimpleItem("hard_part");

    public static final DeferredItem<Item> SMART_SHARD_U = ITEMS.registerSimpleItem("smart_shard_unidentified");
    public static final DeferredItem<Item> SMART_PART_U = ITEMS.registerSimpleItem("smart_part_unidentified");
    public static final DeferredItem<Item> SMART_HEART_U = ITEMS.registerSimpleItem("smart_heart_unidentified");
    public static final DeferredItem<Item> HARD_PART_U = ITEMS.registerSimpleItem("hard_part_unidentified");

    public static final DeferredItem<Item> SMART_SHARD_F = ITEMS.registerSimpleItem("smart_shard_faulty");
    public static final DeferredItem<Item> SMART_PART_F = ITEMS.registerSimpleItem("smart_part_faulty");
    public static final DeferredItem<Item> SMART_HEART_F = ITEMS.registerSimpleItem("smart_heart_faulty");
    public static final DeferredItem<Item> HARD_PART_F = ITEMS.registerSimpleItem("hard_part_faulty");


    public static final DeferredItem<Item> VOLATILE_BLAZE_ROD = ITEMS.register(
        "volatile_blaze_rod",
        () -> new VolatileBlazeRod(new Item.Properties())
    );

    public static void register(IEventBus modEventBus) {
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
    }
}
