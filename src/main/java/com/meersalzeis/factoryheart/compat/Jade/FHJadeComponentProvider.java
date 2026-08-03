package com.meersalzeis.factoryheart.compat.Jade;

import java.util.ArrayList;

import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.blockentity.FHCraftStationEntity;
import com.meersalzeis.factoryheart.blockentity.FactoryHeartBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.BlazerBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.CondenserBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.ExtractorBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.TesterBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.WrapperBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;


public enum FHJadeComponentProvider implements IBlockComponentProvider {
  INSTANCE;

  IElementHelper helper = IElementHelper.get();

  @Override
  public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
    Block block = accessor.getBlock();

    if (block == ModBlocks.FACTORY_HEART.get()) {
        appendHeartTooltips(tooltip, accessor);
    } else if (block == ModBlocks.BLAZER.get()) {
        appendBlazerTooltips(tooltip, accessor);
    } else if (block == ModBlocks.WRAPPER.get()) {
        appendWrapperTooltips(tooltip, accessor);
    } else if (block == ModBlocks.EXTRACTOR.get()) {
        appendExtractorTooltips(tooltip, accessor);
    } else if (block == ModBlocks.TESTER.get()) {
        appendTesterTooltips(tooltip, accessor);
    } else if (block == ModBlocks.CONDENSER.get()) {
        appendCondenserTooltips(tooltip, accessor);
    }
  }

  @Override
  public ResourceLocation getUid() {
    return JadePluginRegistering.getUid();
  }


  private void appendHeartTooltips(ITooltip tooltip, BlockAccessor accessor) {
    BlockEntity blockEntity = accessor.getBlockEntity();
    if (!(blockEntity instanceof FactoryHeartBlockEntity heartEntity)) {
        return;
    }

    int heartTier = heartEntity.calculateCurrentTier();
    tooltip.add(Component.literal("Tier " + heartTier));

    tooltip.add(Component.literal("Fuel left " + heartEntity.getFuelLeft()));
    if (heartEntity.usesCoolant(heartTier)) {
      tooltip.add(Component.literal("Coolant left " + heartEntity.getCoolantLeft()));
    }
  }

  private void appendTierTooltip(ITooltip tooltip, @SuppressWarnings("rawtypes") FHCraftStationEntity bEntity) {
    tooltip.add(Component.literal("Tier "+ bEntity.getTier()));
  }

  ArrayList<IElement> createCraftingStationTooltips(@SuppressWarnings("rawtypes") FHCraftStationEntity bEntity, ItemStack input, ItemStack output) {
    bEntity.syncProgress = true;
    int progress = bEntity.getProgress();
    int maxProgress = bEntity.getMaxProgress();

    ArrayList<IElement> craftProcess = new ArrayList<IElement>();
    craftProcess.add(helper.item(input));
    craftProcess.add(helper.progress(((float)progress+1) / maxProgress));
    craftProcess.add(helper.item(output));
    return craftProcess;
  }

  private void appendBlazerTooltips(ITooltip tooltip, BlockAccessor accessor) {
    BlockEntity blockEntity = accessor.getBlockEntity();
    if (!(blockEntity instanceof BlazerBlockEntity bEntity)) {
        return;
    }
    
    appendTierTooltip(tooltip, bEntity);

    ItemStack input = bEntity.itemHandler.getStackInSlot(BlazerBlockEntity.INPUT_SLOT);
    ItemStack output = bEntity.itemHandler.getStackInSlot(BlazerBlockEntity.OUTPUT_SLOT);
    tooltip.add(createCraftingStationTooltips(bEntity, input, output));
  }

  private void appendWrapperTooltips(ITooltip tooltip, BlockAccessor accessor) {
    BlockEntity blockEntity = accessor.getBlockEntity();
    if (!(blockEntity instanceof WrapperBlockEntity bEntity)) {
        return;
    }

    appendTierTooltip(tooltip, bEntity);

    ItemStack centerpieces = bEntity.itemHandler.getStackInSlot(WrapperBlockEntity.CENTERPIECE_SLOT);
    ItemStack wrappings = bEntity.itemHandler.getStackInSlot(WrapperBlockEntity.WRAPPINGS_SLOT);
    ItemStack output = bEntity.itemHandler.getStackInSlot(WrapperBlockEntity.OUTPUT_SLOT);

    var craftProcess = createCraftingStationTooltips(bEntity, wrappings, output);
    craftProcess.add(0, helper.item(centerpieces));
    tooltip.add(craftProcess);
  }

  private void appendExtractorTooltips(ITooltip tooltip, BlockAccessor accessor) {
    BlockEntity blockEntity = accessor.getBlockEntity();
    if (!(blockEntity instanceof ExtractorBlockEntity bEntity)) {
        return;
    }

    appendTierTooltip(tooltip, bEntity);

    ItemStack input = bEntity.itemHandler.getStackInSlot(ExtractorBlockEntity.INPUT_SLOT);
    ItemStack output = bEntity.itemHandler.getStackInSlot(ExtractorBlockEntity.OUTPUT_SLOT);
    tooltip.add(createCraftingStationTooltips(bEntity, input, output));
  }

  private void appendTesterTooltips(ITooltip tooltip, BlockAccessor accessor) {
    BlockEntity blockEntity = accessor.getBlockEntity();
    if (!(blockEntity instanceof TesterBlockEntity bEntity)) {
        return;
    }

    appendTierTooltip(tooltip, bEntity);

    ItemStack input = bEntity.itemHandler.getStackInSlot(TesterBlockEntity.INPUT_SLOT);
    ItemStack success = bEntity.itemHandler.getStackInSlot(TesterBlockEntity.SUCCESS_SLOT);
    ItemStack failed = bEntity.itemHandler.getStackInSlot(TesterBlockEntity.FAILED_SLOT);
    
    var craftProcess = createCraftingStationTooltips(bEntity, input, success);
    craftProcess.add(helper.item(failed));
    tooltip.add(craftProcess);
  }

  private void appendCondenserTooltips(ITooltip tooltip, BlockAccessor accessor) {
    BlockEntity blockEntity = accessor.getBlockEntity();
    if (!(blockEntity instanceof CondenserBlockEntity bEntity)) {
        return;
    }

    appendTierTooltip(tooltip, bEntity);

    bEntity.syncProgress = true;
    int progress = bEntity.getProgress();
    int maxProgress = bEntity.getMaxProgress();

    // Progress bar / arrow
    tooltip.add(helper.progress(((float)progress+1) / maxProgress));
  }
}