package com.p1nero.tcr_bosses.entity.cataclysm.ignis;

import com.p1nero.tcr_bosses.entity.cataclysm.BaseBossEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import com.p1nero.tcr_bosses.TCRBossesMod;
import org.jetbrains.annotations.NotNull;

public class IgnisHumanoidRenderer<T extends BaseBossEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
    private final MobEffect soulFire;
    private final ResourceLocation COMMON = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ignis_humanoid.png");
    private final ResourceLocation SOUL = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ignis_humanoid_soul.png");
    public IgnisHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        soulFire = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse("tcrcore:soul_incinerator"));
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return pEntity.hasEffect(soulFire) ? SOUL : COMMON;
    }
}
