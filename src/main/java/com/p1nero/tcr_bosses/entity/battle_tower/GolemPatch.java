package com.p1nero.tcr_bosses.entity.battle_tower;

import com.mojang.datafixers.util.Pair;
import com.p1nero.tcr_bosses.entity.goal.MobHateTargetGoal;
import net.minecraft.world.entity.Mob;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import yesman.epicfight.world.capabilities.entitypatch.Factions;

import java.util.*;

public abstract class GolemPatch<T extends Mob> extends CEHumanoidPatch<T> {

    private final Map<UUID, List<Pair<Float, Long>>> mobHateMap = new HashMap<>();

    public GolemPatch(Factions factions) {
        super(factions);
    }

    @Override

    public void setAIAsInfantry() {
        super.setAIAsInfantry();
        original.targetSelector.addGoal(1, new MobHateTargetGoal(this, original, true));
    }

    public boolean hasEntityData(UUID uuid){
        return this.mobHateMap.containsKey(uuid);
    }

    public void updateHateData(){
        long gameTime = original.level().getGameTime();
        for (Map.Entry<UUID, List<Pair<Float, Long>>> entry : mobHateMap.entrySet()) {
            entry.getValue().removeIf(pair -> gameTime - pair.getSecond() > 1200);
            if(entry.getValue().isEmpty()){
                this.mobHateMap.remove(entry.getKey());
            }
        }
    }

    public void addHateData(UUID uuid, float damage){
        long gameTime = original.level().getGameTime();
        mobHateMap.computeIfAbsent(uuid, k-> new ArrayList<>())
                .add(Pair.of(damage, gameTime));

        for (Pair<Float, Long> next : mobHateMap.get(uuid)) {
            if (gameTime - next.getSecond() > 1200) {
                mobHateMap.remove(uuid);
            }
        }
    }

    public double getTotalHate(UUID uuid) {
        List<Pair<Float, Long>> hateList = mobHateMap.get(uuid);
        if (hateList == null || hateList.isEmpty()) {
            return 0.0;
        }

        long currentTime = original.level().getGameTime();
        return hateList.stream()
                .filter(pair -> currentTime - pair.getSecond() <= 1200)
                .mapToDouble(Pair::getFirst)
                .sum();
    }
}
