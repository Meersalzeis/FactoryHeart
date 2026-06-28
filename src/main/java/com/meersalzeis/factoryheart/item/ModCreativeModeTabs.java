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
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModBlocks.BLAZER);
                        pOutput.accept(ModBlocks.WRAPPER);
                        pOutput.accept(ModBlocks.EXTRACTOR);
                        pOutput.accept(ModBlocks.TESTER);
                        
                        pOutput.accept(ModBlocks.FACTORY_MAW);
                        pOutput.accept(ModBlocks.FACTORY_VEIN);
                        pOutput.accept(ModBlocks.FACTORY_HEART);

                        pOutput.accept(ModBlocks.CRYSTALLIZER);

                        pOutput.accept(ModItems.ABZERO_MATTER);
                        pOutput.accept(ModItems.ACTIVATED_BLAZE_ROD);
                        pOutput.accept(ModItems.BLAZEAR_FUEL_ROD);
                        pOutput.accept(ModItems.BLAZEAR_PELLET);
                        pOutput.accept(ModItems.CONTAINED_DEEPCOOL_CRYSTAL);
                        pOutput.accept(ModItems.COPPER_WIRE);
                        pOutput.accept(ModItems.DEEPCOOL_CRYSTAL);
                        pOutput.accept(ModItems.EMPTY_CAN);
                        pOutput.accept(ModItems.ENERGY_DRINK);
                        pOutput.accept(ModItems.ENERGY_POWDER);
                        pOutput.accept(ModItems.FACTORY_PASTE);
                        pOutput.accept(ModItems.FACTORY_TOOTH);
                        pOutput.accept(ModItems.INFERNO_MATTER);
                        pOutput.accept(ModItems.METAL_DISC);
                        pOutput.accept(ModItems.METAL_SHEET);
                        pOutput.accept(ModItems.SODA_POWDER);

                        pOutput.accept(ModItems.VOLATILE_BLAZE_ROD);

                        pOutput.accept(ModItems.CONTAINMENT_SPOOL);
                        pOutput.accept(ModItems.CONTAINMENT_FIELD_PROJECTOR);
                        pOutput.accept(ModItems.CONTAINMENT_CASING);

                        pOutput.accept(ModItems.SMART_SHARD);
                        pOutput.accept(ModItems.SMART_PART );
                        pOutput.accept(ModItems.SMART_HEART);
                        pOutput.accept(ModItems.HARD_PART);

                        pOutput.accept(ModItems.SMART_SHARD_U);
                        pOutput.accept(ModItems.SMART_PART_U );
                        pOutput.accept(ModItems.SMART_HEART_U);
                        pOutput.accept(ModItems.HARD_PART_U);

                        pOutput.accept(ModItems.SMART_SHARD_F);
                        pOutput.accept(ModItems.SMART_PART_F );
                        pOutput.accept(ModItems.SMART_HEART_F);
                        pOutput.accept(ModItems.HARD_PART_F);

                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
