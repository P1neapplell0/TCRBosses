package com.p1nero.tcr_bosses.mixins.stamina;

import com.bobmowzie.mowziesmobs.server.bossinfo.MMBossInfoServer;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossBarAccess;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "com.bobmowzie.mowziesmobs.server.entity.MowzieEntity", remap = false)
public abstract class MowzieBossBarAccessMixin implements NativeBossBarAccess {
    @Shadow(remap = false) @Final
    protected MMBossInfoServer bossInfo;

    @Override
    public ServerBossEvent tcr_bosses$getHealthBossEvent() {
        return bossInfo;
    }
}
