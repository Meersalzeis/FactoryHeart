package com.meersalzeis.factoryheart.block.custom;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;

public class FactoryWrapperBlock extends RotatedBlockProvider {

    public FactoryWrapperBlock(Block block) {
        super(block);
    }
    
}
