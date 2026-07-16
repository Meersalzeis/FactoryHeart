package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.crafting.BlazerBlock;
import com.meersalzeis.factoryheart.blockentity.FHCraftingStation;
import com.meersalzeis.factoryheart.blockentity.FactoryHeartBlockEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.blockentity.SingleSlotFilteredHandler;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartNetwork;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.recipe.BlazerRecipe;
import com.meersalzeis.factoryheart.recipe.BlazerRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.BlazerMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

public class BlazerBlockEntity extends FHCraftingStation<BlazerBlockEntity> implements MenuProvider {

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

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    public final IItemHandler restHandler = new SingleSlotFilteredHandler(itemHandler, INPUT_SLOT, x -> isViableInput(x), false);
    public final IItemHandler bottomHandler = new RangedWrapper(itemHandler, OUTPUT_SLOT, OUTPUT_SLOT + 1);


    public BlazerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BLAZER_BE.get(), pPos, pBlockState);
    }

    private static List<ItemStack> viableInputs = null;
    @Override
    public void onLoad() {
        InitViableMaterials();
    }

    private void InitViableMaterials() {
        if (viableInputs != null) return;

        RecipeManager recipeManager = getLevel().getRecipeManager();

        List<RecipeHolder<BlazerRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipes.BLAZER_TYPE.get());

        viableInputs = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(0).getItems()[0])
            .toList();
    }

    public static boolean isViableInput(ItemStack stack) {
        return viableInputs.stream().anyMatch(x -> ItemStack.isSameItem(x, stack));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockentity.factoryheart.y_blazer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BlazerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected ItemStackHandler getInventory() {
        return itemHandler;
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        if(canCraft(level, pPos)) {
            increaseCraftingProgress();
            // useEnergyForCrafting();
            level.setBlockAndUpdate(pPos, pState.setValue(BlazerBlock.LIT, true));
            setChanged(level, pPos, pState);

            if (hasCraftingFinished()) {
                craftItem();
                // extractFluidForCrafting();
                resetProgress();
            }

        } else {
            resetProgress();
            level.setBlockAndUpdate(pPos, pState.setValue(BlazerBlock.LIT, false));
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<BlazerRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().output();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(
            OUTPUT_SLOT,
            new ItemStack(
                output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount())
        );
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean canCraft(Level level, BlockPos pos) {
        Optional<RecipeHolder<BlazerRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().getResultItem(null);
        return 
            canInsertAmountIntoOutputSlot(output.getCount()) 
            && canInsertItemIntoOutputSlot(output) 
            && hasSufficientTierToCraft(level, pos, recipe)
            && isOutputSlotEmptyOrReceivable();
    }

    private boolean hasSufficientTierToCraft(Level level, BlockPos pos, Optional<RecipeHolder<BlazerRecipe>> recipe) {
        return recipe.get().value().requiredTier() <= currentTier;
    }

    private Optional<RecipeHolder<BlazerRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.BLAZER_TYPE.get(), new BlazerRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
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
