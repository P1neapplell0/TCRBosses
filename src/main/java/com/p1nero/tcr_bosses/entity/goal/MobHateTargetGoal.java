package com.p1nero.tcr_bosses.entity.goal;

import com.p1nero.tcr_bosses.entity.battle_tower.GolemPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class MobHateTargetGoal extends TargetGoal {
    private final GolemPatch<?> mobPatch;
    @Nullable protected LivingEntity target;
    private int unseenTicks;

    public MobHateTargetGoal(GolemPatch<?> mobPatch, Mob pMob, boolean pMustSee) {
        super(pMob, pMustSee);
        this.mobPatch = mobPatch;
    }

    @Override
    public boolean canUse() {
        this.findTarget();
        return this.target != null;
    }

    @Override
    public boolean canContinueToUse() {
        findTarget();
        LivingEntity livingentity = this.target;
        if (livingentity == null) {
            livingentity = this.targetMob;
        }

        if (livingentity == null) {
            return false;
        } else if (!this.mob.canAttack(livingentity)) {
            return false;
        } else {
            Team team = this.mob.getTeam();
            Team team1 = livingentity.getTeam();
            if (team != null && team1 == team) {
                return false;
            } else {
                double d0 = this.getFollowDistance();
                if (this.mob.distanceToSqr(livingentity) > d0 * d0) {
                    return false;
                } else {
                    if (this.mustSee) {
                        if (this.mob.getSensing().hasLineOfSight(livingentity)) {
                            this.unseenTicks = 0;
                        } else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
                            return false;
                        }
                    }


                    this.mob.setTarget(livingentity);
                    return true;
                }
            }
        }
    }

    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
    }

    protected void findTarget() {
        List<LivingEntity> nearbyEntities =
                this.mob.level().getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(getFollowDistance()));
        LivingEntity livingEntity = nearbyEntities.stream()
                .filter(entity -> mobPatch.hasEntityData(entity.getUUID()))
                .max(Comparator.comparingDouble(entity -> mobPatch.getTotalHate(entity.getUUID())))
                .orElse(null);
        if(livingEntity != this.target) {
            this.target = livingEntity;
        }
    }


    public void start() {
        this.mob.setTarget(this.target);
        super.start();
        this.unseenTicks = 0;
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }
}
