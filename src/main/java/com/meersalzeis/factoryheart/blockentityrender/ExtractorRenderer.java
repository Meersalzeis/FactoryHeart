package com.meersalzeis.factoryheart.blockentityrender;

import com.meersalzeis.factoryheart.FHModClient;
import com.meersalzeis.factoryheart.block.crafting.ExtractorBlock;
import com.meersalzeis.factoryheart.blockentity.crafting.ExtractorBlockEntity;
import com.meersalzeis.factoryheart.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ExtractorRenderer implements BlockEntityRenderer<ExtractorBlockEntity> {

    private static final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private static ItemStack plungerStack = new ItemStack(ModItems.PLUNGER.get());

    public ExtractorRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ExtractorBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction faces = blockEntity.getBlockState().getValue(ExtractorBlock.FACING);
        int craftProg = blockEntity.getProgress();
        float animProg = (craftProg == 0) ? 0 : (float)(2 * Math.PI * craftProg / blockEntity.getMaxProgress());
        float animOffset = (float)(Math.sin(animProg)) * 0.1f;
        float xOffset = 0;
        float zOffset = 0;
        int rotation = 0;
        switch (faces) {
            case Direction.NORTH:
                zOffset = -0.25f - animOffset;
                rotation = 90;
                break;
            case Direction.WEST:
                xOffset = -0.25f - animOffset;
                rotation = 180;
                break;
            case Direction.SOUTH:
                zOffset = 0.25f + animOffset;
                rotation = 270;
                break;
            case Direction.EAST:
                xOffset = 0.25f + animOffset;
                rotation = 0;
                break;
            default:
                xOffset = 1.25f;
        }

        renderPlunger(poseStack, xOffset, zOffset, rotation, blockEntity, bufferSource);
        renderInput(poseStack, rotation, blockEntity, bufferSource);
    }
    
    private static void renderPlunger(PoseStack poseStack, float xOffset, float zOffset, int rotation, ExtractorBlockEntity blockEntity, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        poseStack.translate(0.5f + xOffset, 0.5f, 0.5f + zOffset);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // Turn plunger in correct cardinal direction
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        // Adjust plunger downwards such that it directly faces center
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        

        itemRenderer.renderStatic(
            plungerStack, ItemDisplayContext.FIXED, BlockERenderUtil.getLightLevel(blockEntity.getLevel(),
            blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack,
            bufferSource, blockEntity.getLevel(), 1
        );
        
        poseStack.popPose();
    }

    private static void renderInput(PoseStack poseStack, int rotation, ExtractorBlockEntity blockEntity, MultiBufferSource bufferSource) {
        ItemStack inputStack = blockEntity.itemHandler.getStackInSlot(ExtractorBlockEntity.INPUT_SLOT);
        
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // Turn input orthogonally to plunger, in cardinal directions
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation+90));
        

        itemRenderer.renderStatic(
            inputStack, ItemDisplayContext.FIXED, BlockERenderUtil.getLightLevel(blockEntity.getLevel(),
            blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack,
            bufferSource, blockEntity.getLevel(), 1
        );
        
        poseStack.popPose();
    }
}
