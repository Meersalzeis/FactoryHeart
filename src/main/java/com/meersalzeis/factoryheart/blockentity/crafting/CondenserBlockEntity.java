package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.crafting.CondenserBlock;
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.recipe.CondenserRecipe;
import com.meersalzeis.factoryheart.recipe.CondenserRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Optional;

public class CondenserBlockEntity extends FHCraftStationEntity<CondenserBlockEntity> {

    private DyeColor color;

    public CondenserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CONDENSER_BE.get(), pos, blockState);
        color = blockState.getValue(CondenserBlock.COLOR);
    }

    public void setColor(DyeColor newColor) {
        this.color = newColor;
    }

    // Not needed here
    protected void InitViableInputs() {}

    public void tick(Level level, BlockPos pos, BlockState bState) {
        if(canCraft(level, pos)) {
            increaseCraftingProgress();
            level.setBlockAndUpdate(pos, bState.setValue(CondenserBlock.LIT, true));
            setChanged(level, pos, bState);

            if (hasCraftingFinished()) {
                craftItem(level, pos);
                resetProgress();
            }

        } else {
            if (bState.getValue(CondenserBlock.LIT)) {
                resetProgress();
                level.setBlockAndUpdate(pos, bState.setValue(CondenserBlock.LIT, false));
            }
        }
    }

    private void craftItem(Level level, BlockPos pos) {
        Optional<RecipeHolder<CondenserRecipe>> recipe = getRecipeForThis();
        ItemStack output = recipe.get().value().output();

        ItemStack itemStackToDrop = new ItemStack(output.getItem(), output.getCount());
        Direction direction = level.getBlockState(pos).getValue(CondenserBlock.FACING);
        Vec3 ejectStartPoint = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.7));
        
        DefaultDispenseItemBehavior.spawnItem(level, itemStackToDrop, 10, direction, ejectStartPoint);
    }

    private boolean canCraft(Level level, BlockPos pos) {
        Optional<RecipeHolder<CondenserRecipe>> recipe = getRecipeForThis();
        if(recipe.isEmpty()) {
            return false;
        }
        return hasSufficientTierToCraft(level, pos, recipe);
    }

    private boolean hasSufficientTierToCraft(Level level, BlockPos pos, Optional<RecipeHolder<CondenserRecipe>> recipe) {
        return recipe.get().value().requiredTier() <= getTier();
    }

    private Optional<RecipeHolder<CondenserRecipe>> getRecipeForThis() {
        var res =  this.level.getRecipeManager().getRecipeFor(
            ModRecipes.CONDENSER_TYPE.get(),
            new CondenserRecipeInput(this.color),
            level
        );
        return res;
    }

    @Override
    protected ItemStackHandler getInventory() {
        return new ItemStackHandler();
    }
}
