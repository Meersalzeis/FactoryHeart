package com.meersalzeis.factoryheart.hearts;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.blockentity.ModBlockEntities;

import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FHCraftingStation<T extends BlockEntity> extends BlockEntity {

    protected final ContainerData data;
    protected int progress = 0;
    protected int maxProgress = 100;
    protected int currentTier;
    protected final int DEFAULT_MAX_PROGRESS = 100;

    public FHCraftingStation(BlockEntityType<T> bEntityType, BlockPos pPos, BlockState pBlockState) {
        super(bEntityType, pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FHCraftingStation.this.progress;
                    case 1 -> FHCraftingStation.this.maxProgress;
                    case 2 -> FHCraftingStation.this.currentTier;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: FHCraftingStation.this.progress = pValue;
                    case 1: FHCraftingStation.this.maxProgress = pValue;
                    case 2: FHCraftingStation.this.currentTier = pValue;

                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }
    
    public int getTier() {
        return currentTier;
    }

    // =============== Handle Inventory ==============

    protected abstract ItemStackHandler getInventory();

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", getInventory().serializeNBT(pRegistries));
        pTag.putInt("blazer.progress", progress);
        pTag.putInt("blazer.max_progress", maxProgress);

        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        getInventory().deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("blazer.progress");
        maxProgress = pTag.getInt("blazer.max_progress");
    }

    public void drops() {
        var inventory = getInventory();
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    // =============== Handle FH Tier Sync ==============

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Tier", currentTier);
        return tag;
    }   

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        currentTier = tag.getInt("Tier");
    }

    public void initiateSync() {
        level.sendBlockUpdated(
            worldPosition,
            getBlockState(),
            getBlockState(),
            Block.UPDATE_CLIENTS
        );
    }
}
