package com.p1nero.tcr_bosses.entity.custom.nether;

import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NetherSmallBossRenderer<T extends BaseSmallBossEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
    private static final ResourceLocation WITHER_SKELETON_LOCATION = ResourceLocation.parse("textures/entity/skeleton/wither_skeleton.png");

    public NetherSmallBossRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(context.bakeLayer(ModelLayers.WITHER_SKELETON_INNER_ARMOR)), new SkeletonModel(context.bakeLayer(ModelLayers.WITHER_SKELETON_OUTER_ARMOR)), context.getModelManager()));
    }
    
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return WITHER_SKELETON_LOCATION;
    }
}
