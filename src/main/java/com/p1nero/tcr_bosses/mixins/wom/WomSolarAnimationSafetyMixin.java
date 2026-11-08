package com.p1nero.tcr_bosses.mixins.wom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.property.AnimationParameters;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Pseudo
@Mixin(targets = "reascer.wom.gameasset.animations.weapons.AnimsSolar", remap = false)
public abstract class WomSolarAnimationSafetyMixin {
    @Inject(method = "lambda$build$34", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void tcr_bosses$skipNonPlayerSolarEvent(LivingEntityPatch<?> entityPatch,
                                                           AssetAccessor<?> animation,
                                                           AnimationParameters<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> parameters,
                                                           CallbackInfo callback) {
        if (!(entityPatch instanceof PlayerPatch<?>)) {
            callback.cancel();
        }
    }

    @Inject(method = "lambda$build$25", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void tcr_bosses$useSafeSolarCondition(LivingEntityPatch<?> entityPatch,
                                                         CallbackInfoReturnable<Integer> callback) {
        if (!(entityPatch instanceof PlayerPatch<?>)) {
            callback.setReturnValue(0);
        }
    }
}
