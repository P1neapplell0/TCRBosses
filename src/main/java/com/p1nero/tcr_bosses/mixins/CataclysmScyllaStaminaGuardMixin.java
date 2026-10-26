package com.p1nero.tcr_bosses.mixins;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmStaminaPatch;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Scylla_Entity.class, remap = false)
public abstract class CataclysmScyllaStaminaGuardMixin {
    @Inject(method = "canBlockFaceSource", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$disableBrokenParry(DamageSource source, CallbackInfoReturnable<Boolean> callback) {
        if (CataclysmStaminaPatch.isLocked((Scylla_Entity) (Object) this)) {
            callback.setReturnValue(false);
        }
    }
}
