package com.p1nero.tcr_bosses.entity.cataclysm.scylla;

import com.ao.tcrmeshes.TCRMeshes;
import com.p1nero.tcr_bosses.entity.cataclysm.BaseBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ScyllaHumanoidRenderer<T extends BaseBossEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {

    public ScyllaHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return TCRMeshes.SCYLLA_HUMANOID_TEXTURE;
    }
}
