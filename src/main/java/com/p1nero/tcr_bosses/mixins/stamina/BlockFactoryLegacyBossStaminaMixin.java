package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {"net.unusual.block_factorys_bosses.entity.boss.yeti.YetiEntity",
        "net.unusual.block_factorys_bosses.entity.boss.dragon.boss.InfernalDragonEntity",
        "net.unusual.block_factorys_bosses.entity.boss.knight.UnderworldKnightEntity"})
public abstract class BlockFactoryLegacyBossStaminaMixin extends AbstractBossEntity {
    protected BlockFactoryLegacyBossStaminaMixin(EntityType type, Level level) {
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
