package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.client.ClientEvents;
import com.brass_amber.ba_bt.init.BTEntityType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.shelmarow.tcr_bosses.entity.battle_tower.core_golem.CoreGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.land_golem.LandGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.nether_golem.NetherGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.ocean_golem.OceanGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ClientEvents.class)
public class BTClientEventMixin {


    @Redirect(
            method = "registerRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/EntityRenderersEvent$RegisterRenderers;registerEntityRenderer(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/client/renderer/entity/EntityRendererProvider;)V",
                    ordinal = 0,
                    remap = false
            ),
            remap = false
    )
    private static void register1(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.LAND_GOLEM.get(), LandGolemRenderer::new);
    }

    @Redirect(
            method = "registerRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/EntityRenderersEvent$RegisterRenderers;registerEntityRenderer(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/client/renderer/entity/EntityRendererProvider;)V",
                    ordinal = 1,
                    remap = false
            ),
            remap = false
    )
    private static void register2(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.OCEAN_GOLEM.get(), OceanGolemRenderer::new);
    }

    @Redirect(
            method = "registerRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/EntityRenderersEvent$RegisterRenderers;registerEntityRenderer(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/client/renderer/entity/EntityRendererProvider;)V",
                    ordinal = 2,
                    remap = false
            ),
            remap = false
    )
    private static void register3(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.NETHER_GOLEM.get(), NetherGolemRenderer::new);
    }

    @Redirect(
            method = "registerRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/EntityRenderersEvent$RegisterRenderers;registerEntityRenderer(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/client/renderer/entity/EntityRendererProvider;)V",
                    ordinal = 3,
                    remap = false
            ),
            remap = false
    )
    private static void register4(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.CORE_GOLEM.get(), CoreGolemRenderer::new);
    }

    @Redirect(
            method = "registerRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/EntityRenderersEvent$RegisterRenderers;registerEntityRenderer(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/client/renderer/entity/EntityRendererProvider;)V",
                    ordinal = 4,
                    remap = false
            ),
            remap = false
    )
    private static void register5(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.END_GOLEM.get(), EndGolemRenderer::new);
    }

    @Redirect(
            method = "registerRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/event/EntityRenderersEvent$RegisterRenderers;registerEntityRenderer(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/client/renderer/entity/EntityRendererProvider;)V",
                    ordinal = 5,
                    remap = false
            ),
            remap = false
    )
    private static void register6(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.SKY_GOLEM.get(), SkyGolemRenderer::new);
    }
}
