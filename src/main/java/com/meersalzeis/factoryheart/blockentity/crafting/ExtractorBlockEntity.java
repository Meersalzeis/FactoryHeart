package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.block.crafting.CondenserBlock;
import com.meersalzeis.factoryheart.block.crafting.ExtractorBlock;
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.blockentity.SingleSlotFilteredHandler;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipe;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.ExtractorMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ExtractorBlockEntity extends FHCraftStationEntity<ExtractorBlockEntity> implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == INPUT_SLOT) return isViableInput(stack);
            else return false;
        }
    };

    private static List<ItemStack> viableInputs = null;

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    public final IItemHandler restHandler = new SingleSlotFilteredHandler(itemHandler, INPUT_SLOT, x -> isViableInput(x), false);
    public final IItemHandler bottomHandler = new RangedWrapper(itemHandler, OUTPUT_SLOT, OUTPUT_SLOT + 1);

    public ExtractorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pPos, pBlockState);
    }

    @Override
    protected ItemStackHandler getInventory() {
        return itemHandler;
    }

    protected void InitViableInputs() {
        if (viableInputs != null) return;

        RecipeManager recipeManager = getLevel().getRecipeManager();

        List<RecipeHolder<ExtractorRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipes.EXTRACTOR_TYPE.get());

        viableInputs = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(0).getItems()[0])
            .toList();
    }

    public static boolean isViableInput(ItemStack stack) {
        return viableInputs.stream().anyMatch(x -> ItemStack.isSameItem(x, stack));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockentity.factoryheart.y_Extractor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ExtractorMenu(pContainerId, pPlayerInventory, this, this.data);
    }


    public void tick(Level level, BlockPos pos, BlockState bState) {
        if(canCraft(level, pos)) {
            increaseCraftingProgress();
            level.setBlockAndUpdate(pos, bState.setValue(ExtractorBlock.LIT, true));
            setChanged(level, pos, bState);

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }

        } else {
            if (bState.getValue(CondenserBlock.LIT)) {
                resetProgress();
                level.setBlockAndUpdate(pos, bState.setValue(CondenserBlock.LIT, false));
            }
        }
    }

    private void craftItem() {
        ExtractorRecipe recipe = getCurrentRecipe().get().value();
        int inputConsumption = recipe.getIngredientCount();
        ItemStack output = recipe.output();

        itemHandler.setStackInSlot(
            OUTPUT_SLOT,
            new ItemStack(
                output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount())
        );

        if (!recipe.doesConsumeInput()) return;

        itemHandler.extractItem(INPUT_SLOT, inputConsumption, false);
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean canCraft(Level level, BlockPos pos) {
        Optional<RecipeHolder<ExtractorRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }
        ItemStack output = recipe.get().value().getResultItem(null);
        return 
            canInsertAmountIntoOutputSlot(output.getCount()) 
            && canInsertItemIntoOutputSlot(output) 
            && hasSufficientTierToCraft(level, pos, recipe)
            && isOutputSlotEmptyOrReceivable()
            && hasSufficientMaterialForRecipe(recipe);
    }

    private boolean hasSufficientMaterialForRecipe(Optional<RecipeHolder<ExtractorRecipe>> recipe) {
        return recipe.get().value().getIngredientCount() <= itemHandler.getStackInSlot(INPUT_SLOT).getCount();
    }

    private boolean hasSufficientTierToCraft(Level level, BlockPos pos, Optional<RecipeHolder<ExtractorRecipe>> recipe) {
        return recipe.get().value().requiredTier() <= getTier();
    }

    public Optional<RecipeHolder<ExtractorRecipe>> getCurrentRecipe() {
        var inputSlot = itemHandler.getStackInSlot(INPUT_SLOT);
        return this.level.getRecipeManager()
            .getRecipeFor(
                ModRecipes.EXTRACTOR_TYPE.get(),
                new ExtractorRecipeInput(inputSlot, inputSlot.getCount(), 4),
                level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }
}
