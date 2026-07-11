package com.meersalzeis.factoryheart.block.hearting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.blockentity.FactoryHeartBlockEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;


public class FactoryHeartBlock extends BaseEntityBlock {
    
    public static final MapCodec<FactoryHeartBlock> CODEC = simpleCodec(FactoryHeartBlock::new);
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

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide()) return;
        // Be aware this call can cause heart conflicts and thus instantly break the heart again
        HeartBeating.AddBlock(level, pos, true);
    }

    public static int getTier(BlockState state) {
        return state.getValue(TIER);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        String message = ((FactoryHeartBlockEntity) be).toString();
        FHModClient.debugMessageToAll(message, false);
        return InteractionResult.SUCCESS;
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(
            pBlockEntityType, ModBlockEntities.HEART_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide()) return;

        if (state.is(newState.getBlock())) {
            // Only state change, no "actual" removal
            return;
        }

        HeartBeating.DeregisterBlock(level, pos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FactoryHeartBlockEntity(pos, state);
    }
}
