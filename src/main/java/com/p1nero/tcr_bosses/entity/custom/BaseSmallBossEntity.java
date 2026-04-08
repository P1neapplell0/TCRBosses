package com.p1nero.tcr_bosses.entity.custom;

import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.client.sound.BossMusicPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class BaseSmallBossEntity extends PathfinderMob {

    protected static final EntityDataAccessor<BlockPos> SPAWN_POS = SynchedEntityData.defineId(BaseSmallBossEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> HAS_TARGET = SynchedEntityData.defineId(BaseSmallBossEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IN_FIGHTING = SynchedEntityData.defineId(BaseSmallBossEntity.class, EntityDataSerializers.BOOLEAN);

    private int fightDelay = 0;

    protected BaseSmallBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if(!TCRBossesMod.isTcrLoaded()) {
            serverPlayer.displayClientMessage(Component.translatable("info.tcrbosses.not_tcr_env"), false);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HAS_TARGET, false);
        this.getEntityData().define(IN_FIGHTING, true);
        this.getEntityData().define(SPAWN_POS, BlockPos.ZERO);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHasTargetSync(tag.getBoolean("has_target"));
        this.setInFighting(tag.getBoolean("in_fighting"));
        this.setSpawnPos(NbtUtils.readBlockPos(tag.getCompound("SpawnPos")));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("has_target", this.hasTarget());
        tag.putBoolean("in_fighting", this.isInFighting());
        tag.put("SpawnPos", NbtUtils.writeBlockPos(this.getSpawnPos()));
    }

    public void setFightDelay(int fightDelay) {
        this.fightDelay = fightDelay;
    }

    public boolean isInFighting() {
        return this.getEntityData().get(IN_FIGHTING);
    }

    public void setInFighting(boolean inFighting) {
        this.getEntityData().set(IN_FIGHTING, inFighting);
    }

    public boolean hasSpawnPos() {
        return !BlockPos.ZERO.equals(this.getSpawnPos());
    }

    public void setSpawnPos(BlockPos spawnPos) {
        this.entityData.set(SPAWN_POS, spawnPos);
    }

    public BlockPos getSpawnPos() {
        return this.entityData.get(SPAWN_POS);
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
        this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, this::shouldAttack));
    }

    public boolean shouldAttack(LivingEntity target) {
        return this.isInFighting();
    }

    public abstract SoundEvent getBGM();

    @Override
    public void tick() {
        if(!this.isInFighting()) {
            this.setTarget(null);
        }
        if(!level().isClientSide && this.fightDelay > 0) {
            this.setTarget(null);
            fightDelay--;
            Vec3 center = this.position();
            if(fightDelay % 20 == 0) {
                this.level().getEntitiesOfClass(ServerPlayer.class, (new AABB(center, center)).inflate(32)).forEach(player -> {
                    player.displayClientMessage(Component.literal(String.valueOf(fightDelay / 20)), true);
                    player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ANVIL_FALL), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 1.0F, player.getRandom().nextInt()));
                });
            }
        }
        super.tick();
        if(level().isClientSide && this.hasTarget()) {
            BossMusicPlayer.playBossMusic(this, getBGM(), 32);
        } else {
            this.setHasTargetSync(this.getTarget() != null);
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float pAmount) {
        if(!this.isInFighting()) {
            return false;
        }
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
    public boolean fireImmune() {
        return true;
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

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }
}
