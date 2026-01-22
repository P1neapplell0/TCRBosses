package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.AbstractGolem;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractGolem.class,remap = false)
public interface BTAbstractGolemAccessor {
    @Accessor("bossBar")
    ServerBossEvent getBossEvent();
}
