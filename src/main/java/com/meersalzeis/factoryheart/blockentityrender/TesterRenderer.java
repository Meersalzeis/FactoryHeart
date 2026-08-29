package com.meersalzeis.factoryheart.blockentityrender;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.meersalzeis.factoryheart.FHModMain;
import com.meersalzeis.factoryheart.block.crafting.TesterBlock;
import com.meersalzeis.factoryheart.blockentity.crafting.ExtractorBlockEntity;
import com.meersalzeis.factoryheart.blockentity.crafting.TesterBlockEntity;
import com.meersalzeis.factoryheart.item.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TesterRenderer implements BlockEntityRenderer<TesterBlockEntity> {

    private static final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private static final ResourceLocation SCAN_TEXTURE = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID,"textures/blockanimation/tester_scan.png");

    public TesterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(TesterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        //prep
        int craftProg = blockEntity.getProgress();
        float animProg = (craftProg == 0) ? 0 : (float)(2 * Math.PI * craftProg / blockEntity.getMaxProgress());
        float animOffset = (float)(Math.sin(animProg)) * 0.35f;

        Direction faces = blockEntity.getBlockState().getValue(TesterBlock.FACING);
        int rotation = 0;
        switch (faces) {
            case Direction.NORTH:
                rotation = 90;
                break;
            case Direction.WEST:
                rotation = 180;
                break;
            case Direction.SOUTH:
                rotation = 270;
                break;
            case Direction.EAST:
                rotation = 0;
                break;
            default:
                rotation = 45;
        }

        renderInput(poseStack, rotation, blockEntity, bufferSource);

        boolean isLit = blockEntity.getBlockState().getValue(TesterBlock.LIT);
        if (!isLit) return;

        renderScan(poseStack, animOffset, blockEntity, bufferSource, packedLight, packedOverlay);
    }

    private static void renderInput(PoseStack poseStack, int rotation, TesterBlockEntity blockEntity, MultiBufferSource bufferSource) {
        ItemStack inputStack = blockEntity.itemHandler.getStackInSlot(ExtractorBlockEntity.INPUT_SLOT);
        
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // Turn input orthogonally to plunger, in cardinal directions
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation+90));
        

        itemRenderer.renderStatic(
            inputStack, ItemDisplayContext.FIXED, BlockEntRenderUtil.getLightLevel(blockEntity.getLevel(),
            blockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack,
            bufferSource, blockEntity.getLevel(), 1
        );
        
        poseStack.popPose();
    }
    
    private static void renderScan(PoseStack poseStack, float animOffset, TesterBlockEntity blockEntity, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        
        poseStack.translate(0.1f, (0.1f + animOffset), 0.1f);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        RenderType renderType = RenderType.entityCutoutNoCull(SCAN_TEXTURE);
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        
        addVertices(buffer, poseStack, packedLight, packedOverlay);
        
        poseStack.popPose();
    }

    private static void addVertices(VertexConsumer buffer, PoseStack poseStack, int packedLight, int packedOverlay) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float min = 0.0F;
        float max = 1.0F;
        float y = 0.501F;

        buffer.addVertex(matrix, min, y, min)
            .setColor(255, 255, 255, 255)
            .setUv(0.0F, 1.0F)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);

        buffer.addVertex(matrix, min, y, max)
            .setColor(255, 255, 255, 255)
            .setUv(0.0F, 0.0F)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);

        buffer.addVertex(matrix, max, y, max)
            .setColor(255, 255, 255, 255)
            .setUv(1.0F, 0.0F)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);

        buffer.addVertex(matrix, max, y, min)
            .setColor(255, 255, 255, 255)
            .setUv(1.0F, 1.0F)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
