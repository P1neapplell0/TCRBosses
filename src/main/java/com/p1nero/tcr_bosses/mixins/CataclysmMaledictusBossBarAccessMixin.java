package com.p1nero.tcr_bosses.mixins;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Maledictus.Maledictus_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmBossAccess;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Maledictus_Entity.class, remap = false)
public abstract class CataclysmMaledictusBossBarAccessMixin implements CataclysmBossAccess {
    @Shadow(remap = false)
    @Final
    private CMBossInfoServer bossEvent1;

    @Override
    public ServerBossEvent tcr_bosses$getHealthBossEvent() {
        return bossEvent1;
    }
}
