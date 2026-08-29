package com.meersalzeis.factoryheart.block.hearting;
    
import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.util.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// Copious copying from Powah mod: https://github.com/Technici4n/Powah/tree/26.1/src/main/java/owmii/powah/block/cable

public class FactoryVeinBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;

    private static final VoxelShape CABLE_CORE = box(5,5,5,11,11,11);
    private static final VoxelShape[] MULTIPART = new VoxelShape[] {
        box(6, 6, 0, 10, 10, 8),
        box(10, 6, 6, 16, 10, 10),
        box(6, 6, 10, 10, 10, 16),
        box(0, 6, 6, 6, 10, 10),
        box(6, 10, 6, 10, 16, 10),
        box(6, 0, 6, 10, 8, 10)
    };

    public FactoryVeinBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        VoxelShape voxelShape = CABLE_CORE;
        if (blockGetter instanceof Level world) {
            if (state.getValue(NORTH) || canLinkTo(world, pos.north(), Direction.NORTH))
                voxelShape = Shapes.or(voxelShape, MULTIPART[0]);
            if (state.getValue(EAST) || canLinkTo(world, pos.east(), Direction.EAST))
                voxelShape = Shapes.or(voxelShape, MULTIPART[1]);
            if (state.getValue(SOUTH) || canLinkTo(world, pos.south(), Direction.SOUTH))
                voxelShape = Shapes.or(voxelShape, MULTIPART[2]);
            if (state.getValue(WEST) || canLinkTo(world, pos.west(), Direction.WEST))
                voxelShape = Shapes.or(voxelShape, MULTIPART[3]);
            if (state.getValue(UP) || canLinkTo(world, pos.above(), Direction.UP))
                voxelShape = Shapes.or(voxelShape, MULTIPART[4]);
            if (state.getValue(DOWN) || canLinkTo(world, pos.below(), Direction.DOWN))
                voxelShape = Shapes.or(voxelShape, MULTIPART[5]);
        }
        return voxelShape;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        var newState = createVeinState(level, pos);
        if (! newState.equals(state)) {
            // update all bc. veins change collision shapes which the server needs to know as well
            level.setBlock(pos, newState, Block.UPDATE_ALL);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return createVeinState(context.getLevel(), context.getClickedPos());
    }

    private BlockState createVeinState(Level world, BlockPos pos) {
        final BlockState state = defaultBlockState();
        boolean north = canLinkTo(world, pos.north(), Direction.NORTH);
        boolean south = canLinkTo(world, pos.south(), Direction.SOUTH);
        boolean west = canLinkTo(world, pos.west(), Direction.WEST);
        boolean east = canLinkTo(world, pos.east(), Direction.EAST);
        boolean up = canLinkTo(world, pos.above(), Direction.UP);
        boolean down = canLinkTo(world, pos.below(), Direction.DOWN);
        //FluidState fluidState = world.getFluidState(pos);
        return state.setValue(NORTH, north).setValue(SOUTH, south).setValue(WEST, west)
                .setValue(EAST, east).setValue(UP, up).setValue(DOWN, down)
                //.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER)
                ;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.setBlock(pos, createVeinState(level, pos), Block.UPDATE_ALL);

        if (level.isClientSide()) return;
        if (oldState.is(state.getBlock())) {return;}
        HeartBeating.TryAddBlock(level, pos, false);
    }

    public boolean canLinkTo(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos).is(ModTags.Blocks.HEART_NETWORK_BLOCKS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
        super.createBlockStateDefinition(builder);
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