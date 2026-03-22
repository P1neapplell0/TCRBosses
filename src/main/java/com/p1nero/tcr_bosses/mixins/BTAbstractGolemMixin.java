package com.p1nero.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.*;
import com.brass_amber.ba_bt.util.BTUtil;
import com.brass_amber.ba_bt.util.GolemType;
import com.p1nero.tcr_bosses.client.sound.BossMusicPlayer;
import com.p1nero.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.bossbar.CEBossEvent;
import com.p1nero.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import org.jetbrains.annotations.Nullable;
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

import java.util.Objects;

@Mixin(value = AbstractGolem.class)
public abstract class BTAbstractGolemMixin extends PathfinderMob {

    @Shadow(remap = false)
    @Final
    private ServerBossEvent bossBar;

    @Shadow(remap = false)
    public GolemType golemType;

    @Shadow(remap = false)
    protected abstract void addBehaviorGoals();

    @Shadow(remap = false)
    public Music BOSS_MUSIC;

    @Shadow(remap = false)
    public abstract boolean isAwake();

    @Shadow(remap = false)
    public abstract int getAllowedTowerRange();

    @Shadow(remap = false)
    protected abstract BlockPos getSpawnPos();

    @Shadow(remap = false)
    protected abstract void resetGolem();

    @Shadow(remap = false)
    public abstract int getTargetingRange();

    @Shadow(remap = false)
    public abstract boolean isDormant();

    @Shadow(remap = false)
    public abstract Float getSpawnDirection();

    @Shadow(remap = false)
    public abstract byte getGolemState();

    @Shadow(remap = false)
    protected int explosionPower;

    @Shadow
    public abstract void setTarget(@Nullable LivingEntity livingEntity);

    @Shadow(remap = false)
    public abstract void setGolemState(byte golemStateID);

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

        if(!level().isClientSide()) {
            if (getTarget() != null && !getTarget().isAlive()) {
                setTarget(null);
            }
        }
    }

    /**
     * 换自己的bgm管理
     */
    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr$clientTick(CallbackInfo ci) {
        ci.cancel();
        if(this.isAwake()) {
            BossMusicPlayer.playBossMusic(this, this.BOSS_MUSIC.getEvent().get(), 32);
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void tr$die(DamageSource source, CallbackInfo ci) {
        if(level().isClientSide) {
            BossMusicPlayer.stopBossMusic(this);
        }
    }

    /**
     * 尝试修多人下的bug
     */
    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    private void tcr$registerGoals(CallbackInfo ci) {
        ci.cancel();
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.addBehaviorGoals();
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

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"), cancellable = true)
    private void tcr$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        ci.cancel();
        super.addAdditionalSaveData(compound);
        compound.put("SpawnPos", NbtUtils.writeBlockPos(this.getSpawnPos()));
        compound.putFloat("SpawnDirection", this.getSpawnDirection());
        compound.putByte("GolemState", this.getGolemState());
        compound.putInt("ExplosionPower", this.explosionPower);
    }

    /**
     * 允许刚出生时破坏，禁止非高塔生的傀儡破坏
     */
    @Inject(method = "destroyBlocksNearby",at = @At(value = "HEAD"),cancellable = true,remap = false)
    public void destroyBlocksNearby(CallbackInfo ci){
//        if(this.golemType.equals(GolemType.SKY) || this.golemType.equals(GolemType.NETHER) || this.golemType.equals(GolemType.END)) {
//            ci.cancel();
//        }
//        if(this.tickCount > 200) {
//            ci.cancel();
//        }
        ci.cancel();
    }

    @Inject(method = "getTargetingRange", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void tcr$getTargetingRange(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(((AbstractGolem) (Object) this).getAllowedTowerRange() * 2);
    }

    @Inject(method = "resetGolem", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void tcr$resetGolem(CallbackInfo ci) {
        ci.cancel();
        AbstractGolem btAbstractGolem = (AbstractGolem) (Object) this;
        if (!btAbstractGolem.isDormant() && btAbstractGolem instanceof EndGolem endGolem) {
//            endGolem.setHealth(endGolem.getMaxHealth());
            EndGolemPatch endGolemPatch = EpicFightCapabilities.getEntityPatch(endGolem, EndGolemPatch.class);
            if (endGolemPatch != null) {
                endGolemPatch.setBossBreakPhase(0);
                CEPatchUtils.setStamina(endGolemPatch, Float.MAX_VALUE);
            }
        }
//        this.tickCount = 0;//重置允许破坏的时间

        if (!this.isDormant()) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            BlockPos spawnPos = this.getSpawnPos();
            if (BTUtil.distanceTo2D(this, spawnPos.getX(), spawnPos.getZ()) > (double)4.0F) {
                this.teleportTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            }

            this.getNavigation().stop();
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, this.level());
            lightning.setPos(x, y, z);
            lightning.setDamage(0.0F);
            this.level().addFreshEntity(lightning);
        }
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

    @Inject(method = "resetGolemTick", at = @At("HEAD"), cancellable = true, remap = false)
    public void tcr$getAllowedTowerRange(CallbackInfo ci) {
        ci.cancel();
        int maxDistanceFromSpawn = this.getAllowedTowerRange();

        if(((Object)this instanceof CoreGolem)) {
            maxDistanceFromSpawn = 13;
        }

        if(((Object)this instanceof LandGolem)) {
            maxDistanceFromSpawn = 13;
        }

        maxDistanceFromSpawn *= maxDistanceFromSpawn;
        BlockPos spawnPos = this.getSpawnPos();
        boolean shouldReset = false;
        if (BTUtil.sqrDistanceTo2D(this, spawnPos.getX(), spawnPos.getZ()) > (double)maxDistanceFromSpawn) {
            shouldReset = true;
        } else {
            boolean hasPlayer = this.level().hasNearbyAlivePlayer(this.getX(), this.getY(), this.getZ(), this.getTargetingRange());
            if (!hasPlayer) {
                shouldReset = true;
                setTarget(null);
                setGolemState((byte) 0);
            }
        }
        if((Object)this instanceof CoreGolem) {
            if(this.getY() < spawnPos.getY() - 10) {
                shouldReset = true;
            }
        }
        if((Object)this instanceof LandGolem) {
            if(this.getY() < spawnPos.getY() - 10) {
                shouldReset = true;
            }
        }
        if((Object)this instanceof SkyGolem) {
            if(this.getY() < spawnPos.getY() - 8) {
                shouldReset = true;
            }
        }
        if(shouldReset) {
            this.resetGolem();
        }
    }

}
