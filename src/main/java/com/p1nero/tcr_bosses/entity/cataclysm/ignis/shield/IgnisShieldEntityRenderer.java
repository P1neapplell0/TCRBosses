package com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import com.p1nero.tcr_bosses.TCRBossesMod;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IgnisShieldEntityRenderer extends GeoEntityRenderer<IgnisShieldEntity> {
    private final MobEffect soulFire;
    private final ResourceLocation COMMON = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ignis_humanoid.png");
    private final ResourceLocation SOUL = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID,"textures/entity/ignis_humanoid_soul.png");

    public IgnisShieldEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID, "ignis_shield")));
        soulFire = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.parse("tcrcore:soul_incinerator"));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(IgnisShieldEntity shieldEntity) {
        if(shieldEntity.getOwner() == null) {
            return COMMON;
        }
        return shieldEntity.getOwner().hasEffect(soulFire) ? SOUL : COMMON;
    }
}
