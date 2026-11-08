package com.p1nero.tcr_bosses.mixins.stamina;

import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"com.bobmowzie.mowziesmobs.server.entity.umvuthana.EntityUmvuthi",
        "com.bobmowzie.mowziesmobs.server.entity.sculptor.EntitySculptor"})
public abstract class MowzieGeckoBossStaminaMixin extends MowzieGeckoEntity {
    protected MowzieGeckoBossStaminaMixin(EntityType type, Level level) {
        super(type, level);
    }

    @Inject(method = {"tick", "m_8119_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$freezeScripts(CallbackInfo callback) {
        if (NativeBossStaminaPatch.isLocked(this)) {
            super.tick();
            NativeBossStaminaPatch.get(this).freeze();
            callback.cancel();
        }
    }
}
