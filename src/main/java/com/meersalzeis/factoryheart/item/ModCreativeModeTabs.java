package com.meersalzeis.factoryheart.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FHModMain.MOD_ID);

    public static final Supplier<CreativeModeTab> factoryheart_ALL_TAB =
            CREATIVE_MODE_TABS.register("factoryheart_all_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.factoryheart.factoryheart_all_tab"))
                    .icon(() -> new ItemStack(ModBlocks.FACTORY_MAW))
                    .displayItems((pParameters, output) -> {

                        output.accept(ModBlocks.BLAZER);
                        output.accept(ModBlocks.WRAPPER);
                        output.accept(ModBlocks.EXTRACTOR);
                        output.accept(ModBlocks.TESTER);
                        output.accept(ModBlocks.CONDENSER);
                        
                        output.accept(ModBlocks.FACTORY_MAW);
                        output.accept(ModBlocks.FACTORY_HEART);

                        output.accept(ModBlocks.FACTORY_VEIN);
                        output.accept(ModBlocks.FACTORY_SKIN);

                        output.accept(ModItems.ABZERO_MATTER);
                        output.accept(ModItems.ACTIVATED_BLAZEAR_ROD);
                        output.accept(ModItems.BLAZEAR_FUEL_ROD);
                        output.accept(ModItems.BLAZEAR_PELLET);
                        output.accept(ModItems.CONTAINED_DEEPCOOL_CRYSTAL);
                        output.accept(ModItems.DEEPCOOL_CRYSTAL);
                        output.accept(ModItems.EMPTY_CAN);
                        output.accept(ModItems.ENERGY_DRINK);
                        output.accept(ModItems.ENERGY_POWDER);
                        output.accept(ModItems.FACTORY_PASTE);
                        output.accept(ModItems.FACTORY_TOOTH);
                        output.accept(ModItems.INFERNO_MATTER);
                        output.accept(ModItems.METAL_DISC);
                        output.accept(ModItems.METAL_SHEET);
                        output.accept(ModItems.SODA_POWDER);

                        output.accept(ModItems.VOLATILE_BLAZEAR_ROD);

                        output.accept(ModItems.CONTAINMENT_SPOOL);
                        output.accept(ModItems.CONTAINMENT_FIELD_PROJECTOR);
                        output.accept(ModItems.CONTAINMENT_CASING);

                        output.accept(ModItems.PLUNGER);

                        output.accept(ModItems.COMBUSTION_CORE);
                        output.accept(ModItems.RAW_BLAZE_CANDY);
                        output.accept(ModItems.UNWRAPPED_BLAZE_CANDY);
                        output.accept(ModItems.BLAZE_CANDY);

                        output.accept(ModItems.ABZERO_CRYSTAL);
                        output.accept(ModItems.ABZERO_SHARD);
                        output.accept(ModItems.INFERNO_SHARD);

                        output.accept(ModItems.SMART_SHARD);
                        output.accept(ModItems.SMART_PART );
                        output.accept(ModItems.SMART_HEART);
                        output.accept(ModItems.HARD_PART);

                        output.accept(ModItems.SMART_SHARD_U);
                        output.accept(ModItems.SMART_PART_U );
                        output.accept(ModItems.SMART_HEART_U);
                        output.accept(ModItems.HARD_PART_U);

                        output.accept(ModItems.SMART_SHARD_F);
                        output.accept(ModItems.SMART_PART_F );
                        output.accept(ModItems.SMART_HEART_F);
                        output.accept(ModItems.HARD_PART_F);

                        output.accept(ModItems.SLIMY_ROTTEN);
                        output.accept(ModItems.LEATHERY_ROTTEN);

                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
