package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.block.crafting.CondenserBlock;
import com.meersalzeis.factoryheart.blockentity.FactoryHeartBlockEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartNetwork;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.recipe.BlazerRecipe;
import com.meersalzeis.factoryheart.recipe.CondenserRecipe;
import com.meersalzeis.factoryheart.recipe.CondenserRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CondenserBlockEntity extends BlockEntity {

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private final int DEFAULT_MAX_PROGRESS = 100;

    private DyeColor color;

    private FactoryHeartBlockEntity heartEntity = null;

    public CondenserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CONDENSER_BE.get(), pos, blockState);

        color = blockState.getValue(CondenserBlock.COLOR);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CondenserBlockEntity.this.progress;
                    case 1 -> CondenserBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: CondenserBlockEntity.this.progress = pValue;
                    case 1: CondenserBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void setColor(DyeColor newColor) {
        this.color = newColor;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("condenser.progress", progress);
        pTag.putInt("condenser.max_progress", maxProgress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        progress = pTag.getInt("condenser.progress");
        maxProgress = pTag.getInt("condenser.max_progress");
    }


    public void tick(Level level, BlockPos pos, BlockState pState) {
        if(canCraft(level, pos)) {
            increaseCraftingProgress();
            level.setBlockAndUpdate(pos, pState.setValue(CondenserBlock.LIT, true));
            setChanged(level, pos, pState);

            if (hasCraftingFinished()) {
                craftItem(level, pos);
                resetProgress();
            }

        } else {
            resetProgress();
            level.setBlockAndUpdate(pos, pState.setValue(CondenserBlock.LIT, false));
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem(Level level, BlockPos pos) {
        Optional<RecipeHolder<CondenserRecipe>> recipe = getRecipeForThis();
        ItemStack output = recipe.get().value().output();

        ItemStack itemStackToDrop = new ItemStack(output.getItem(), output.getCount());
        Direction direction = level.getBlockState(pos).getValue(CondenserBlock.FACING);
        Vec3 ejectStartPoint = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.7));
        
        DefaultDispenseItemBehavior.spawnItem(level, itemStackToDrop, 10, direction, ejectStartPoint);
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean canCraft(Level level, BlockPos pos) {
        Optional<RecipeHolder<CondenserRecipe>> recipe = getRecipeForThis();
        if(recipe.isEmpty()) {
            return false;
        }
        return hasSufficientTierToCraft(level, pos, recipe);
    }

    private boolean hasSufficientTierToCraft(Level level, BlockPos pos, Optional<RecipeHolder<CondenserRecipe>> recipe) {
        if (heartEntity == null) heartEntity = HeartNetwork.getHeartEntity(level, pos);
        if (heartEntity == null) return false;
        return recipe.get().value().requiredTier() <= heartEntity.calculateCurrentTier();
    }

    private Optional<RecipeHolder<CondenserRecipe>> getRecipeForThis() {
        var res =  this.level.getRecipeManager().getRecipeFor(
            ModRecipes.CONDENSER_TYPE.get(),
            new CondenserRecipeInput(this.color),
            level
        );
        return res;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
}
