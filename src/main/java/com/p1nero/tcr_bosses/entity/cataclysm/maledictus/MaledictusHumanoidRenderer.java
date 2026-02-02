package com.p1nero.tcr_bosses.entity.cataclysm.maledictus;

import com.ao.tcrmeshes.TCRMeshes;
import com.p1nero.tcr_bosses.entity.cataclysm.BaseBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MaledictusHumanoidRenderer<T extends BaseBossEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {

    public MaledictusHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return TCRMeshes.MALEDICTUS_HUMANOID_TEXTURE;
    }
}
