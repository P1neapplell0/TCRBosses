package net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem;

import com.brass_amber.ba_bt.BABTMain;
import com.brass_amber.ba_bt.entity.hostile.golem.BTSkyGolem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.shelmarow.tcr_bosses.TCRBosses;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SkyGolemRenderer extends HumanoidMobRenderer<BTSkyGolem, HumanoidModel<BTSkyGolem>> {
    ResourceLocation DORMANT = ResourceLocation.fromNamespaceAndPath(TCRBosses.MOD_ID, "textures/entity/golem/sky_golem/sky_golem_dormant.png");
    ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(TCRBosses.MOD_ID, "textures/entity/golem/sky_golem/sky_golem.png");

    public SkyGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BTSkyGolem pEntity) {
        return pEntity.isDormant() ? DORMANT : NORMAL;
    }
}
