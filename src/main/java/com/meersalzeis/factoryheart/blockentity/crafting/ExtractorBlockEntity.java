package com.meersalzeis.factoryheart.blockentity.crafting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.SingleSlotFilteredHandler;
import com.meersalzeis.factoryheart.block.crafting.ExtractorBlock;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;
import com.meersalzeis.factoryheart.blockentity.energy.ModEnergyStorage;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartFeeding;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.recipe.BlazerRecipe;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipe;
import com.meersalzeis.factoryheart.recipe.ExtractorRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.ExtractorMenu;

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
import net.minecraft.world.item.crafting.Ingredient;
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

public class ExtractorBlockEntity extends BlockEntity implements MenuProvider {

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

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private final int DEFAULT_MAX_PROGRESS = 100;

    public final IItemHandler restHandler = new SingleSlotFilteredHandler(itemHandler, INPUT_SLOT, x -> isViableInput(x), false);
    public final IItemHandler bottomHandler = new RangedWrapper(itemHandler, OUTPUT_SLOT, OUTPUT_SLOT + 1);

    public ExtractorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.EXTRACTOR_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ExtractorBlockEntity.this.progress;
                    case 1 -> ExtractorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: ExtractorBlockEntity.this.progress = pValue;
                    case 1: ExtractorBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private static List<ItemStack> viableInputs = null;
    @Override
    public void onLoad() {
        InitViableMaterials();
    }

    private void InitViableMaterials() {
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

    // public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
    //     return this.ENERGY_STORAGE;
    // }

    // public IFluidHandler getFluidTank(@Nullable Direction direction) {
    //     return this.FLUID_TANK;
    // }

    // public FluidStack getFluid() {
    //     return FLUID_TANK.getFluid();
    // }

    public IItemHandler getItemHandler(Direction direction) {
        return this.itemHandler;
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

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("Extractor.progress", progress);
        pTag.putInt("Extractor.max_progress", maxProgress);

        // pTag.putInt("factory_Extractor.energy", ENERGY_STORAGE.getEnergyStored());
        // pTag = FLUID_TANK.writeToNBT(pRegistries, pTag);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("Extractor.progress");
        maxProgress = pTag.getInt("Extractor.max_progress");

        // ENERGY_STORAGE.setEnergy(pTag.getInt("Extractor.energy"));
        // FLUID_TANK.readFromNBT(pRegistries, pTag);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        if(canCraft(level, pPos)) {
            increaseCraftingProgress();
            // useEnergyForCrafting();
            level.setBlockAndUpdate(pPos, pState.setValue(ExtractorBlock.LIT, true));
            setChanged(level, pPos, pState);

            if (hasCraftingFinished()) {
                craftItem();
                // extractFluidForCrafting();
                resetProgress();
            }

        } else {
            resetProgress();
            level.setBlockAndUpdate(pPos, pState.setValue(ExtractorBlock.LIT, false));
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem() {
        Optional<RecipeHolder<ExtractorRecipe>> recipe = getCurrentRecipe();
        int inputConsumption = recipe.get().value().getIngredientCount();
        ItemStack output = recipe.get().value().output();
        itemHandler.extractItem(INPUT_SLOT, inputConsumption, false);
        itemHandler.setStackInSlot(
            OUTPUT_SLOT,
            new ItemStack(
                output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount())
        );
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
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
        return recipe.get().value().requiredTier() <= 4;
    }

    private Optional<RecipeHolder<ExtractorRecipe>> getCurrentRecipe() {
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
