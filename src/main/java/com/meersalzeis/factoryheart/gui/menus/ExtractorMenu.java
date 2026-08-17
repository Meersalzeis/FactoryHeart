
package com.meersalzeis.factoryheart.gui.menus;

import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.blockentity.crafting.ExtractorBlockEntity;
import com.meersalzeis.factoryheart.gui.ModMenuTypes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ExtractorMenu extends FHCraftingMenu<ExtractorBlockEntity> {

    private final ContainerData data;

    public ExtractorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public ExtractorMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.EXTRACTOR_MENU.get(), pContainerId, ModBlocks.EXTRACTOR.get());
        blockEntity = ((ExtractorBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        //this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 8, 62));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 54, 34));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 104, 34));
        //this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 3, 152, 62));

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public boolean doesConsumeInput() {
        var recipeOptional = blockEntity.getCurrentRecipe();
        return (recipeOptional.isEmpty()) ? false : recipeOptional.get().value().doesConsumeInput();
    }

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
    }
}
