package com.p1nero.tcr_bosses.entity.cataclysm.maledictus;

import com.ao.tcrmeshes.TCRMeshes;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.nameless.indestructible.world.capability.AdvancedCustomHumanoidMobPatch;
import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import com.p1nero.tcr_bosses.gameassets.TCRBMeshes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.joml.Vector4f;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.forgeevent.PrepareModelEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.mixin.client.MixinEntityRenderer;
import yesman.epicfight.mixin.client.MixinLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PMaledictusHumanoidGolemRenderer extends PHumanoidRenderer {

    public PMaledictusHumanoidGolemRenderer(AssetAccessor<HumanoidMesh> mesh, EntityRendererProvider.Context context, EntityType<?> entityType) {
        super(mesh, context, entityType);
    }

    @Override
    public void render(LivingEntity entity, LivingEntityPatch entitypatch, LivingEntityRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
        RenderNameTagEvent renderNameplateEvent = new RenderNameTagEvent(entity, entity.getDisplayName(), renderer, poseStack, buffer, packedLight, partialTicks);
        MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
        MixinEntityRenderer entityRendererAccessor = (MixinEntityRenderer) renderer;
        if ((entityRendererAccessor.invokeShouldShowName(entity) || renderNameplateEvent.getResult() == Event.Result.ALLOW) && renderNameplateEvent.getResult() != Event.Result.DENY) {
            entityRendererAccessor.invokeRenderNameTag(entity, renderNameplateEvent.getContent(), poseStack, buffer, packedLight);
        }


        Minecraft mc = Minecraft.getInstance();
        MixinLivingEntityRenderer livingEntityRendererAccessor = (MixinLivingEntityRenderer) renderer;

        boolean isVisible = livingEntityRendererAccessor.invokeIsBodyVisible(entity);
        boolean isVisibleToPlayer = !isVisible && !entity.isInvisibleTo(mc.player);
        boolean isGlowing = mc.shouldEntityAppearGlowing(entity);

        RenderType renderType = livingEntityRendererAccessor.invokeGetRenderType(entity, isVisible, isVisibleToPlayer, isGlowing);
        Armature armature = entitypatch.getArmature();

        RenderType wingRenderType = RenderType.entityTranslucent(TCRMeshes.MALEDICTUS_HUMANOID_TEXTURE);

        poseStack.pushPose();

        this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
        this.prepareVanillaModel(entity, renderer.getModel(), renderer, partialTicks);
        this.setArmaturePose(entitypatch, armature, partialTicks);

        if (renderType != null) {

            HumanoidMesh mesh = (HumanoidMesh) this.getMeshProvider(entitypatch).get();
            this.prepareModel(mesh, entity, entitypatch, renderer);


            PrepareModelEvent prepareModelEvent = new PrepareModelEvent(this, mesh, entitypatch, buffer, poseStack, packedLight, partialTicks);
            if (!MinecraftForge.EVENT_BUS.post(prepareModelEvent)) {
                Vector4f color = new Vector4f(1.0F, 1.0F, 1.0F, isVisibleToPlayer ? 0.15F : 1.0F);
                entitypatch.getEntityDecorations().modifyColor(color, partialTicks);

                int blockLight = (packedLight & 240) >> 4;
                int skyLight = (packedLight & 15728640) >> 20;
                Vec2i lightUv = new Vec2i(blockLight, skyLight);
                entitypatch.getEntityDecorations().modifyLight(lightUv, partialTicks);
                int modifiedLight = LightTexture.pack(lightUv.x, lightUv.y);

                //挥一次的时间
                int wingTime = 40;
                //挥动角度
                float wingAngle = 15;

                float wingRatio = (entity.tickCount % wingTime + partialTicks) / (float) wingTime;

                Pose poseL = entitypatch.getAnimator().getPose(partialTicks);
                AvalonAnimationUtils.joinRotationInPose(poseL, entitypatch, "Chest", 0, wingAngle * Math.abs(wingRatio - 0.5F) * 2, 0);
                SkinnedMesh wingMeshL = TCRBMeshes.M_WING_L.get();
                wingMeshL.draw(poseStack, buffer, wingRenderType, modifiedLight, color.x(), color.y(), color.z(), 0.5F,
                        OverlayTexture.NO_OVERLAY, armature, armature.getPoseAsTransformMatrix(poseL, false));

                Pose poseR = entitypatch.getAnimator().getPose(partialTicks);
                AvalonAnimationUtils.joinRotationInPose(poseR, entitypatch, "Chest", 0, -wingAngle * Math.abs(wingRatio - 0.5F) * 2, 0);
                SkinnedMesh wingMeshR = TCRBMeshes.M_WING_R.get();
                wingMeshR.draw(poseStack, buffer, wingRenderType, modifiedLight, color.x(), color.y(), color.z(), 0.5F,
                        OverlayTexture.NO_OVERLAY, armature, armature.getPoseAsTransformMatrix(poseR, false));

                mesh.draw(poseStack, buffer, renderType, modifiedLight, color.x(), color.y(), color.z(), color.w(), this.getOverlayCoord(entity, entitypatch, partialTicks), armature, armature.getPoseMatrices());
                entitypatch.getEntityDecorations().listDecorationOverlays().forEach((decorationOverlay) -> {
                    if (!decorationOverlay.shouldRemove() && decorationOverlay.shouldRender()) {
                        Vector4f overlayColor = decorationOverlay.color(partialTicks);
                        mesh.draw(poseStack, buffer, decorationOverlay.getRenderType(), modifiedLight, overlayColor.x(), overlayColor.y(), overlayColor.z(), overlayColor.w(), OverlayTexture.NO_OVERLAY, armature, armature.getPoseMatrices());
                    }

                });
            }
        }

        if (!entity.isSpectator()) {
            this.renderLayer(renderer, entitypatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
        }

        if (renderType != null && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
        }

        poseStack.popPose();
    }
}
