
package com.meersalzeis.factoryheart.gui.menus;

import com.meersalzeis.factoryheart.block.ModBlocks;
import com.meersalzeis.factoryheart.blockentity.crafting.CondenserBlockEntity;
import com.meersalzeis.factoryheart.gui.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CondenserMenu  extends AbstractContainerMenu {
    public final CondenserBlockEntity blockEntity;
    // private final Level level;
    // private final ContainerData data;

    public CondenserMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public CondenserMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CONDENSER_MENU.get(), pContainerId);
        blockEntity = ((CondenserBlockEntity) entity);
        // this.level = inv.player.level();
        // this.data = data;

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return false;
    }

    public int getScaledArrowProgress() {
        return 0;
    }

    @Override
    public ItemStack quickMoveStack(Player arg0, int arg1) {
        // This Menu is only for JEI or similar, not for real interaction
        throw new UnsupportedOperationException("Unimplemented method 'quickMoveStack'");
    }

    @Override
    public boolean stillValid(Player arg0) {
        // This Menu is only for JEI or similar, not for real interaction
        throw new UnsupportedOperationException("Unimplemented method 'stillValid'");
    }
}
