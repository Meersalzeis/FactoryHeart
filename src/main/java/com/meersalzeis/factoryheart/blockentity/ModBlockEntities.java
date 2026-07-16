package com.meersalzeis.factoryheart.blockentity;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.blockentity.crafting.BlazerBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.CondenserBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.ExtractorBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.TesterBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.WrapperBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
                DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FHModMain.MOD_ID);

        public static void register(IEventBus eventBus) { BLOCK_ENTITIES.register(eventBus); }

                        
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


        public static final Supplier<BlockEntityType<FactoryHeartBlockEntity>> HEART_BE =
                BLOCK_ENTITIES.register("heart_be", () -> BlockEntityType.Builder.of(
                        FactoryHeartBlockEntity::new, ModBlocks.FACTORY_HEART.get()).build(null));
}
