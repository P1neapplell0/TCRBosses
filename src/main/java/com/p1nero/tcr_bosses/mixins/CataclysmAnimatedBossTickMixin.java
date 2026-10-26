package com.p1nero.tcr_bosses.mixins;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ender_Guardian_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.LLibrary_Boss_Monster;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmStaminaPatch;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Ender_Guardian_Entity.class, Ignis_Entity.class, The_Leviathan_Entity.class})
public abstract class CataclysmAnimatedBossTickMixin extends LLibrary_Boss_Monster {
    protected CataclysmAnimatedBossTickMixin(EntityType entityType, Level level) {
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
}
