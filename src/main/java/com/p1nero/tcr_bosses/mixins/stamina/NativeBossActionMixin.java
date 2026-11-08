package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import com.p1nero.tcr_bosses.entity.stamina.compat.BomdBossAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class NativeBossActionMixin {
    @Inject(method = "broadcastEntityEvent", at = @At("HEAD"))
    private void tcr_bosses$recordNativeAttack(Entity entity, byte status, CallbackInfo callback) {
        if (entity instanceof Mob mob && mob instanceof BomdBossAccess access
                && NativeBossStaminaPatch.get(mob) != null && !NativeBossStaminaPatch.isLocked(mob)) {
            access.tcr_bosses$recordAction(status);
        }
    }
}
