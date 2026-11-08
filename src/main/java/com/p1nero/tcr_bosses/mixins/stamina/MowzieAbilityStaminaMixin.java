package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.bobmowzie.mowziesmobs.server.capability.AbilityCapability$AbilityCapabilityImp", remap = false)
public abstract class MowzieAbilityStaminaMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$freezeAbilities(LivingEntity entity, CallbackInfo callback) {
        if (entity instanceof Mob mob && NativeBossStaminaPatch.isLocked(mob)) {
            callback.cancel();
        }
    }
}
