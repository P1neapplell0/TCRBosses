package com.p1nero.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.AbstractGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Invoker不知为毛用了会apply failed
 */
@Mixin(AbstractGolem.class)
public interface AbstractGolemInvoker {

    @Accessor(value = "SPAWN_POS", remap = false)
    EntityDataAccessor<BlockPos> getSpawnPosKey();

}
