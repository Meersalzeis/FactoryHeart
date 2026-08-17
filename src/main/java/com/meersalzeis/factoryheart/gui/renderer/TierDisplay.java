package com.meersalzeis.factoryheart.gui.renderer;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.util.MouseUtil;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TierDisplay {

    private static final ResourceLocation TIER_SCALE_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/tier_scale.png");
    private static final ResourceLocation TIER_4_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/tier_4.png");

    public static Component getHasTooltip(int tier) {
        // Component.translatable("factoryheart.gui.hasTier") + tier?
        return Component.literal("Can craft up to tier " + tier);
    }

    public static Component getNeedsTooltip(int tier) {
        // Component.translatable("factoryheart.gui.needsTier") + tier ?
        return Component.literal("Needs tier " + tier + " or higher to craft");
    }
    
    public static void renderTierDisplay(GuiGraphics guiGraphics, int tier, int x, int y) {
        if(tier == 4) {
            guiGraphics.blit(TIER_4_TEXTURE, x+134,  y+25, 0, 0, 16, 32, 16, 32);
        } else {
            guiGraphics.blit(TIER_SCALE_TEXTURE, x+134,  y + 41 + 16 - getScaleVisibleSize(tier), 0,
                    32 - getScaleVisibleSize(tier), 16, getScaleVisibleSize(tier),16, 32);
        }
    }

    public static int getScaleVisibleSize(int curTier) {
        // Only handles tiers from 0-3 not 4
        switch(curTier) {
            case 0: return 0;
            case 1: return 11;
            case 2: return 22;
            default: return 32;
        }
    }

    public static void renderTierTooltip(GuiGraphics guiGraphics, double pMouseX, double pMouseY, int x, int y, Font font, int tier, boolean has) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 132, 24, 18, 34)) {
            guiGraphics.renderTooltip(
                font,
                has ? getHasTooltip(tier) : getNeedsTooltip(tier),
                (int)Math.round(pMouseX), (int)Math.round(pMouseY)
            );
        }
    }

    public static boolean isMouseAboveArea(double pMouseX, double pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}
