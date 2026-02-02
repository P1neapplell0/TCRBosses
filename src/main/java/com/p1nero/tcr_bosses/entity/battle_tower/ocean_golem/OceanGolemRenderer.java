package com.p1nero.tcr_bosses.entity.battle_tower.ocean_golem;

import com.brass_amber.ba_bt.BABattleTowers;
import com.brass_amber.ba_bt.entity.hostile.golem.OceanGolem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class OceanGolemRenderer extends HumanoidMobRenderer<OceanGolem, HumanoidModel<OceanGolem>> {
    ResourceLocation DORMANT = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/ocean_golem/ocean_golem_dormant.png");
    ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/ocean_golem/ocean_golem.png");

    public OceanGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull OceanGolem pEntity) {
        return pEntity.isDormant() ? DORMANT : NORMAL;
    }
}
