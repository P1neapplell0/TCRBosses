package com.p1nero.tcr_bosses.entity.cataclysm.maledictus;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.p1nero.tcr_bosses.TCRBossesMod;
import net.shelmarow.combat_evolution.ai.CEBossEntity;
import org.jetbrains.annotations.NotNull;

public class MaledictusHumanoidRenderer<T extends CEBossEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {

    public MaledictusHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/maledictus_humanoid.png");
    }
}
