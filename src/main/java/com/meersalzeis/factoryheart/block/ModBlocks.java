package com.meersalzeis.factoryheart.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.custom.CrystallizerBlock;
import com.meersalzeis.factoryheart.block.custom.FactoryHeartBlock;
import com.meersalzeis.factoryheart.block.custom.BlazerBlock;
import com.meersalzeis.factoryheart.block.custom.FactoryMawBlock;
import com.meersalzeis.factoryheart.item.ModItems;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =  DeferredRegister.createBlocks(FHModMain.MOD_ID);

    public static final DeferredBlock<Block> BLAZER = registerBlock("blazer",
        () -> new BlazerBlock(BlockBehaviour.Properties.of().noOcclusion())
    );

    public static final DeferredBlock<Block> WRAPPER = registerBlock("wrapper",
        () -> new Block(BlockBehaviour.Properties.of().strength(3f))
    );

    // RotatedPillarBlock ?
    public static final DeferredBlock<Block> EXTRACTOR = registerBlock("extractor",
        () -> new Block(BlockBehaviour.Properties.of().strength(3f))
    );

    // RotatedPillarBlock ?
    public static final DeferredBlock<Block> TESTER = registerBlock("tester",
        () -> new Block(BlockBehaviour.Properties.of().strength(3f))
    );


    public static final DeferredBlock<Block> FACTORY_VEIN = registerBlock("factory_vein",
        () -> new Block(BlockBehaviour.Properties.of().strength(3f))
    );

    public static final DeferredBlock<Block> FACTORY_MAW = registerBlock("factory_maw",
        () -> new FactoryMawBlock(BlockBehaviour.Properties.of().strength(3f))
    );

    public static final DeferredBlock<FactoryHeartBlock> FACTORY_HEART = registerBlock("factory_heart",
        () -> new FactoryHeartBlock(BlockBehaviour.Properties.of().strength(3f))
    );

    public static final DeferredBlock<Block> CRYSTALLIZER = registerBlock("crystallizer",
            () -> new CrystallizerBlock(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));


    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
