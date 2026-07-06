package com.meersalzeis.factoryheart.block.entity.crafting;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.custom.TesterBlock;
import com.meersalzeis.factoryheart.block.entity.ModBlockEntities;
import com.meersalzeis.factoryheart.recipe.TesterRecipe;
import com.meersalzeis.factoryheart.recipe.TesterRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.TesterMenu;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TesterBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int SUCCESS_SLOT = 1;
    private static final int FAILED_SLOT = 2;

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private final int DEFAULT_MAX_PROGRESS = 100;


    public TesterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TESTER_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> TesterBlockEntity.this.progress;
                    case 1 -> TesterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: TesterBlockEntity.this.progress = pValue;
                    case 1: TesterBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public IItemHandler getItemHandler(Direction direction) {
        return this.itemHandler;
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

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("tester.progress", progress);
        pTag.putInt("tester.max_progress", maxProgress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("tester.progress");
        maxProgress = pTag.getInt("tester.max_progress");
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
            level.setBlockAndUpdate(pPos, pState.setValue(TesterBlock.LIT, true));

            if (hasCraftingFinished()) {
                setChanged(level, pPos, pState);
                craftItem();
                resetProgress();
            }

        } else {
            if (pState.getValue(TesterBlock.LIT)) {
                setChanged(level, pPos, pState);
                resetProgress();
                level.setBlockAndUpdate(pPos, pState.setValue(TesterBlock.LIT, false));
            }
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem() {
        TesterRecipe recipe = getCurrentRecipe().get().value();
        float successChance = recipe.getSuccessChance();

        boolean didAssemblySucceed = FHModMain.rnd.nextFloat(0f, 1f) < successChance;
        ItemStack output;
        if (didAssemblySucceed) {
            output = recipe.assembleSuccess();
        } else {
            output = recipe.assembleFailed();
        }

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(
            SUCCESS_SLOT,
            new ItemStack(
                output.getItem(),
                itemHandler.getStackInSlot(SUCCESS_SLOT).getCount() + output.getCount())
        );
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
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
        return recipe.get().value().requiredTier() <= 4;
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
