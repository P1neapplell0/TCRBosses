package com.p1nero.tcr_bosses.mixins.stamina;

import com.bobmowzie.mowziesmobs.server.entity.MowzieLLibraryEntity;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import com.p1nero.tcr_bosses.entity.stamina.compat.MowzieStaminaCompat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {"com.bobmowzie.mowziesmobs.server.entity.wroughtnaut.EntityWroughtnaut",
        "com.bobmowzie.mowziesmobs.server.entity.frostmaw.EntityFrostmaw"})
public abstract class MowzieAnimatedBossStaminaMixin extends MowzieLLibraryEntity {
    protected MowzieAnimatedBossStaminaMixin(EntityType type, Level level) {
        super(type, level);
    }

    @Inject(method = {"tick", "m_8119_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$freezeScripts(CallbackInfo callback) {
        if (NativeBossStaminaPatch.isLocked(this)) {
            super.tick();
            MowzieStaminaCompat.tickReaction(this);
            NativeBossStaminaPatch.get(this).freeze();
            callback.cancel();
        }
    }

    @Inject(method = {"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", "m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},
            at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$unshieldBrokenBoss(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (NativeBossStaminaPatch.isLocked(this)) {
            callback.setReturnValue(super.hurt(source, amount));
        }
    }
}
