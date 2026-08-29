package com.meersalzeis.factoryheart.block.hearting;

import com.mojang.serialization.MapCodec;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.Entity;

public class FactoryMawBlock extends DirectionalBlock {
    
    public static final MapCodec<FactoryMawBlock> CODEC = simpleCodec(FactoryMawBlock::new);
    public static final DirectionProperty FACING =  BlockStateProperties.FACING;

    public static final VoxelShape SHAPE_DOWN = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_UP = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    public static final VoxelShape SHAPE_NORTH = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_WEST = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_SOUTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    public static final VoxelShape SHAPE_EAST = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);

    public FactoryMawBlock(Properties pProperties) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case Direction.UP:
                return SHAPE_UP;
            case Direction.DOWN:
                return SHAPE_DOWN;
            case Direction.NORTH:
                return SHAPE_NORTH;
            case Direction.WEST:
                return SHAPE_WEST;
            case Direction.SOUTH:
                return SHAPE_SOUTH;
            default: return SHAPE_EAST; 
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide()) return;
        if(entity instanceof ItemEntity itemEntity) {
            HeartBeating.MawGetsItemFed(level, pos, itemEntity);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide()) return;
        if (oldState.is(state.getBlock())) {return;}
        HeartBeating.TryAddBlock(level, pos, false);
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
}
