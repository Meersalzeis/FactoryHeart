package com.meersalzeis.factoryheart.block.entity.custom;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.custom.CondenserBlock;
import com.meersalzeis.factoryheart.block.entity.ModBlockEntities;
import com.meersalzeis.factoryheart.hearts.HeartBeating;
import com.meersalzeis.factoryheart.hearts.HeartFeeding;
import com.meersalzeis.factoryheart.item.ModItems;
import com.meersalzeis.factoryheart.recipe.CondenserRecipe;
import com.meersalzeis.factoryheart.recipe.CondenserRecipeInput;
import com.meersalzeis.factoryheart.recipe.ModRecipes;
import com.meersalzeis.factoryheart.gui.menus.BlazerMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredBlock;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class CondenserBlockEntity extends BlockEntity {

    private static final int OUTPUT_SLOT = 0;

    private final ContainerData data;
    private int progress = 0;
    private int maxProgress = 100;
    private final int DEFAULT_MAX_PROGRESS = 100;

    private DyeColor color;

    public CondenserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CONDENSER_BE.get(), pos, blockState);

        color = blockState.getValue(CondenserBlock.COLOR);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> CondenserBlockEntity.this.progress;
                    case 1 -> CondenserBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: CondenserBlockEntity.this.progress = pValue;
                    case 1: CondenserBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("condenser.progress", progress);
        pTag.putInt("condenser.max_progress", maxProgress);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        progress = pTag.getInt("condenser.progress");
        maxProgress = pTag.getInt("condenser.max_progress");

        // ENERGY_STORAGE.setEnergy(pTag.getInt("condenser.energy"));
        // FLUID_TANK.readFromNBT(pRegistries, pTag);
    }


    public void tick(Level level, BlockPos pos, BlockState pState) {
        if(canCraft(level, pos)) {
            FHModClient.debugMessageToAll("Condenser can craft", false);
            increaseCraftingProgress();
            level.setBlockAndUpdate(pos, pState.setValue(CondenserBlock.LIT, true));
            setChanged(level, pos, pState);

            if (hasCraftingFinished()) {
                FHModClient.debugMessageToAll("Condenser crafting finished", false);
                craftItem(level, pos);
                resetProgress();
            }

        } else {
            resetProgress();
            level.setBlockAndUpdate(pos, pState.setValue(CondenserBlock.LIT, false));
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }

    private void craftItem(Level level, BlockPos pos) {
        FHModClient.debugMessageToAll("Condenser Spawning Item", false);
        Optional<RecipeHolder<CondenserRecipe>> recipe = getRecipeForThis();
        ItemStack output = recipe.get().value().output();

        ItemStack itemStackToDrop = new ItemStack(output.getItem(), output.getCount());
        Direction direction = level.getBlockState(pos).getValue(CondenserBlock.FACING);
        Vec3 ejectStartPoint = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.7));
        
        DefaultDispenseItemBehavior.spawnItem(level, itemStackToDrop, 10, direction, ejectStartPoint);
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean canCraft(Level level, BlockPos pos) {
        Optional<RecipeHolder<CondenserRecipe>> recipe = getRecipeForThis();
        if(recipe.isEmpty()) {
            return false;
        }
        return hasSufficientTierToCraft(level, pos, recipe);
    }

    private boolean hasSufficientTierToCraft(Level level, BlockPos pos, Optional<RecipeHolder<CondenserRecipe>> recipe) {
        return recipe.get().value().requiredTier() <= 4;//HeartBeating.GetTier(level, pos);
    }

    private Optional<RecipeHolder<CondenserRecipe>> getRecipeForThis() {
        FHModClient.debugMessageToAll("CurColor: " + this.color.toString(), false);
        return this.level.getRecipeManager().getRecipeFor(
            ModRecipes.CONDENSER_TYPE.get(),
            new CondenserRecipeInput(this.color),
            level
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
}
