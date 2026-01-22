package net.shelmarow.tcr_bosses.entity.battle_tower.end_golem;

import com.brass_amber.ba_bt.BABattleTowers;
import com.brass_amber.ba_bt.entity.hostile.golem.EndGolem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class EndGolemRenderer extends HumanoidMobRenderer<EndGolem, HumanoidModel<EndGolem>> {
    ResourceLocation DORMANT = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/end_golem/end_golem_dormant.png");
    ResourceLocation ENRAGED = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/end_golem/end_golem_apparition.png");
    ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(BABattleTowers.MOD_ID, "textures/entity/golem/end_golem/end_golem.png");

    public EndGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EndGolem pEntity) {
        return pEntity.isDormant() ? DORMANT : (pEntity.isEnraged() ? ENRAGED : NORMAL);
    }
}
