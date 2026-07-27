package com.meersalzeis.factoryheart.blockentity;

import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public abstract class FHCraftStationEntity<T extends BlockEntity> extends BlockEntity {

    public final ContainerData data;
    protected int progress = 0;
    protected int maxProgress = 100;
    protected int currentTier;

    public FHCraftStationEntity(BlockEntityType<T> bEntityType, BlockPos pPos, BlockState pBlockState) {
        super(bEntityType, pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FHCraftStationEntity.this.progress;
                    case 1 -> FHCraftStationEntity.this.maxProgress;
                    case 2 -> FHCraftStationEntity.this.currentTier;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: FHCraftStationEntity.this.progress = pValue;
                    case 1: FHCraftStationEntity.this.maxProgress = pValue;
                    case 2: FHCraftStationEntity.this.currentTier = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }
    
    @Override
    public void onLoad() {
        InitViableInputs();
    }

    protected abstract void InitViableInputs();

    public int getTier() {
        return currentTier;
    }

    protected boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    protected void increaseCraftingProgress() {
        progress++;
    }

    protected void resetProgress() {
        this.progress = 0;
    }

    // =============== Handle Inventory ==============

    protected abstract ItemStackHandler getInventory();

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", getInventory().serializeNBT(pRegistries));
        pTag.putInt("fhcraftingstation.progress", progress);
        pTag.putInt("fhcraftingstation.max_progress", maxProgress);
        pTag.putInt("fhcraftingstation.currentTier", currentTier);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        getInventory().deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("fhcraftingstation.progress");
        maxProgress = pTag.getInt("fhcraftingstation.max_progress");
        currentTier = pTag.getInt("fhcraftingstation.currentTier");
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    // =============== Jade access ==============

    public int getProgress() { return progress; }

    public int getMaxProgress() { return maxProgress; }
}
