package com.meersalzeis.factoryheart.gui.screens;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.gui.menus.ExtractorMenu;
import com.meersalzeis.factoryheart.gui.renderer.FHScreens;
import com.meersalzeis.factoryheart.gui.renderer.DisplayHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ExtractorScreen extends AbstractContainerScreen<ExtractorMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/extractor/extractor_gui.png");

    public ExtractorScreen(ExtractorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelX = 62;
        this.titleLabelY = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        FHScreens.setRenderSystem(GUI_TEXTURE);
        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics, x, y);

        int tier = menu.getTier();
        DisplayHelper.renderTierDisplay(guiGraphics, tier, x, y);
        DisplayHelper.renderTierTooltip(guiGraphics, mouseX, mouseY, x, y, tier, true);

        if (menu.isRecipeInfinite()) {
            DisplayHelper.renderNoInputConsumedTooltip(guiGraphics, mouseX, mouseY, x+45, y+16);
            DisplayHelper.renderNoInputConsumedDisplay(guiGraphics, x+45, y+16);
        }
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
