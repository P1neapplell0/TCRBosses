package com.p1nero.tcr_bosses.mixins;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Ancient_Remnant.Ancient_Remnant_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.IABoss_monster;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Maledictus.Maledictus_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.NewNetherite_Monstrosity.Netherite_Monstrosity_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmStaminaPatch;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Netherite_Monstrosity_Entity.class, Ancient_Remnant_Entity.class, Maledictus_Entity.class, Scylla_Entity.class})
public abstract class CataclysmInternalBossStaminaMixin extends IABoss_monster {
    protected CataclysmInternalBossStaminaMixin(EntityType entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$staggerTick(CallbackInfo callback) {
        if (CataclysmStaminaPatch.isLocked(this)) {
            super.tick();
            CataclysmStaminaPatch.get(this).freeze();
            callback.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$staggerAiStep(CallbackInfo callback) {
        if (CataclysmStaminaPatch.isLocked(this)) {
            super.aiStep();
            CataclysmStaminaPatch.get(this).freeze();
            callback.cancel();
        }
    }
}
