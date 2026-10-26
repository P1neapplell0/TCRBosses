package com.p1nero.tcr_bosses.mixins;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ender_Guardian_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.NewNetherite_Monstrosity.Netherite_Monstrosity_Entity;
import com.github.L_Ender.cataclysm.entity.etc.CMBossInfoServer;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmBossAccess;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {
        Ender_Guardian_Entity.class,
        Netherite_Monstrosity_Entity.class,
        Ignis_Entity.class,
        The_Leviathan_Entity.class
}, remap = false)
public abstract class CataclysmBossBarAccessMixin implements CataclysmBossAccess {
    @Shadow(remap = false)
    @Final
    private CMBossInfoServer bossInfo;

    @Override
    public ServerBossEvent tcr_bosses$getHealthBossEvent() {
        return bossInfo;
    }
}
