package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.block.crafting.CondenserBlock;
import com.meersalzeis.factoryheart.block.crafting.WrapperBlock;
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
import com.meersalzeis.factoryheart.blockentity.FactoryHeartBlockEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.blockentity.SingleSlotFilteredHandler;
import com.meersalzeis.factoryheart.recipe.WrapperRecipe;
import com.meersalzeis.factoryheart.recipe.WrapperRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.WrapperMenu;
import com.meersalzeis.factoryheart.hearts.HeartNetwork;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class WrapperBlockEntity extends FHCraftStationEntity<WrapperBlockEntity> implements MenuProvider {

    public static final int CENTERPIECE_SLOT = 0;
    public static final int WRAPPINGS_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == CENTERPIECE_SLOT) return isViableCenterpiece(stack);
            if (slot == WRAPPINGS_SLOT) return isViableWrapping(stack);
            else return false;
        }
    };

    // Hopper Handling
    public final IItemHandler topHandler = new SingleSlotFilteredHandler(itemHandler, CENTERPIECE_SLOT, x -> isViableCenterpiece(x), false);
    public final IItemHandler sideHandler = new SingleSlotFilteredHandler(itemHandler, WRAPPINGS_SLOT, x -> isViableWrapping(x), false);
    public final IItemHandler bottomHandler = new RangedWrapper(itemHandler, OUTPUT_SLOT, OUTPUT_SLOT + 1);

    private static List<Item> viableWrappings = null;
    private static List<Item> viableCenterpieces = null;

    public WrapperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WRAPPER_BE.get(), pPos, pBlockState);
    }

    @Override
    protected ItemStackHandler getInventory() {
        return itemHandler;
    }

    protected void InitViableInputs() {
        if (viableCenterpieces != null) return;

        RecipeManager recipeManager = getLevel().getRecipeManager();

        List<RecipeHolder<WrapperRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipes.WRAPPER_TYPE.get());

        viableCenterpieces = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(0).getItems()[0].getItem())
            .toList();
        
        viableWrappings = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(1).getItems()[0].getItem())
            .toList();
    }

    public static boolean isViableCenterpiece(ItemStack stack) {
        var item = stack.getItem();
        return viableCenterpieces.contains(item);
    }

    public static boolean isViableWrapping(ItemStack stack) {
        var item = stack.getItem();
        return viableWrappings.contains(item);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockentity.factoryheart.y_wrapper");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WrapperMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void tick(Level level, BlockPos pos, BlockState bState) {
        if(canCraft(level, pos)) {
            increaseCraftingProgress();
            level.setBlockAndUpdate(pos, bState.setValue(WrapperBlock.LIT, true));
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
        Optional<RecipeHolder<WrapperRecipe>> recipe = getCurrentRecipe();
        int centerpieceConsumption = recipe.get().value().getCenterpieceCount();
        int wrappingsConsumption = recipe.get().value().getWrappingsCount();
        ItemStack output = recipe.get().value().output();
        itemHandler.extractItem(CENTERPIECE_SLOT, centerpieceConsumption, false);
        itemHandler.extractItem(WRAPPINGS_SLOT, wrappingsConsumption, false);
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
        Optional<RecipeHolder<WrapperRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }
        ItemStack output = recipe.get().value().getResultItem(null);
        return 
            canInsertAmountIntoOutputSlot(output.getCount()) 
            && canInsertItemIntoOutputSlot(output) 
            && isOutputSlotEmptyOrReceivable()
            && hasSufficientTier(level, pos)
            && hasSufficientMaterialForRecipe(recipe);
    }

    private boolean hasSufficientTier(Level level, BlockPos pos) {
        return getTier() >= 1;
    }

    private boolean hasSufficientMaterialForRecipe(Optional<RecipeHolder<WrapperRecipe>> recipe) {
        var recipeObj = recipe.get().value();
        return recipeObj.getCenterpieceCount() <= itemHandler.getStackInSlot(CENTERPIECE_SLOT).getCount()
            && recipeObj.getWrappingsCount() <= itemHandler.getStackInSlot(WRAPPINGS_SLOT).getCount();
    }

    private Optional<RecipeHolder<WrapperRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
            .getRecipeFor(
                ModRecipes.WRAPPER_TYPE.get(),
                new WrapperRecipeInput(
                    itemHandler.getStackInSlot(CENTERPIECE_SLOT),
                    itemHandler.getStackInSlot(WRAPPINGS_SLOT),
                    itemHandler.getStackInSlot(CENTERPIECE_SLOT).getCount(),
                    itemHandler.getStackInSlot(WRAPPINGS_SLOT).getCount()
                ),
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
