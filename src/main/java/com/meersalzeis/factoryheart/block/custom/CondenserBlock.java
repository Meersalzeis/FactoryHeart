package com.meersalzeis.factoryheart.block.custom;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.entity.ModBlockEntities;
import com.meersalzeis.factoryheart.block.entity.custom.CondenserBlockEntity;
//import com.meersalzeis.factoryheart.block.entity.custom.CondenserBlockEntity;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class CondenserBlock extends BaseEntityBlock {
    
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final MapCodec<CondenserBlock> CODEC = simpleCodec(CondenserBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public static final VoxelShape SHAPE_DOWN = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_UP = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
    public static final VoxelShape SHAPE_NORTH = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_WEST = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final VoxelShape SHAPE_SOUTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
    public static final VoxelShape SHAPE_EAST = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);

    public CondenserBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false)
            .setValue(COLOR, DyeColor.RED)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState)this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
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
    protected BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT, COLOR);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.CONDENSER_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CondenserBlockEntity(pPos, pState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    // @Override
    // public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    //     if (!state.getValue(LIT)) {
    //         return;
    //     }

    //     double xPos = (double)pos.getX() + 0.5;
    //     double yPos = pos.getY();
    //     double zPos = (double)pos.getZ() + 0.5;
    //     if (random.nextDouble() < 0.15) {
    //         level.playLocalSound(xPos, yPos, zPos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.0f, false);
    //     }

    //     Direction direction = Direction.UP; //state.getValue(FACING);
    //     Direction.Axis axis = direction.getAxis();

    //     double defaultOffset = random.nextDouble() * 0.6 - 0.3;
    //     double xOffsets = axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : defaultOffset;
    //     double yOffset = random.nextDouble() * 6.0 / 8.0;
    //     double zOffset = axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : defaultOffset;

    //     level.addParticle(ParticleTypes.SMOKE, xPos + xOffsets, yPos + yOffset, zPos + zOffset, 0.0, 0.0, 0.0);

    //     if(level.getBlockEntity(pos) instanceof CondenserBlockEntity factoryBalzerBlockEntity && !factoryBalzerBlockEntity.itemHandler.getStackInSlot(1).isEmpty()) {
    //         level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, factoryBalzerBlockEntity.itemHandler.getStackInSlot(1)),
    //                 xPos + xOffsets, yPos + yOffset, zPos + zOffset, 0.0, 0.0, 0.0);
    //     }
    // }

    @Override
    protected ItemInteractionResult useItemOn(
                ItemStack pStack,
                BlockState pState,
                Level pLevel,
                BlockPos pPos,
                Player pPlayer,
                InteractionHand pHand,
                BlockHitResult pHitResult) {

        if (!(pStack.getItem() instanceof DyeItem dyeItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        DyeColor newColor = dyeItem.getDyeColor();

        if (pState.getValue(COLOR) != newColor && !pLevel.isClientSide) {
            pLevel.setBlock(pPos, pState.setValue(COLOR, newColor), Block.UPDATE_ALL);

            if (!pPlayer.getAbilities().instabuild) pStack.shrink(1);
            FHModClient.debugMessageToAll("Dyed Condenser to " + newColor.toString(), false);
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
    }

}
