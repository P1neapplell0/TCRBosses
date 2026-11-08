package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossBarAccess;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = {"net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity",
        "net.unusual.block_factorys_bosses.entity.boss.AbstractStateBossEntity"}, remap = false)
public abstract class BlockFactoryBossBarAccessMixin implements NativeBossBarAccess {
    @Shadow(remap = false) @Final
    protected ServerBossEvent bossEvent;

    @Override
    public ServerBossEvent tcr_bosses$getHealthBossEvent() {
        return bossEvent;
    }
}
