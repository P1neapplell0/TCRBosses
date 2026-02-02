package com.p1nero.tcr_bosses.entity.cataclysm.ancient_remnant;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import com.p1nero.tcr_bosses.TCRBossesMod;
import org.jetbrains.annotations.NotNull;

public class AncientRemnantRenderer extends HumanoidMobRenderer<AncientRemnantHumanoid, HumanoidModel<AncientRemnantHumanoid>> {

    public AncientRemnantRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AncientRemnantHumanoid pEntity) {
        return ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ancient_remnant_humanoid.png");
    }
}
