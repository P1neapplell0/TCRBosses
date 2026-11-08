package com.p1nero.tcr_bosses.mixins.wom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Pseudo
@Mixin(targets = "reascer.wom.gameasset.animations.weapons.AnimsSatsujin", remap = false)
public abstract class WomSatsujinAnimationSafetyMixin {
    @Inject(method = "lambda$build$39", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void tcr_bosses$useSafeSatsujinCondition(LivingEntityPatch<?> entityPatch,
                                                           CallbackInfoReturnable<Integer> callback) {
        if (!(entityPatch instanceof PlayerPatch<?>)) {
            callback.setReturnValue(0);
        }
    }
}
