package com.meersalzeis.factoryheart.block.hearting;
    
import com.meersalzeis.factoryheart.util.ModTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

    private static final VoxelShape CABLE_CORE = box(6.25, 6.25, 6.25, 9.75, 9.75, 9.75);
    private static final VoxelShape[] MULTIPART = new VoxelShape[] {
        box(6.5, 6.5, 0, 9.5, 9.5, 7),
        box(9.5, 6.5, 6.5, 16, 9.5, 9.5),
        box(6.5, 6.5, 9.5, 9.5, 9.5, 16),
        box(0, 6.5, 6.5, 6.5, 9.5, 9.5),
        box(6.5, 9.5, 6.5, 9.5, 16, 9.5),
        box(6.5, 0, 6.5, 9.5, 7, 9.5)
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
            if (state.getValue(NORTH) || canSupply(world, pos, Direction.NORTH))
                voxelShape = Shapes.or(voxelShape, MULTIPART[0]);
            if (state.getValue(EAST) || canSupply(world, pos, Direction.EAST))
                voxelShape = Shapes.or(voxelShape, MULTIPART[1]);
            if (state.getValue(SOUTH) || canSupply(world, pos, Direction.SOUTH))
                voxelShape = Shapes.or(voxelShape, MULTIPART[2]);
            if (state.getValue(WEST) || canSupply(world, pos, Direction.WEST))
                voxelShape = Shapes.or(voxelShape, MULTIPART[3]);
            if (state.getValue(UP) || canSupply(world, pos, Direction.UP))
                voxelShape = Shapes.or(voxelShape, MULTIPART[4]);
            if (state.getValue(DOWN) || canSupply(world, pos, Direction.DOWN))
                voxelShape = Shapes.or(voxelShape, MULTIPART[5]);
        }
        return voxelShape;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        var newState = createVeinState(level, pos);

        if (newState != state) {
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
        boolean north = canSupply(world, pos, Direction.NORTH);
        boolean south = canSupply(world, pos, Direction.SOUTH);
        boolean west = canSupply(world, pos, Direction.WEST);
        boolean east = canSupply(world, pos, Direction.EAST);
        boolean up = canSupply(world, pos, Direction.UP);
        boolean down = canSupply(world, pos, Direction.DOWN);
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
    }

    public boolean canSupply(Level level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos).is(ModTags.Blocks.HEART_NETWORK_BLOCKS);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
        super.createBlockStateDefinition(builder);
    }
}