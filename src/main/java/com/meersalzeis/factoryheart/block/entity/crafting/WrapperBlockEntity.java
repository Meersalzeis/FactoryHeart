package com.meersalzeis.factoryheart.block.entity.crafting;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.SingleSlotFilteredHandler;
import com.meersalzeis.factoryheart.block.custom.WrapperBlock;
import com.meersalzeis.factoryheart.block.entity.ModBlockEntities;
import com.meersalzeis.factoryheart.block.entity.energy.ModEnergyStorage;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartFeeding;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.recipe.WrapperRecipe;
import com.meersalzeis.factoryheart.recipe.WrapperRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.WrapperMenu;

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

public class WrapperBlockEntity extends BlockEntity implements MenuProvider {

    private static final int CENTERPIECE_SLOT = 0;
    private static final int WRAPPINGS_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

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

    private static List<ItemStack> viableWrappings = null;
    private static List<ItemStack> viableCenterpieces = null;

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private final int DEFAULT_MAX_PROGRESS = 100;


    public WrapperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WRAPPER_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> WrapperBlockEntity.this.progress;
                    case 1 -> WrapperBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: WrapperBlockEntity.this.progress = pValue;
                    case 1: WrapperBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    // Hopper handling region

    @Override
    public void onLoad() {
        InitViableMaterials();
    }

    private void InitViableMaterials() {
        if (viableCenterpieces != null) return;

        RecipeManager recipeManager = getLevel().getRecipeManager();

        List<RecipeHolder<WrapperRecipe>> recipes = recipeManager.getAllRecipesFor(ModRecipes.WRAPPER_TYPE.get());

        viableCenterpieces = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(0).getItems()[0])
            .toList();
        
        viableWrappings = recipes.stream()
            .map(holder -> holder.value().getIngredients().get(1).getItems()[0])
            .toList();
    }

    public static boolean isViableCenterpiece(ItemStack stack) {
        return viableCenterpieces.stream().anyMatch(x -> ItemStack.isSameItem(x, stack));
    }

    public static boolean isViableWrapping(ItemStack stack) {
        return viableWrappings.stream().anyMatch(x -> ItemStack.isSameItem(x, stack));
    }

    // End hopper handling

    public IItemHandler getItemHandler(Direction direction) {
        return this.itemHandler;
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

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("wrapper.progress", progress);
        pTag.putInt("wrapper.max_progress", maxProgress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("wrapper.progress");
        maxProgress = pTag.getInt("wrapper.max_progress");
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
            level.setBlockAndUpdate(pPos, pState.setValue(WrapperBlock.LIT, true));
            setChanged(level, pPos, pState);

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
            }

        } else {
            resetProgress();
            level.setBlockAndUpdate(pPos, pState.setValue(WrapperBlock.LIT, false));
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
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
        Optional<RecipeHolder<WrapperRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }
        ItemStack output = recipe.get().value().getResultItem(null);
        return 
            canInsertAmountIntoOutputSlot(output.getCount()) 
            && canInsertItemIntoOutputSlot(output) 
            && isOutputSlotEmptyOrReceivable()
            && hasSufficientMaterialForRecipe(recipe);
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
