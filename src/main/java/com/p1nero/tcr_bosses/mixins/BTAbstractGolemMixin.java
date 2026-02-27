package com.p1nero.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.*;
import com.brass_amber.ba_bt.util.GolemType;
import com.p1nero.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.bossbar.CEBossEvent;
import com.p1nero.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AbstractGolem.class)
public abstract class BTAbstractGolemMixin extends PathfinderMob {

    @Shadow(remap = false)
    @Final
    private ServerBossEvent bossBar;

    @Shadow(remap = false)
    public GolemType golemType;
    @Unique
    private final CEBossEvent tcr_bosses$ceBossEvent = new CEBossEvent(Component.empty());

    protected BTAbstractGolemMixin(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void tcr$init(CallbackInfo ci) {
        AbstractGolem abstractGolem = (AbstractGolem) (Object) this;
        if (abstractGolem instanceof LandGolem) {
            tcr_bosses$ceBossEvent.setDisplayType("land_golem");
        }
        if (abstractGolem instanceof OceanGolem) {
            tcr_bosses$ceBossEvent.setDisplayType("ocean_golem");
        }
        if (abstractGolem instanceof NetherGolem) {
            tcr_bosses$ceBossEvent.setDisplayType("nether_golem");
        }
        if (abstractGolem instanceof CoreGolem) {
            tcr_bosses$ceBossEvent.setDisplayType("core_golem");
        }
        if (abstractGolem instanceof SkyGolem) {
            tcr_bosses$ceBossEvent.setDisplayType("sky_golem");
        }
        if (abstractGolem instanceof EndGolem) {
            tcr_bosses$ceBossEvent.setDisplayType("end_golem");
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tcr$tick(CallbackInfo ci) {
        AbstractGolem abstractGolem = (AbstractGolem) (Object) this;
        tcr_bosses$ceBossEvent.setName(bossBar.getName());
        tcr_bosses$ceBossEvent.setProgress(bossBar.getProgress());
        tcr_Bosses$syncStamina(abstractGolem);
    }

    @Unique
    private void tcr_Bosses$syncStamina(AbstractGolem golem) {
        LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(golem, LivingEntityPatch.class);
        if (entityPatch != null) {
            tcr_bosses$ceBossEvent.setStaminaStatus(CEPatchUtils.getStaminaStatus(entityPatch));
            tcr_bosses$ceBossEvent.setStamina(CEPatchUtils.getStaminaPercent(entityPatch));
        }
    }

    @Redirect(method = "startSeenByPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;addPlayer(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void tcr$onAddedPlayer(ServerBossEvent serverBossEvent, ServerPlayer serverPlayer) {
        tcr_bosses$ceBossEvent.addPlayer(serverPlayer);
    }

    @Redirect(method = "stopSeenByPlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;removePlayer(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void tcr$onRemovePlayer(ServerBossEvent serverBossEvent, ServerPlayer serverPlayer) {
        tcr_bosses$ceBossEvent.removePlayer(serverPlayer);
    }

    @Redirect(method = "setGolemState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;isVisible()Z")
    )
    private boolean tcr$onGetState1(ServerBossEvent instance) {
        return tcr_bosses$ceBossEvent.isVisible();
    }

    @Redirect(method = "setGolemState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;setVisible(Z)V")
    )
    private void tcr$onGetState2(ServerBossEvent instance, boolean b) {
        tcr_bosses$ceBossEvent.setVisible(b);
    }

    /**
     * 允许刚出生时破坏，禁止非高塔生的傀儡破坏
     */
    @Inject(method = "destroyBlocksNearby",at = @At(value = "HEAD"),cancellable = true,remap = false)
    public void destroyBlocksNearby(CallbackInfo ci){
        if(this.golemType.equals(GolemType.SKY) || this.golemType.equals(GolemType.NETHER) || this.golemType.equals(GolemType.END)) {
            ci.cancel();
        }
        if(this.tickCount > 200) {
            ci.cancel();
        }
    }

    @Inject(method = "getTargetingRange", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void tcr$getTargetingRange(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(((AbstractGolem) (Object) this).getAllowedTowerRange() * 2);
    }

    @Inject(method = "resetGolem", at = @At(value = "HEAD"), remap = false)
    public void tcr$resetGolem(CallbackInfo ci) {
        AbstractGolem btAbstractGolem = (AbstractGolem) (Object) this;
        if (!btAbstractGolem.isDormant() && btAbstractGolem instanceof EndGolem endGolem) {
            endGolem.setHealth(endGolem.getMaxHealth());
            EndGolemPatch endGolemPatch = EpicFightCapabilities.getEntityPatch(endGolem, EndGolemPatch.class);
            if (endGolemPatch != null) {
                endGolemPatch.setBossBreakPhase(0);
                CEPatchUtils.setStamina(endGolemPatch, Float.MAX_VALUE);
            }
        }
        this.tickCount = 0;//重置允许破坏的时间
    }

    @Inject(method = "setTarget", at = @At("HEAD"))
    public void tcr$setTarget(LivingEntity entityIn, CallbackInfo ci) {
        EpicFightCapabilities.getUnparameterizedEntityPatch(this, SkyGolemPatch.class).ifPresent(skyGolemPatch -> {
            if (entityIn != null && this.getTarget() == null) {
                skyGolemPatch.handleNearByBlock(20, false);
            }
            if (entityIn == null && this.getTarget() != null) {
                skyGolemPatch.handleNearByBlock(20, true);
            }
        });
    }

}
