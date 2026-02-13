package com.p1nero.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.AbstractGolem;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractGolem.class)
public interface BTAbstractGolemAccessor {
    @Accessor(value = "bossBar", remap = false)
    ServerBossEvent getBossEvent();
}
