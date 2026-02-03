package com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.p1nero.tcr_bosses.TCRBossesMod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IgnisShieldEntityRenderer extends GeoEntityRenderer<IgnisShieldEntity> {
    private final ResourceLocation COMMON = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ignis_humanoid.png");
    private final ResourceLocation SOUL = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ignis_humanoid_soul.png");

    public IgnisShieldEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID, "ignis_shield")));
    }

    @Override
    public void render(@NotNull IgnisShieldEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.6F, 1.6F, 1.6F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(IgnisShieldEntity shieldEntity) {
        return shieldEntity.hasSoul() ? SOUL : COMMON;
    }
}
