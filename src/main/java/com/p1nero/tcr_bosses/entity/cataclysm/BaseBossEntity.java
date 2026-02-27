package com.p1nero.tcr_bosses.entity.cataclysm;

import com.p1nero.tcr_bosses.client.sound.BossMusicPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBossEntity extends PathfinderMob {

    private static final EntityDataAccessor<Boolean> HAS_TARGET = SynchedEntityData.defineId(BaseBossEntity.class, EntityDataSerializers.BOOLEAN);

    protected BaseBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HAS_TARGET, false);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHasTargetSync(tag.getBoolean("has_target"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("has_target", this.hasTarget());
    }

    public void setHasTargetSync(boolean hasTarget) {
        this.entityData.set(HAS_TARGET, hasTarget);
    }

    public boolean hasTarget() {
        return this.entityData.get(HAS_TARGET);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public abstract SoundEvent getBGM();

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide && this.hasTarget()) {
            BossMusicPlayer.playBossMusic(this, getBGM(), 32);
        } else {
            this.setHasTargetSync(this.getTarget() != null);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float pAmount) {
        if(source.is(DamageTypeTags.IS_FIRE)) {
            this.clearFire();
            return false;
        }
        if(source.isIndirect() && source.getEntity() != null && source.getEntity().distanceTo(this) > 5) {
            return false;
        }
        return super.hurt(source, pAmount);
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        if(level().isClientSide) {
            BossMusicPlayer.stopBossMusic(this);
        }
    }

    @Override
    protected float getEquipmentDropChance(@NotNull EquipmentSlot pSlot) {
        return 0;
    }
}
