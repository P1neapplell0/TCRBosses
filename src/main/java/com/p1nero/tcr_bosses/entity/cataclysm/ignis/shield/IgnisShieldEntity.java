package com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import com.p1nero.tcr_bosses.entity.TCRBossEntities;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.IgnisHumanoid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class IgnisShieldEntity extends PathfinderMob implements GeoEntity {

    private IgnisHumanoid owner;

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation SUMMON = RawAnimation.begin().thenPlay("summon");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> HAS_SOUL = SynchedEntityData.defineId(IgnisShieldEntity.class, EntityDataSerializers.BOOLEAN);

    public IgnisShieldEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    /**
     * 防止被锁定
     */
    @Override
    public boolean isPickable() {
        return false;
    }

    public IgnisShieldEntity(IgnisHumanoid owner) {
        this(TCRBossEntities.IGNIS_SHIELD.get(), owner.level());
        this.owner = owner;
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(EpicFightAttributes.IMPACT.get(),1.0D)
                .add(EpicFightAttributes.MAX_STAMINA.get(),15.0D)
                .add(EpicFightAttributes.STAMINA_REGEN.get(),1.0D)
                .build();
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(HAS_SOUL, false);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHasSoul(tag.getBoolean("has_soul"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("has_soul", this.hasSoul());
    }

    public void setHasSoul(boolean hasSoul) {
        this.entityData.set(HAS_SOUL, hasSoul);
    }

    public boolean hasSoul() {
        return this.entityData.get(HAS_SOUL);
    }

    @Nullable
    public IgnisHumanoid getOwner() {
        return owner;
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if(pSource.is(DamageTypeTags.IS_FIRE)) {
            this.clearFire();
            return false;
        }
        if(pSource.getEntity() instanceof IgnisHumanoid) {
            return false;
        }
        return super.hurt(pSource, 0);
    }

    @Override
    public void setHealth(float pHealth) {

    }

    @Override
    public void kill() {

    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide) {
            clearFire();
            if(owner != null) {
                this.setHasSoul(owner.hasSoulEffect());
            }
            if(owner == null || owner.isDeadOrDying() || !owner.hasEffectOfLevel(MobEffects.DAMAGE_RESISTANCE, 4)) {
                this.discard();
            }
        }
    }

    public void playSummonAnimation() {
        this.triggerAnim("Summon", "summon");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::deployAnimController));
        controllers.add(new AnimationController<>(this, "Summon", 0, state -> PlayState.CONTINUE)
                .triggerableAnim("summon", SUMMON));
    }

    protected <E extends IgnisShieldEntity> PlayState deployAnimController(final AnimationState<E> state) {
        return state.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
