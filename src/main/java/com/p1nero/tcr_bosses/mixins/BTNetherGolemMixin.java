package com.p1nero.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.AbstractGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.NetherGolem;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherGolem.class)
public abstract class BTNetherGolemMixin extends AbstractGolem {

    protected BTNetherGolemMixin(EntityType<? extends Monster> type, Level levelIn, BossEvent.BossBarColor bossBarColor) {
        super(type, levelIn, bossBarColor);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void tcr$init(EntityType type, Level levelIn, CallbackInfo ci) {
        this.xpReward = 2048;
    }
}