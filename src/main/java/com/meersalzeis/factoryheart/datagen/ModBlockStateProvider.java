package com.meersalzeis.factoryheart.datagen;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.custom.FactoryHeartBlock;
import com.meersalzeis.factoryheart.block.custom.FactoryMawBlock;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FHModMain.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.FACTORY_VEIN);
        //blockItem(ModBlocks.FACTORY_HEART, "_t0");

        blockWithItem(ModBlocks.WRAPPER);
        blockWithItem(ModBlocks.EXTRACTOR);
        blockWithItem(ModBlocks.TESTER);

        ModelFile factoryMawModel = models().cube(
            "factory_maw",
            mcLoc("block/blast_furnace_top"),
            mcLoc("block/blast_furnace_top"),
            modLoc("block/factory_maw_front"),
            mcLoc("block/blast_furnace_side"),
            mcLoc("block/blast_furnace_side"),
            mcLoc("block/blast_furnace_side")
            
        );

        directionalBlockCustomSides(factoryMawModel);

        AddFactoryHeart();

        blockItem(ModBlocks.CRYSTALLIZER);

        generateFromBlockbenchModel("blazer", ModBlocks.BLAZER.get(), modLoc("block/blazer"));
        generateFromBlockbenchModel("condenser", ModBlocks.CONDENSER.get(), modLoc("block/condenser"));
        //directionalBlock(ModBlocks.FACTORY_MAW.get());
    }

    private void blockWithItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("factoryheart:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("factoryheart:block/" + deferredBlock.getId().getPath() + appendix));
    }


    private void AddFactoryHeart() {
        getVariantBuilder(ModBlocks.FACTORY_HEART.get()).forAllStates(state -> {
            int tier = state.getValue(FactoryHeartBlock.TIER);
            ModelFile factoryHeartModel = models().cubeColumn(
                "factory_heart_tier" + tier,
                modLoc("block/factory_heart_t"+tier),
                modLoc("block/factory_standard")
            );
            return new ConfiguredModel[]{new ConfiguredModel(factoryHeartModel)};
        });

        // Item Model for Block in Inventories
        simpleBlockItem(ModBlocks.FACTORY_HEART.get(), models().cubeColumn(
                "factory_heart_t0",
                modLoc("block/factory_heart_t0"),
                modLoc("block/factory_standard"))
        );
    }

    private void directionalBlockCustomSides(ModelFile model) {
        getVariantBuilder(ModBlocks.FACTORY_MAW.get())
        .forAllStates(state -> {

            Direction dir = state.getValue(FactoryMawBlock.FACING);

            int xRot = switch (dir) {
                case DOWN -> 90;
                case UP -> 270;
                default -> 0;
            };

            int yRot = switch (dir) {
                case NORTH -> 0;
                case SOUTH -> 180;
                case WEST -> 270;
                case EAST -> 90;
                default -> 0;
            };

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });
    }

    private void generateFromBlockbenchModel(String name, Block block, ResourceLocation modelLocation) {
        ModelFile model = models().getExistingFile(modelLocation);

        getVariantBuilder(block)
            .forAllStates(state -> {
                Direction facing = state.getValue(BlockStateProperties.FACING);

                int xRot;
                int yRot;

                switch (facing) {
                    case DOWN -> {
                        xRot = 90;
                        yRot = 0;
                    }
                    case UP -> {
                        xRot = 270;
                        yRot = 0;
                    }
                    case NORTH -> {
                        xRot = 0;
                        yRot = 0;
                    }
                    case SOUTH -> {
                        xRot = 0;
                        yRot = 180;
                    }
                    case WEST -> {
                        xRot = 0;
                        yRot = 270;
                    }
                    case EAST -> {
                        xRot = 0;
                        yRot = 90;
                    }
                    default -> throw new IllegalStateException();
                }

                return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });

        // Item Model for Block in Inventories
        //blockItem(ModBlocks.FACTORY_HEART);
        itemModels().withExistingParent(
            name,
            modelLocation
        );
    }
}
