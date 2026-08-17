package com.meersalzeis.factoryheart.block.hearting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.blockentity.FactoryHeartBlockEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIER).add(FACING);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide()) return;

        if (oldState.is(state.getBlock())) {
            return;
        }

        // Be aware this call can cause heart conflicts and thus instantly break the heart again
        HeartBeating.TryAddBlock(level, pos, true);
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


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(
            pBlockEntityType, ModBlockEntities.HEART_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pPos, pState1)
        );
    }

    @Override
    public void animateTick(BlockState state, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        double xPos = pPos.getX() + 0.5f;
        double yPos = pPos.getY() + 1.0f;
        double zPos = pPos.getZ() + 0.5f;
        double offset = pRandom.nextDouble() * 0.8 - 0.6;

        int tier = state.getValue(TIER);
        
        if (tier == 1 || tier == 3 || tier == 4) {
            pLevel.addParticle(ParticleTypes.SMOKE, xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
        }
        if (tier == 2 || tier == 3) {
            pLevel.addParticle(ParticleTypes.FLAME, xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
        }
        if (tier == 4) {
            pLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
        }
    }

    public static int getLightLevel(BlockState state) {
        return state.getValue(TIER) * 2;
    }

    // Comparator interaction
    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    // Comparator interaction
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FactoryHeartBlockEntity myBE) {
            return myBE.getComparatorOutput();
        }
        return 0;
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
