package net.shelmarow.tcr_bosses.mixins;

import com.hm.efn.gameasset.EFNAnimations;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.iml.ILivingEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = EFNAnimations.class)
public class EFNAnimationsMixin {

    @Inject(
            method = "calculateWeaponSpeedWithCap",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false
    )
    private static void getEFNPlaySpeed(DynamicAnimation animation, LivingEntityPatch<?> entityPatch, float speedCap, float defaultBasisSpeed, CallbackInfoReturnable<Float> cir){
        if(entityPatch instanceof CEHumanoidPatch ceHumanoidPatch){
            ILivingEntityData entityData = (ILivingEntityData) ceHumanoidPatch;
            cir.setReturnValue(entityData.combat_evolution$getAttackSpeed(entityPatch.getOriginal()));
        }
    }
}
