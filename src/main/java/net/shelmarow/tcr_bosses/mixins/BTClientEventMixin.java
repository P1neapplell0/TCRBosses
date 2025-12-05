package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.client.ClientEvents;
import com.brass_amber.ba_bt.init.BTEntityType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.shelmarow.tcr_bosses.entity.battle_tower.nether_golem.NetherGolemRenderer;
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
                    ordinal = 5,
                    remap = false
            ),
            remap = false
    )
    private static void register(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.SKY_GOLEM.get(), SkyGolemRenderer::new);
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
    private static void register2(EntityRenderersEvent.RegisterRenderers instance, EntityType<?> entityType, EntityRendererProvider<?> entityRendererProvider){
        instance.registerEntityRenderer(BTEntityType.NETHER_GOLEM.get(), NetherGolemRenderer::new);
    }
}
