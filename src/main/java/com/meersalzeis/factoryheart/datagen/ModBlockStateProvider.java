package com.meersalzeis.factoryheart.datagen;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.block.hearting.FactoryHeartBlock;
import com.meersalzeis.factoryheart.block.hearting.FactoryMawBlock;
import com.meersalzeis.factoryheart.block.hearting.FactoryVeinBlock;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FHModMain.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.FACTORY_SKIN);


        // ModelFile factoryMawModel = models().cube(
        //     "factory_maw",
        //     mcLoc("block/blast_furnace_top"),
        //     mcLoc("block/blast_furnace_top"),
        //     modLoc("block/factory_maw_front"),
        //     mcLoc("block/blast_furnace_side"),
        //     mcLoc("block/blast_furnace_side"),
        //     mcLoc("block/blast_furnace_side")
            
        // );

        // directionalBlockCustomSides(factoryMawModel);

        vein();

        simpleBlockItem(
                ModBlocks.FACTORY_VEIN.get(),
                new ModelFile.UncheckedModelFile(
                        modLoc("block/vein_core")
                )
        );

        addFactoryHeart();
        addWrapperBlock();

        generateFromBlockbenchModel("blazer", ModBlocks.BLAZER.get(), modLoc("block/blazer"));
        generateFromBlockbenchModel("extractor", ModBlocks.EXTRACTOR.get(), modLoc("block/extractor"));
        generateFromBlockbenchModel("tester", ModBlocks.TESTER.get(), modLoc("block/tester"));
        generateFromBlockbenchModel("condenser", ModBlocks.CONDENSER.get(), modLoc("block/condenser"));
        generateFromBlockbenchModel("factory_maw", ModBlocks.FACTORY_MAW.get(), modLoc("block/factory_maw"));
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


    private void addFactoryHeart() {
        getVariantBuilder(ModBlocks.FACTORY_HEART.get()).forAllStates(state -> {
            int tier = state.getValue(FactoryHeartBlock.TIER);
            ModelFile factoryHeartModel = models().cubeColumn(
                "factory_heart_tier" + tier,
                modLoc("block/factory_heart_t"+tier),
                modLoc("block/factory_skin")
            );
            return new ConfiguredModel[]{new ConfiguredModel(factoryHeartModel)};
        });

        // Item Model for Block in Inventories
        simpleBlockItem(ModBlocks.FACTORY_HEART.get(), models().cubeColumn(
                "factory_heart_t0",
                modLoc("block/factory_heart_t0"),
                modLoc("block/factory_skin"))
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
        itemModels().withExistingParent(
            name,
            modelLocation
        );
    }

    private void addWrapperBlock() {
        ModelFile model = models().cubeBottomTop(
            "wrapper",
            modLoc("block/wrapper_side"),
            modLoc("block/wrapper_bottom"),
            modLoc("block/wrapper_top")
        );

        simpleBlock(ModBlocks.WRAPPER.get(), model);
        simpleBlockItem(ModBlocks.WRAPPER.get(), model);
    }

    private void vein() {
        Block veinBlock = ModBlocks.FACTORY_VEIN.get();
        ResourceLocation coreModLoc = modLoc("block/vein_core");

        ModelFile core = models().getExistingFile(coreModLoc);
        ModelFile arm = models().getExistingFile(modLoc("block/vein_arm"));
        MultiPartBlockStateBuilder builder = getMultipartBuilder(veinBlock);

        // Always render the core
        builder.part()
                .modelFile(core)
                .addModel();

        addArm(builder, FactoryVeinBlock.NORTH, arm, 0, 0);
        addArm(builder, FactoryVeinBlock.EAST,  arm, 0, 90);
        addArm(builder, FactoryVeinBlock.SOUTH, arm, 0, 180);
        addArm(builder, FactoryVeinBlock.WEST,  arm, 0, 270);
        addArm(builder, FactoryVeinBlock.UP,    arm, 270, 0);
        addArm(builder, FactoryVeinBlock.DOWN,  arm, 90, 0);

        // Item Model for Block in Inventories
        itemModels().withExistingParent(
            "factory_vein",
            coreModLoc
        );
    }

    private void addArm(
            MultiPartBlockStateBuilder builder,
            BooleanProperty property,
            ModelFile model,
            int rotationX,
            int rotationY
    ) {
        builder.part()
                .modelFile(model)
                .rotationX(rotationX)
                .rotationY(rotationY)
                .addModel()
                .condition(property, true);
    }

    // private void vein() {
    //     String name = "factory_vein";
    //     Block block = ModBlocks.FACTORY_VEIN.get();

    //     ModelFile core = models().getExistingFile(modLoc("block/vein_core"));
    //     ModelFile arm = models().getExistingFile(modLoc("block/vein_arm"));

    //     getVariantBuilder(block)
    //         .forAllStates(state -> {
    //             List<ConfiguredModel> models = new ArrayList<>();
                
    //             Collections.addAll(models,ConfiguredModel.builder().modelFile(core).build());

    //             if (state.getValue(FactoryVeinBlock.NORTH)) {
    //                 Collections.addAll(models,ConfiguredModel.builder().modelFile(arm).rotationY(0).build());
    //             }
    //             if (state.getValue(FactoryVeinBlock.EAST)) {
    //                 Collections.addAll(models,ConfiguredModel.builder().modelFile(arm).rotationY(90).build());
    //             }
    //             if (state.getValue(FactoryVeinBlock.SOUTH)) {
    //                 Collections.addAll(models,ConfiguredModel.builder().modelFile(arm).rotationY(180).build());
    //             }
    //             if (state.getValue(FactoryVeinBlock.WEST)) {
    //                 Collections.addAll(models,ConfiguredModel.builder().modelFile(arm).rotationY(270).build());
    //             }
    //             if (state.getValue(FactoryVeinBlock.DOWN)) {
    //                 Collections.addAll(models,ConfiguredModel.builder().modelFile(arm).rotationX(90).build());
    //             }
    //             if (state.getValue(FactoryVeinBlock.UP)) {
    //                 Collections.addAll(models,ConfiguredModel.builder().modelFile(arm).rotationX(270).build());
    //             }

    //             return models.toArray(new ConfiguredModel[0]);
    //     });

    //     // Item Model for Block in Inventories
    //     itemModels().withExistingParent(
    //         name,
    //         modLoc("block/vein_core")
    //     );
    // }
}
