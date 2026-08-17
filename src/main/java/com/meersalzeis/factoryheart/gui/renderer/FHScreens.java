package com.meersalzeis.factoryheart.gui.renderer;

import com.meersalzeis.factoryheart.FHModMain;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class FHScreens {

    public static final ResourceLocation ARROW_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/arrow_progress.png");

    public static final ResourceLocation BASIC_TEXTURE =
        ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/gui/basic_gui.png");

    public static void setRenderSystem(ResourceLocation gui_texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, gui_texture);
    }
}
