package com.p1nero.tcr_bosses.entity.cataclysm.stamina;

import com.p1nero.tcr_bosses.config.CataclysmStaminaConfig;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.ai.iml.CustomExecuteEntity;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.bossbar.BossData;
import net.shelmarow.combat_evolution.damage_source.CEDamageTypeTags;
import net.shelmarow.combat_evolution.execution.ExecutionHandler;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager;
import net.shelmarow.combat_evolution.network.CENetworkHandler;
import net.shelmarow.combat_evolution.network.server.S2CRemoveBossDataPacket;
import net.shelmarow.combat_evolution.network.server.S2CUpdateBossDataPacket;
import net.shelmarow.combat_evolution.network.server.S2CUpdateStaminaDataPacket;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Objects;

public class CataclysmStaminaPatch extends MobPatch<Mob> implements CustomExecuteEntity {
    public static final String DISPLAY_TYPE = "tcr_cataclysm_stamina";
    private static final String REACTION_TAG = "tcr_bosses_stamina_reaction";
    private final CataclysmBossType bossType;
    private int lastActionTick;
    private int stateTicks;
    private int executionTicks;
    private boolean executionActive;
    private Object previousAction;
    private float lastSyncedStamina = -1;
    private StaminaStatus lastSyncedStatus;

    public CataclysmStaminaPatch(CataclysmBossType bossType) {
        super(Factions.UNDEAD);
        this.bossType = bossType;
    }

    public static CataclysmStaminaPatch get(Mob entity) {
        return EpicFightCapabilities.getEntityPatch(entity, CataclysmStaminaPatch.class);
    }

    public static boolean isLocked(Mob entity) {
        CataclysmStaminaPatch patch = get(entity);
        return patch != null && entity.isAlive() && patch.isBroken();
    }

    @Override
    protected void initAI() {
    }

    @Override
    public boolean overrideRender() {
        return false;
    }

    @Override
    public void onJoinWorld(Mob entity, EntityJoinLevelEvent event) {
        super.onJoinWorld(entity, event);
        if (!entity.level().isClientSide()) {
            CataclysmStaminaConfig.Boss config = bossType.config();
            setAttribute(EpicFightAttributes.MAX_STAMINA.get(), config.maxStamina.get());
            setAttribute(EpicFightAttributes.STAMINA_REGEN.get(), config.staminaRegen.get());
            setAttribute(EpicFightAttributes.IMPACT.get(), config.impact.get());
            setAttribute(EpicFightAttributes.ARMOR_NEGATION.get(), config.armorNegation.get());
            setAttribute(EpicFightAttributes.STUN_ARMOR.get(), config.stunArmor.get());
            setAttribute(EpicFightAttributes.WEIGHT.get(), config.weight.get());
            setAttribute(EpicFightAttributes.MAX_STRIKES.get(), config.maxStrikes.get());
            setAttribute(EpicFightAttributes.EXECUTION_RESISTANCE.get(), config.executionResistance.get());
            setExecutionResistance(config.executionResistance.get());
            CEPatchUtils.setStaminaStatus(this, StaminaStatus.COMMON);
            CEPatchUtils.setStamina(this, CEPatchUtils.getMaxStamina(this));
            if (entity.isAlive() && entity.getPersistentData().getBoolean(REACTION_TAG)) {
                bossType.clearReaction(entity);
                entity.getPersistentData().remove(REACTION_TAG);
            }
            lastActionTick = entity.tickCount;
            previousAction = bossType.action(entity);
        }
    }

    private void setAttribute(Attribute attribute, double value) {
        Objects.requireNonNull(original.getAttribute(attribute)).setBaseValue(value);
    }

    @Override
    protected void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
    }

    @Override
    public void updateMotion(boolean considerInaction) {
        currentLivingMotion = original.isAlive() ? LivingMotions.IDLE : LivingMotions.DEATH;
        currentCompositeMotion = currentLivingMotion;
    }

    @Override
    public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
        return stunType == StunType.NEUTRALIZE ? Animations.BIPED_COMMON_NEUTRALIZED : null;
    }

    @Override
    public boolean applyStun(StunType stunType, float stunTime) {
        return false;
    }

    public boolean isBroken() {
        return CEPatchUtils.getStaminaStatus(this) == StaminaStatus.BREAK;
    }

    public void freeze() {
        original.setDeltaMovement(Vec3.ZERO);
        original.xxa = 0;
        original.yya = 0;
        original.zza = 0;
        cancelKnockback = true;
        if (!original.level().isClientSide()) {
            original.getNavigation().stop();
        }
    }

    @Override
    protected void serverTick(LivingEvent.LivingTickEvent event) {
        super.serverTick(event);
        if (!original.isAlive()) {
            return;
        }
        bossEvent().setProgress(original.getHealth() / original.getMaxHealth());
        CataclysmStaminaConfig.Boss config = bossType.config();
        StaminaStatus status = CEPatchUtils.getStaminaStatus(this);
        float maxStamina = CEPatchUtils.getMaxStamina(this);
        if (status == StaminaStatus.COMMON) {
            Object action = bossType.action(original);
            if (!original.isNoAi() && bossType.isActing(original)) {
                lastActionTick = original.tickCount;
                if (!Objects.equals(previousAction, action)) {
                    damageStamina(config.attackCost.get().floatValue());
                }
            } else if (original.tickCount - lastActionTick >= config.regenDelay.get()) {
                float regen = (float) original.getAttributeValue(EpicFightAttributes.STAMINA_REGEN.get());
                CEPatchUtils.addStamina(this, maxStamina * 0.01F * regen);
            }
            previousAction = action;
        } else if (status == StaminaStatus.BREAK) {
            freeze();
            if (executionActive) {
                executionTicks = Math.max(0, executionTicks - 1);
                if (executionTicks == 0 && !ExecutionHandler.isExecutingTarget(original, original)) {
                    enterRecovery();
                }
            } else if (++stateTicks >= config.breakTicks.get()) {
                enterRecovery();
            }
            if (isBroken() && !bossType.isActing(original)) {
                bossType.playReaction(original, executionActive);
            }
        } else {
            float progress = Math.min(1.0F, (float) ++stateTicks / config.recoverTicks.get());
            CEPatchUtils.setStamina(this, maxStamina * progress);
            if (progress >= 1.0F) {
                CEPatchUtils.setStaminaStatus(this, StaminaStatus.COMMON);
                lastActionTick = original.tickCount;
                previousAction = bossType.action(original);
            }
        }
        syncBossData();
    }

    private void enterRecovery() {
        bossType.clearReaction(original);
        original.getPersistentData().remove(REACTION_TAG);
        CEPatchUtils.setStaminaStatus(this, StaminaStatus.RECOVER);
        cancelKnockback = false;
        stateTicks = 0;
        executionActive = false;
        previousAction = bossType.action(original);
    }

    public void onHurt(DamageSource source) {
        if (source.is(CEDamageTypeTags.EXECUTION)) {
            return;
        }
        CataclysmStaminaConfig.Boss config = bossType.config();
        float impact = source instanceof EpicFightDamageSource epicSource
                ? epicSource.calculateImpact() : config.vanillaHitCost.get().floatValue();
        damageStamina(impact * config.hurtImpactMultiplier.get().floatValue());
    }

    public void damageStamina(float amount) {
        if (original.level().isClientSide() || !Float.isFinite(amount) || amount <= 0 || !original.isAlive()
                || CEPatchUtils.getStaminaStatus(this) != StaminaStatus.COMMON) {
            return;
        }
        lastActionTick = original.tickCount;
        CEPatchUtils.addStamina(this, -amount);
        if (CEPatchUtils.getStamina(this) <= 0) {
            CEPatchUtils.setStaminaStatus(this, StaminaStatus.BREAK);
            original.getPersistentData().putBoolean(REACTION_TAG, true);
            stateTicks = 0;
            original.goalSelector.getRunningGoals().toList().forEach(goal -> goal.stop());
            original.ejectPassengers();
            freeze();
            bossType.playReaction(original, false);
            playSound(yesman.epicfight.gameasset.EpicFightSounds.NEUTRALIZE_MOBS.get(), 1, 1);
        }
        syncBossData();
    }

    @Override
    public void onAttackBlocked(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        damageStamina(bossType.config().blockedAttackCost.get().floatValue());
    }

    public void beginExecution(int duration) {
        executionTicks = duration + 1;
        executionActive = true;
        stateTicks = 0;
        bossType.playReaction(original, true);
        freeze();
    }

    @Override
    public boolean canBeExecuted(LivingEntityPatch<?> executor) {
        return original.isAlive() && isBroken() && !executionActive && bossType.config().executionEnabled.get();
    }

    @Override
    public boolean canUseCustomType(LivingEntityPatch<?> executor, ExecutionTypeManager.Type type) {
        return true;
    }

    @Override
    public ExecutionTypeManager.Type getExecutionType(LivingEntityPatch<?> executor, ExecutionTypeManager.Type type) {
        double offset = Math.max(type.offset().x, original.getBbWidth() * 0.5 + 0.8);
        return new ExecutionTypeManager.Type(type.executionAnimation(), type.executedAnimation(),
                new Vec3(offset, type.offset().y, type.offset().z), type.rotationOffset(), type.totalTick());
    }

    @Override
    public void onStartTracking(ServerPlayer trackingPlayer) {
        sendBossData(trackingPlayer);
    }

    @Override
    public void onStopTracking(ServerPlayer trackingPlayer) {
        CENetworkHandler.sendToPlayer(trackingPlayer, new S2CRemoveBossDataPacket(bossEvent().getId()));
    }

    public ServerBossEvent bossEvent() {
        return ((CataclysmBossAccess) original).tcr_bosses$getHealthBossEvent();
    }

    private void syncBossData() {
        float stamina = CEPatchUtils.getStaminaPercent(this);
        StaminaStatus status = CEPatchUtils.getStaminaStatus(this);
        if (stamina != lastSyncedStamina || status != lastSyncedStatus) {
            for (ServerPlayer player : bossEvent().getPlayers()) {
                CENetworkHandler.sendToPlayer(player, new S2CUpdateStaminaDataPacket(bossEvent().getId(), stamina, status));
            }
            lastSyncedStamina = stamina;
            lastSyncedStatus = status;
        }
    }

    private void sendBossData(ServerPlayer player) {
        BossData data = new BossData();
        data.displayType = DISPLAY_TYPE;
        data.stamina = CEPatchUtils.getStaminaPercent(this);
        data.staminaO = data.stamina;
        data.staminaStatus = CEPatchUtils.getStaminaStatus(this);
        CENetworkHandler.sendToPlayer(player, new S2CUpdateBossDataPacket(bossEvent().getId(), data));
    }
}
