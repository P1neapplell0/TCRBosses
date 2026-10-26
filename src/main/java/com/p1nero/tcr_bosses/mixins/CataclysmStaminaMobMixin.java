package com.p1nero.tcr_bosses.mixins;

import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmStaminaPatch;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class CataclysmStaminaMobMixin {
    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$stopStaggeredAi(CallbackInfo callback) {
        if (CataclysmStaminaPatch.isLocked((Mob) (Object) this)) {
            callback.cancel();
        }
    }
}
