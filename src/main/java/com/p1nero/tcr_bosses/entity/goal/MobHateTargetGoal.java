package com.p1nero.tcr_bosses.entity.goal;

import com.p1nero.tcr_bosses.entity.battle_tower.GolemPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public class MobHateTargetGoal extends TargetGoal {
    private static final int SEARCH_INTERVAL_TICKS = 10;
    private final ToDoubleFunction<UUID> hateProvider;
    private final Predicate<LivingEntity> targetPredicate;
    private final TargetingConditions targetConditions = TargetingConditions.forCombat().ignoreLineOfSight();
    @Nullable protected LivingEntity target;
    private int unseenTicks;
    private long nextSearchTick;

    public MobHateTargetGoal(GolemPatch<?> mobPatch, Mob mob, boolean mustSee) {
        this(mob, mustSee, mobPatch::getTotalHate, entity -> true);
    }

    public MobHateTargetGoal(Mob mob, boolean mustSee, ToDoubleFunction<UUID> hateProvider,
                             Predicate<LivingEntity> targetPredicate) {
        super(mob, mustSee);
        this.hateProvider = hateProvider;
        this.targetPredicate = targetPredicate;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive() || this.mob.level().getGameTime() < this.nextSearchTick) {
            return false;
        }
        this.findTarget();
        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.mob.isAlive()) {
            return false;
        }
        if (!this.isValidTarget(this.target)) {
            this.target = null;
            this.findTarget();
        } else {
            if (this.mustSee) {
                if (this.mob.getSensing().hasLineOfSight(this.target)) {
                    this.unseenTicks = 0;
                } else if (++this.unseenTicks > this.unseenMemoryTicks) {
                    this.target = null;
                    this.findTarget();
                }
            }
            if (this.mob.level().getGameTime() >= this.nextSearchTick) {
                this.findTarget();
            }
        }
        if (this.target == null) {
            return false;
        }
        this.mob.setTarget(this.target);
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected AABB getTargetSearchArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance);
    }

    private boolean isValidTarget(@Nullable LivingEntity candidate) {
        return candidate != null && candidate != this.mob && candidate.isAlive() && !candidate.isRemoved()
                && candidate.level() == this.mob.level() && !candidate.isSpectator()
                && (!(candidate instanceof Player player) || !player.isCreative())
                && this.targetPredicate.test(candidate)
                && this.canAttack(candidate, this.targetConditions.range(this.getFollowDistance()));
    }

    protected void findTarget() {
        this.nextSearchTick = this.mob.level().getGameTime() + SEARCH_INTERVAL_TICKS;
        LivingEntity bestTarget = this.isValidTarget(this.target) ? this.target : null;
        double bestHate = bestTarget == null ? 0.0 : this.hateProvider.applyAsDouble(bestTarget.getUUID());
        if (!(bestTarget instanceof Player) && bestHate <= 0.0) {
            bestTarget = null;
        }
        double bestDistance = bestTarget == null ? Double.MAX_VALUE : this.mob.distanceToSqr(bestTarget);
        for (LivingEntity candidate : this.mob.level().getEntitiesOfClass(LivingEntity.class,
                this.getTargetSearchArea(this.getFollowDistance()), this::isValidTarget)) {
            if (this.mustSee && candidate != this.target && !this.mob.getSensing().hasLineOfSight(candidate)) {
                continue;
            }
            double hate = this.hateProvider.applyAsDouble(candidate.getUUID());
            if (hate <= 0.0 && !(candidate instanceof Player)) {
                continue;
            }
            double distance = this.mob.distanceToSqr(candidate);
            if (bestTarget == null || hate > bestHate
                    || (hate == bestHate && hate <= 0.0 && distance < bestDistance)) {
                bestTarget = candidate;
                bestHate = hate;
                bestDistance = distance;
            }
        }
        this.setTarget(bestTarget);
    }

    @Override
    public void start() {
        if (!this.isValidTarget(this.target)) {
            this.target = null;
        }
        this.mob.setTarget(this.target);
        this.targetMob = this.target;
        super.start();
        this.unseenTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.target = null;
        this.unseenTicks = 0;
    }

    public void setTarget(@Nullable LivingEntity target) {
        if (this.target != target) {
            this.unseenTicks = 0;
        }
        this.target = target;
        this.targetMob = target;
    }
}
