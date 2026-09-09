package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.crafting.TesterBlock;
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.blockentity.SingleSlotFilteredHandler;
import com.meersalzeis.factoryheart.recipe.TesterRecipe;
import com.meersalzeis.factoryheart.recipe.TesterRecipeInput;
import com.meersalzeis.factoryheart.sound.ModSounds;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.TesterMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
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

public class TesterBlockEntity extends FHCraftStationEntity implements MenuProvider {

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
            if (slot == INPUT_SLOT) return isViableInput(stack);
            else return false;
        }
    };

    private static List<ItemStack> viableInputs = null;

    public static final int INPUT_SLOT = 0;
    public static final int SUCCESS_SLOT = 1;
    public static final int FAILED_SLOT = 2;

    public final IItemHandler restHandler = new SingleSlotFilteredHandler(itemHandler, INPUT_SLOT, x -> isViableInput(x), false);
    public final IItemHandler bottomHandler = new RangedWrapper(itemHandler, SUCCESS_SLOT, FAILED_SLOT + 1);

    public TesterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TESTER_BE.get(), pPos, pBlockState);
    }

    @Override
    protected ItemStackHandler getInventory() {
        return itemHandler;
    }

    protected void InitViableInputs() {
        if (viableInputs != null) return;

        RecipeManager recipeManager = getLevel().getRecipeManager();

        List<RecipeHolder<TesterRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipes.TESTER_TYPE.get());

        viableInputs = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(0).getItems()[0])
            .toList();
    }

    public static boolean isViableInput(ItemStack stack) {
        return viableInputs.stream().anyMatch(x -> ItemStack.isSameItem(x, stack));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockentity.factoryheart.y_tester");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new TesterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    public void tick(Level level, BlockPos pos, BlockState bState) {
        if(canCraft(level, pos)) {
            increaseCraftingProgress();
            level.setBlockAndUpdate(pos, bState.setValue(TesterBlock.LIT, true));

            if (hasCraftingFinished()) {
                //setChanged(level, pPos, pState);
                craftItem(pos);
                resetProgress();
            }

        } else {
            if (bState.getValue(TesterBlock.LIT)) {
                //setChanged(level, pPos, pState);
                resetProgress();
                level.setBlockAndUpdate(pos, bState.setValue(TesterBlock.LIT, false));
            }
        }
    }

    private void craftItem(BlockPos pos) {
        TesterRecipe recipe = getCurrentRecipe().get().value();
        float successChance = recipe.getSuccessChance();

        boolean didAssemblySucceed = FHModMain.rnd.nextFloat(0f, 1f) < successChance;

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        if (didAssemblySucceed) {
            insertIntoSlot(SUCCESS_SLOT, recipe.assembleSuccess());
            level.playSound(null, pos, ModSounds.TEST_SUCCESS.get(), SoundSource.BLOCKS, 0.333f, 1.0f);
        } else {
            insertIntoSlot(FAILED_SLOT, recipe.assembleFailed());
            level.playSound(null, pos, ModSounds.TEST_FAULTY.get(), SoundSource.BLOCKS, 0.333f, 1.0f);
        }
    }

    private void insertIntoSlot(int slotNr, ItemStack input) {
        itemHandler.setStackInSlot(
            slotNr,
            new ItemStack(
                input.getItem(),
                itemHandler.getStackInSlot(slotNr).getCount() + input.getCount())
        );
    }

    private boolean canCraft(Level level, BlockPos pos) {
        Optional<RecipeHolder<TesterRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        ItemStack success = recipe.get().value().assembleSuccess();
        ItemStack failure = recipe.get().value().assembleFailed();
        return 
            canInsertIntoSlot(SUCCESS_SLOT, success) 
            && canInsertIntoSlot(FAILED_SLOT, failure) 
            && hasSufficientTierToCraft(level, pos, recipe);
    }

    private boolean hasSufficientTierToCraft(Level level, BlockPos pos, Optional<RecipeHolder<TesterRecipe>> recipe) {
        return recipe.get().value().requiredTier() <= getTier();
    }

    private Optional<RecipeHolder<TesterRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.TESTER_TYPE.get(), new TesterRecipeInput(itemHandler.getStackInSlot(INPUT_SLOT)), level);
    }

    private boolean canInsertIntoSlot(int slotNr, ItemStack output) {
        var slot = itemHandler.getStackInSlot(slotNr);
        if (slot.isEmpty()) return true;
        if (slot.getItem() != output.getItem()) return false;
        return slot.getMaxStackSize() >= slot.getCount() + output.getCount();
    }
}
