package com.p1nero.tcr_bosses.mixins.wom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Pseudo
@Mixin(targets = "reascer.wom.gameasset.animations.weapons.AnimsRuine", remap = false)
public abstract class WomRuineAnimationSafetyMixin {
    @Inject(method = "lambda$build$11", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void tcr_bosses$useSafeRuineCondition(LivingEntityPatch<?> entityPatch,
                                                        CallbackInfoReturnable<Integer> callback) {
        if (!(entityPatch instanceof PlayerPatch<?>)) {
            callback.setReturnValue(0);
        }
    }
}
