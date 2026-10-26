package com.p1nero.tcr_bosses.entity.cataclysm.stamina;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ender_Guardian_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.LLibrary_Monster;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Harbinger_Entity;
import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.The_Leviathan_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Internal_Animation_Monster;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.lionfishapi.server.animation.Animation;
import com.github.L_Ender.lionfishapi.server.animation.AnimationHandler;
import com.github.L_Ender.lionfishapi.server.animation.IAnimatedEntity;
import com.p1nero.tcr_bosses.config.CataclysmStaminaConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.function.Supplier;

public enum CataclysmBossType {
    ENDER_GUARDIAN("ender_guardian", ModEntities.ENDER_GUARDIAN, 0, 0),
    NETHERITE_MONSTROSITY("netherite_monstrosity", ModEntities.NETHERITE_MONSTROSITY, 5, 5),
    IGNIS("ignis", ModEntities.IGNIS, 0, 0),
    HARBINGER("the_harbinger", ModEntities.THE_HARBINGER, 0, 0),
    LEVIATHAN("the_leviathan", ModEntities.THE_LEVIATHAN, 0, 0),
    ANCIENT_REMNANT("ancient_remnant", ModEntities.ANCIENT_REMNANT, 12, 12),
    MALEDICTUS("maledictus", ModEntities.MALEDICTUS, 5, 6),
    SCYLLA("scylla", ModEntities.SCYLLA, 19, 12);

    private final String id;
    private final Supplier<? extends EntityType<? extends Mob>> entityType;
    private final int breakState;
    private final int executedState;

    CataclysmBossType(String id, Supplier<? extends EntityType<? extends Mob>> entityType, int breakState, int executedState) {
        this.id = id;
        this.entityType = entityType;
        this.breakState = breakState;
        this.executedState = executedState;
    }

    public String id() {
        return id;
    }

    public EntityType<? extends Mob> entityType() {
        return entityType.get();
    }

    public CataclysmStaminaConfig.Boss config() {
        return CataclysmStaminaConfig.BOSSES.get(id);
    }

    public Object action(Mob entity) {
        if (entity instanceof IAnimatedEntity animated) {
            return animated.getAnimation();
        }
        return ((Internal_Animation_Monster) entity).getAttackState();
    }

    public boolean isActing(Mob entity) {
        return entity instanceof IAnimatedEntity animated
                ? animated.getAnimation() != null && animated.getAnimation() != IAnimatedEntity.NO_ANIMATION
                : ((Internal_Animation_Monster) entity).getAttackState() != 0;
    }

    public void playReaction(Mob entity, boolean executed) {
        if (entity instanceof IAnimatedEntity animated) {
            Animation animation = switch (this) {
                case ENDER_GUARDIAN -> Ender_Guardian_Entity.GUARDIAN_FALLEN;
                case IGNIS -> Ignis_Entity.BREAK_THE_SHIELD;
                case HARBINGER -> The_Harbinger_Entity.STUN_ANIAMATION;
                case LEVIATHAN -> The_Leviathan_Entity.LEVIATHAN_STUN;
                default -> throw new IllegalStateException("Unexpected animated boss: " + id);
            };
            AnimationHandler.INSTANCE.sendAnimationMessage((LLibrary_Monster) entity, animation);
        } else {
            Internal_Animation_Monster monster = (Internal_Animation_Monster) entity;
            monster.setAttackState(0);
            monster.setAttackState(executed ? executedState : breakState);
        }
    }

    public void clearReaction(Mob entity) {
        if (entity instanceof IAnimatedEntity animated) {
            AnimationHandler.INSTANCE.sendAnimationMessage((LLibrary_Monster) entity, IAnimatedEntity.NO_ANIMATION);
        } else {
            ((Internal_Animation_Monster) entity).setAttackState(0);
        }
    }
}
