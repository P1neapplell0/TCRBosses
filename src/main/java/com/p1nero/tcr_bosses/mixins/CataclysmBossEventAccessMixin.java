package com.p1nero.tcr_bosses.mixins;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Harbinger_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Ancient_Remnant.Ancient_Remnant_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmBossAccess;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {The_Harbinger_Entity.class, Ancient_Remnant_Entity.class, Scylla_Entity.class}, remap = false)
public abstract class CataclysmBossEventAccessMixin implements CataclysmBossAccess {
    @Shadow(remap = false)
    @Final
    private CMBossInfoServer bossEvent;

    @Override
    public ServerBossEvent tcr_bosses$getHealthBossEvent() {
        return bossEvent;
    }
}
