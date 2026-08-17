package com.meersalzeis.factoryheart.blockentityrender;

import com.meersalzeis.factoryheart.blockentity.crafting.BlazerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlazerRenderer implements BlockEntityRenderer<BlazerBlockEntity> {

    private static final ItemRenderer itemRenderer = BlockERenderUtil.itemRenderer;
    private static ItemStack stack = new ItemStack(Items.BLAZE_ROD);

    public BlazerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(BlazerBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        float animProg = blockEntity.getRodAnimationProgress();
        renderBlazeRod(poseStack, blockEntity, animProg, bufferSource);
        renderBlazeRod(poseStack, blockEntity, (float)(animProg+Math.PI*0.5), bufferSource);
        renderBlazeRod(poseStack, blockEntity, (float)(animProg+Math.PI), bufferSource);
        renderBlazeRod(poseStack, blockEntity, (float)(animProg+Math.PI*1.5), bufferSource);
    }

    private void renderBlazeRod(PoseStack poseStack, BlazerBlockEntity blockEntity, float animProg, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        
        if (blockEntity.isVertical()) {
            poseStack.translate(0.5f + 0.25f*Math.sin(animProg), 0.5f, 0.5f + 0.25f*Math.cos(animProg));
            
            // Adjust rods to face inwards with sides
            poseStack.mulPose(Axis.YP.rotationDegrees(animProg * 180 / (float)Math.PI));

            // Adjust rods to face point to barrel
            poseStack.mulPose(Axis.ZN.rotationDegrees(45));
        } else {
            if (blockEntity.pointsToPole()) {
                poseStack.translate(0.5f + 0.25f*Math.cos(-animProg), 0.5f + 0.25f*Math.sin(-animProg), 0.5f);

                // Adjust rods to face inwards with sides
                poseStack.mulPose(Axis.ZN.rotationDegrees(animProg * 180 / (float)Math.PI));
                poseStack.mulPose(Axis.YN.rotationDegrees(90));
            } else {
                poseStack.translate(0.5f, 0.5f + 0.25f*Math.sin(animProg), 0.5f + 0.25f*Math.cos(animProg));

                // Adjust rods to face inwards with sides
                poseStack.mulPose(Axis.XN.rotationDegrees(animProg * 180 / (float)Math.PI));
            }

            // Adjust rods to point parallel to barrel
            poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        }
        poseStack.scale(0.5f, 0.5f, 0.5f);

        itemRenderer.renderStatic(
            stack, ItemDisplayContext.FIXED, BlockERenderUtil.getLightLevel(blockEntity.getLevel(),
            blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack,
            bufferSource, blockEntity.getLevel(), 1
        );
        
        poseStack.popPose();
    }
}
