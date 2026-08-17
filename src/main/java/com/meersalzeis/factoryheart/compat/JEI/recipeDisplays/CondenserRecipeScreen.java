package com.meersalzeis.factoryheart.compat.JEI.recipeDisplays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.meersalzeis.factoryheart.gui.menus.CondenserMenu;
import com.meersalzeis.factoryheart.gui.renderer.FHScreens;
import com.meersalzeis.factoryheart.util.MouseUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;


public class CondenserRecipeScreen extends AbstractContainerScreen<CondenserMenu> {

    public CondenserRecipeScreen(CondenserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelX = 64;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, FHScreens.BASIC_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(FHScreens.BASIC_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrow(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(FHScreens.ARROW_TEXTURE, x + 70, y + 30, 0, 0, 24, 16, 24, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
