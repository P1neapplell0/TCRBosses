package com.p1nero.tcr_bosses.mixins;

import com.hm.efn.gameasset.EFNAnimations;
import com.p1nero.tcr_bosses.entity.battle_tower.land_golem.LandGolemPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(EFNAnimations.class)
public class EFNAnimationsMixin {

    @Inject(method = "calculateWeaponSpeedWithCap", at = @At("HEAD"), cancellable = true, remap = false)
    private static void tcr$calculateWeaponSpeedWithCap(DynamicAnimation animation, LivingEntityPatch<?> entitypatch, float speedCap, float defaultBasisSpeed, CallbackInfoReturnable<Float> cir) {
        if(entitypatch instanceof LandGolemPatch) {
            cir.setReturnValue(0.7F);
        }
    }

}
