
package com.meersalzeis.factoryheart.gui.menus;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.blockentity.crafting.TesterBlockEntity;
import com.meersalzeis.factoryheart.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class TesterMenu extends FHCraftingMenu<TesterBlockEntity> {

    private final ContainerData data;

    public TesterMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public TesterMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.TESTER_MENU.get(), pContainerId, ModBlocks.TESTER.get());
        blockEntity = ((TesterBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 0, 54, 34));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 1, 104, 20));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler, 2, 104, 49));

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        int res = maxProgress != 0 && progress != 0 ? progress * arrowPixelSize / maxProgress : 0;
        FHModClient.debugMessageToAll(""+res, false);
        return res;
    }
}
