package com.meersalzeis.factoryheart.block.hearting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartNetwork;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;


public class FactoryHeartBlock extends DirectionalBlock {
    
    public static final MapCodec<FactoryMawBlock> CODEC = simpleCodec(FactoryMawBlock::new);
    public static final DirectionProperty FACING =  BlockStateProperties.FACING;
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 4);

    public FactoryHeartBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(TIER, 0)
            .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(TIER);
    }

    // @Override
    // protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    //     if (level.isClientSide()) return;
    //     // Be aware this call can cause heart conflicts and thus instantly break the heart again
    //     HeartBeating.AddBlock(level, pos, true);
    // }

    public static int getTier(BlockState state) {
        return state.getValue(TIER);
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    // @Override
    // protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    //     super.useWithoutItem(state, level, pos, player, hitResult);
        
    //     if (level.isClientSide()) return InteractionResult.SUCCESS;

    //     HeartNetwork netw = HeartNetwork.GetNetworkOrNew(level, pos);
    //     String message = "Tier:" + netw.GetCurrentTier() + ", fuel:" + netw.fuelGauge + ", coolant:" + netw.coolantGauge;
    //     FHModClient.debugMessageToAll(message, false);
    //     return InteractionResult.SUCCESS;
    // }


    // @Override
    // protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    //     if (level.isClientSide()) return;

    //     HeartNetwork.DoTickFor(level, pos);
        
    //     int curTier = HeartNetwork.GetTierOf(level, pos);
    //     if (state.getValue(TIER) != curTier) {
    //         level.setBlock(pos, state.setValue(TIER, curTier), 3);
    //     }
    // }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide()) return;

        if (state.is(newState.getBlock())) {
            // Only state change, no "actual" removal
            return;
        }

        HeartBeating.DeregisterBlock(level, pos, true);
    }
}
