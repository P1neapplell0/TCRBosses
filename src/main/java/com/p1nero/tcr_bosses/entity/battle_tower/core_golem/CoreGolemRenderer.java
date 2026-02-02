package com.p1nero.tcr_bosses.entity.battle_tower.core_golem;

import com.brass_amber.ba_bt.BABattleTowers;
import com.brass_amber.ba_bt.entity.hostile.golem.CoreGolem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CoreGolemRenderer extends HumanoidMobRenderer<CoreGolem, HumanoidModel<CoreGolem>> {
    ResourceLocation DORMANT = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/core_golem/core_golem_dormant.png");
    ResourceLocation ENRAGED = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/core_golem/core_golem_cracked.png");
    ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/core_golem/core_golem.png");

    public CoreGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CoreGolem pEntity) {
        return pEntity.isDormant() ? DORMANT : (pEntity.isEnraged() ? ENRAGED : NORMAL);
    }
}
