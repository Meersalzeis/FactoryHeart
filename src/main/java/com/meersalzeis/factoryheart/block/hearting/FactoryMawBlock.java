package com.meersalzeis.factoryheart.block.hearting;

import com.mojang.serialization.MapCodec;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.hearts.HeartFeeding;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.Entity;

public class FactoryMawBlock extends DirectionalBlock {
    
    public static final MapCodec<FactoryMawBlock> CODEC = simpleCodec(FactoryMawBlock::new);
    public static final DirectionProperty FACING =  BlockStateProperties.FACING;

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
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        // THIS METHOD IS !CLIENT ONLY!
        double xPos = pPos.getX() + 0.5f;
        double yPos = pPos.getY() + 1.25f;
        double zPos = pPos.getZ() + 0.5f;
        double offset = pRandom.nextDouble() * 0.6 - 0.3;

        pLevel.addParticle(ParticleTypes.SMOKE, xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
        pLevel.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.FACTORY_MAW.get().defaultBlockState()),
                xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
    }

    // @Override
    // protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    //     FHModClient.debugMessageToAll("Registered Entity inside!");

    //     if(entity instanceof ItemEntity itemEntity) {
    //         FHModClient.debugMessageToAll("Maw found ItemEntity of " + itemEntity.getItem().getItem());
    //         HeartNetwork.MawGetsItemFed(level, pos, itemEntity);
    //     }

    //     super.entityInside(state, level, pos, entity);
    // }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        super.stepOn(pLevel, pPos, pState, pEntity);

        if(pEntity instanceof ItemEntity itemEntity) {
            HeartFeeding.MawGetsItemFed(pLevel, pPos, itemEntity);
        }
    }

    // @Override
    // public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
    //     if (level.isClientSide()) return;
    //     HeartBeating.AddBlock(level, pos, false);
    // }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide()) return;

        if (state.is(newState.getBlock())) {
            // Only state change, no "actual" removal
            return;
        }

        HeartBeating.DeregisterBlock(level, pos, false);
    }
}
