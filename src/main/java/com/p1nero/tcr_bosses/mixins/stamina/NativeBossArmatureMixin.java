package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

@Mixin(value = Armatures.class, remap = false)
public abstract class NativeBossArmatureMixin {
    @Inject(method = "getArmatureFor", at = @At("HEAD"), cancellable = true)
    private static void tcr_bosses$useInternalArmature(EntityPatch<?> patch, CallbackInfoReturnable<Armature> callback) {
        if (patch instanceof NativeBossStaminaPatch) {
            callback.setReturnValue(Armatures.BIPED.get().deepCopy());
        }
    }
}
