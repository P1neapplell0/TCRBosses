package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossCompanion;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenTentacleEntity")
public abstract class KrakenTentacleStaminaMixin extends Monster implements NativeBossCompanion {
    protected KrakenTentacleStaminaMixin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Shadow(remap = false)
    public abstract KrakenEntity getOwnerKraken();

    @Override
    public boolean tcr_bosses$isOwnerBroken() {
        KrakenEntity owner = getOwnerKraken();
        return owner != null && NativeBossStaminaPatch.isLocked(owner);
    }

    @Inject(method = {"baseTick", "m_6075_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$freezeTentacleAnimation(CallbackInfo callback) {
        if (tcr_bosses$isOwnerBroken()) {
            goalSelector.getRunningGoals().toList().forEach(goal -> goal.stop());
            getNavigation().stop();
            setDeltaMovement(Vec3.ZERO);
            super.baseTick();
            callback.cancel();
        }
    }
}
