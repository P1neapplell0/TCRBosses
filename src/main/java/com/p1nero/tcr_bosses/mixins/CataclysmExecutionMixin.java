package com.p1nero.tcr_bosses.mixins;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = ExecutionHandler.class, remap = false)
public abstract class CataclysmExecutionMixin {
    @Inject(method = "isTargetGuardBreak", at = @At("HEAD"), cancellable = true)
    private static void tcr_bosses$nativeGuardBreak(AssetAccessor<? extends StaticAnimation> animation,
                                                  LivingEntityPatch<?> target, CallbackInfoReturnable<Boolean> callback) {
        if (target instanceof NativeBossStaminaPatch patch) {
            callback.setReturnValue(patch.isBroken());
        }
    }
}
