package com.meersalzeis.factoryheart.block.hearting;

import com.meersalzeis.factoryheart.hearts.HeartBeating;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FactorySkinBlock extends Block {


    public FactorySkinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (level.isClientSide()) return;
        if (oldState.is(state.getBlock())) {return;}
        HeartBeating.TryAddBlock(level, pos, false);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide()) return;
        if (state.is(newState.getBlock())) {return;}
        HeartBeating.DeregisterBlock(level, pos);
    }
}