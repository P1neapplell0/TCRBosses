package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.unusual.block_factorys_bosses.entity.boss.AbstractStateBossEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenEntity")
public abstract class KrakenStaminaMixin extends AbstractStateBossEntity {
    protected KrakenStaminaMixin(EntityType type, Level level) {
        super(type, level);
    }

    @Inject(method = {"hurt", "m_6469_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$skipNativeKnockdown(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (NativeBossStaminaPatch.isLocked(this)) {
            callback.setReturnValue(super.hurt(source, amount));
        }
    }
}
