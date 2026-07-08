package com.meersalzeis.factoryheart.block.entity;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.entity.crafting.CrystallizerBlockEntity;
import com.meersalzeis.factoryheart.block.entity.crafting.ExtractorBlockEntity;
import com.meersalzeis.factoryheart.block.entity.crafting.WrapperBlockEntity;
import com.meersalzeis.factoryheart.block.entity.custom.CondenserBlockEntity;
import com.meersalzeis.factoryheart.block.entity.crafting.TesterBlockEntity;
import com.meersalzeis.factoryheart.block.entity.crafting.BlazerBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
                DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FHModMain.MOD_ID);

        public static final Supplier<BlockEntityType<CrystallizerBlockEntity>> CRYSTALLIZER_BE =
                BLOCK_ENTITIES.register("crystallizer_be", () -> BlockEntityType.Builder.of(
                        CrystallizerBlockEntity::new, ModBlocks.CRYSTALLIZER.get()).build(null));

        public static final Supplier<BlockEntityType<BlazerBlockEntity>> BLAZER_BE =
                BLOCK_ENTITIES.register("blazer_be", () -> BlockEntityType.Builder.of(
                        BlazerBlockEntity::new, ModBlocks.BLAZER.get()).build(null));

        public static final Supplier<BlockEntityType<WrapperBlockEntity>> WRAPPER_BE =
                BLOCK_ENTITIES.register("wrapper_be", () -> BlockEntityType.Builder.of(
                        WrapperBlockEntity::new, ModBlocks.WRAPPER.get()).build(null));

        public static final Supplier<BlockEntityType<ExtractorBlockEntity>> EXTRACTOR_BE =
                BLOCK_ENTITIES.register("extractor_be", () -> BlockEntityType.Builder.of(
                        ExtractorBlockEntity::new, ModBlocks.EXTRACTOR.get()).build(null));

        public static final Supplier<BlockEntityType<TesterBlockEntity>> TESTER_BE =
                BLOCK_ENTITIES.register("tester_be", () -> BlockEntityType.Builder.of(
                        TesterBlockEntity::new, ModBlocks.TESTER.get()).build(null));

        public static final Supplier<BlockEntityType<CondenserBlockEntity>> CONDENSER_BE =
                BLOCK_ENTITIES.register("condenser_be", () -> BlockEntityType.Builder.of(
                        CondenserBlockEntity::new, ModBlocks.CONDENSER.get()).build(null));

        public static void register(IEventBus eventBus) {
                BLOCK_ENTITIES.register(eventBus);
        }
}
