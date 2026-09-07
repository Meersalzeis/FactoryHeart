package com.meersalzeis.factoryheart;

// import java.util.List;

// import net.minecraft.core.registries.BuiltInRegistries;
// import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // public static final ModConfigSpec.BooleanValue ALT_TEXTURES = BUILDER
    //     .comment("Whether to use the alternative cyber textures")
    //     .define("cyberLook", false);

    // =============== MAW BASICS ===============

    public static final ModConfigSpec.IntValue RESOURCE_PER_ITEM = BUILDER
        .comment("How long (in ticks) an item fed to a factorymaw lasts.")
        .defineInRange("ticks_per_item", 800, 1, Integer.MAX_VALUE);
    
    public static final ModConfigSpec.IntValue MAX_RESOURCE = BUILDER
        .comment("How long (in ticks) the full tank of a factoryheart lasts.")
        .defineInRange("max_ticks", 2400, 1, Integer.MAX_VALUE);

    // // =============== FUELS ===============

    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_1_FUEL_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as  fuel at tier 1.")
    //     .defineList("fuels_t1", List.of("minecraft:coal", "minecraft:charcoal", "minecraft:dried_kelp_block"), () -> "", Config::validateItemName);

    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_2_FUEL_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as fuel at tier 2.")
    //     .defineList("fuels_t2", List.of("factoryheart:blazear_pellet", "create:blaze_cake"), () -> "", Config::validateItemName);

    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_3_FUEL_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as fuel at tier 3.")
    //     .defineList("fuels_t3", List.of("factoryheart:blazear_fuel_rod"), () -> "", Config::validateItemName);
    
    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_4_FUEL_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as fuel at tier 4.")
    //     .defineList("fuels_t4", List.of("factoryheart:inferno_matter"), () -> "", Config::validateItemName);
    
    // // =============== COOLANTS ===============

    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_1_COOLANT_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as coolant at tier 1.")
    //     .defineList("coolants_t1", List.of(), () -> "", Config::validateItemName);

    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_2_COOLANT_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as coolant at tier 2.")
    //     .defineList("coolants_t2", List.of("minecraft:snowball"), () -> "", Config::validateItemName);

    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_3_COOLANT_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as coolant at tier 3.")
    //     .defineList("coolants_t3", List.of("factoryheart:contained_deepcool_crystal"), () -> "", Config::validateItemName);
    
    // public static final ModConfigSpec.ConfigValue<List<? extends String>> TIER_4_COOLANT_ITEMS = BUILDER
    //     .worldRestart()    
    //     .comment("A list of items to use as coolant at tier 4.")
    //     .defineList("coolants_t4", List.of("factoryheart:abzero_matter"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    // private static boolean validateItemName(final Object obj) {
    //     return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    // }
}
