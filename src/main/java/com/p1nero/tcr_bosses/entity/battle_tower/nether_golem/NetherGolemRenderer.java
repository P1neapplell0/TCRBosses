package com.p1nero.tcr_bosses.entity.battle_tower.nether_golem;

import com.brass_amber.ba_bt.BABattleTowers;
import com.brass_amber.ba_bt.entity.hostile.golem.NetherGolem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class NetherGolemRenderer extends HumanoidMobRenderer<NetherGolem, HumanoidModel<NetherGolem>> {
    ResourceLocation DORMANT = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/nether_golem/nether_golem_dormant.png");
    ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/nether_golem/nether_golem.png");
    ResourceLocation NORMAL_P1 = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/nether_golem/nether_golem_cracked_stage_1.png");
    ResourceLocation NORMAL_P2 = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/nether_golem/nether_golem_cracked_stage_2.png");
    ResourceLocation NORMAL_P3 = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/nether_golem/blaze_metal_golem/blaze_metal_golem.png");

    public NetherGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull NetherGolem pEntity) {
        if(pEntity.isDormant()){
            return DORMANT;
        }
        else{
            float healthRatio = pEntity.getHealth() / pEntity.getMaxHealth();
            if(healthRatio > 0.5F && healthRatio <= 0.75F){
                return NORMAL_P1;
            }
            else if (healthRatio > 0.25F && healthRatio <= 0.5F) {
                return NORMAL_P2;
            }
            else if (healthRatio <= 0.25F) {
                return NORMAL_P3;
            }
        }
        return NORMAL;
    }
}
