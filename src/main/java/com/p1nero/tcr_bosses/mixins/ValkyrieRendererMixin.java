package com.p1nero.tcr_bosses.mixins;

import com.aetherteam.aether.client.renderer.entity.ValkyrieRenderer;
import com.aetherteam.aether.client.renderer.entity.model.ValkyrieModel;
import com.aetherteam.aether.entity.monster.dungeon.Valkyrie;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ValkyrieRenderer.class)
public abstract class ValkyrieRendererMixin extends MobRenderer<Valkyrie, ValkyrieModel<Valkyrie>> {

    public ValkyrieRendererMixin(EntityRendererProvider.Context pContext, ValkyrieModel<Valkyrie> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void tcr$init(EntityRendererProvider.Context pContext, CallbackInfo ci) {
        this.addLayer(new ItemInHandLayer<>(this, pContext.getItemInHandRenderer()));
    }
}
