package com.meersalzeis.factoryheart.gui.screens;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.gui.menus.BlazerMenu;
import com.meersalzeis.factoryheart.gui.renderer.FHScreens;
import com.meersalzeis.factoryheart.gui.renderer.DisplayHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class BlazerScreen extends AbstractContainerScreen<BlazerMenu> {
    private static final ResourceLocation GUI_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/blazer/blazer_gui.png");

    public BlazerScreen(BlazerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelX = 70;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        FHScreens.setRenderSystem(GUI_TEXTURE);
        pGuiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(pGuiGraphics, x, y);

        int tier = menu.getTier();
        DisplayHelper.renderTierDisplay(pGuiGraphics, tier, x, y);
        DisplayHelper.renderTierTooltip(pGuiGraphics, pMouseX, pMouseY, x, y, tier, true);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(FHScreens.ARROW_TEXTURE, x + 73, y + 35, 0, 0, menu.getScaledArrowProgress(), 16, 24, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
