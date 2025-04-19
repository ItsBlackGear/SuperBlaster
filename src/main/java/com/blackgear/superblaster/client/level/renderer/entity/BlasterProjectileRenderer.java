package com.blackgear.superblaster.client.level.renderer.entity;

import com.blackgear.superblaster.common.level.entity.BlasterProjectile;
import com.blackgear.superblaster.core.SuperBlaster;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlasterProjectileRenderer extends EntityRenderer<BlasterProjectile> {
    public static final ResourceLocation TEXTURE = SuperBlaster.resource("textures/entity/projectile/gunshot.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

    public BlasterProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(BlasterProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // Skip rendering if just spawned and too close to camera
        if (entity.tickCount < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25) {
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose pose = poseStack.last();

        renderSprite(pose, vertexConsumer, packedLight, 255, 255, 255);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderSprite(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, int red, int green, int blue) {
        vertex(consumer, pose, -0.5F, -0.5F, red, green, blue, 0.0F, 1.0F, packedLight);
        vertex(consumer, pose, 0.5F, -0.5F, red, green, blue, 1.0F, 1.0F, packedLight);
        vertex(consumer, pose, 0.5F, 0.5F, red, green, blue, 1.0F, 0.0F, packedLight);
        vertex(consumer, pose, -0.5F, 0.5F, red, green, blue, 0.0F, 0.0F, packedLight);
    }

    private static void vertex(
        VertexConsumer consumer,
        PoseStack.Pose pose,
        float x,
        float y,
        int red,
        int green,
        int blue,
        float u,
        float v,
        int packedLight
    ) {
        consumer.addVertex(pose, x, y, 0.0F)
            .setColor(red, green, blue, 230)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(BlasterProjectile entity) {
        return TEXTURE;
    }
}