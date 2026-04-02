package com.p1nero.tcr_bosses.entity.custom.aether.valkyrie;

import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ValkyrieQueenRenderer<T extends BaseSmallBossEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {

    private static final ResourceLocation TEXTURE_PATH = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID, "textures/entity/valkyrie.png");

    public ValkyrieQueenRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SkeletonModel(context.bakeLayer(ModelLayers.WITHER_SKELETON_INNER_ARMOR)), new SkeletonModel(context.bakeLayer(ModelLayers.WITHER_SKELETON_OUTER_ARMOR)), context.getModelManager()));
    }
    
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return TEXTURE_PATH;
    }
}
