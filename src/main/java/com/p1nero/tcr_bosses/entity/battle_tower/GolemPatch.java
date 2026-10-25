package com.p1nero.tcr_bosses.entity.battle_tower;

import com.p1nero.tcr_bosses.entity.goal.MobHateTargetGoal;
import com.p1nero.tcr_bosses.entity.goal.MobHateTracker;
import net.minecraft.world.entity.Mob;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import yesman.epicfight.world.capabilities.entitypatch.Factions;

import java.util.UUID;

public abstract class GolemPatch<T extends Mob> extends CEHumanoidPatch<T> {

    private final MobHateTracker hateTracker = new MobHateTracker(() -> original.level().getGameTime());

    public GolemPatch(Factions factions) {
        super(factions);
    }

    @Override

    public void setAIAsInfantry() {
        super.setAIAsInfantry();
        original.targetSelector.removeAllGoals(goal -> goal instanceof MobHateTargetGoal);
        original.targetSelector.addGoal(0, new MobHateTargetGoal(this, original, true));
    }

    public void addHateData(UUID uuid, float damage){
        this.hateTracker.addHateData(uuid, damage);
    }

    public double getTotalHate(UUID uuid) {
        return this.hateTracker.getTotalHate(uuid);
    }
}
