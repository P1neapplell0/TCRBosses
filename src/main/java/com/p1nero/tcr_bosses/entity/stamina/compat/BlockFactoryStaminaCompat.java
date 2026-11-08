package com.p1nero.tcr_bosses.entity.stamina.compat;

import net.minecraft.world.entity.Mob;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.AbstractStateBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.dragon.boss.InfernalDragonEntity;
import net.unusual.block_factorys_bosses.entity.boss.knight.UnderworldKnightEntity;
import net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenEntity;
import net.unusual.block_factorys_bosses.entity.boss.sandworm.SandwormEntity;
import net.unusual.block_factorys_bosses.entity.boss.yeti.YetiEntity;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Set;
import java.util.Locale;

public final class BlockFactoryStaminaCompat {
    private static final Set<String> IDLE_STATES = Set.of("idle", "patrol", "patrol_wander", "walk", "chase");

    public static Object action(Mob entity) {
        if (entity instanceof AbstractBossEntity boss) {
            return boss.getState() == null ? "idle" : boss.getState().name().toLowerCase(Locale.ROOT);
        }
        var state = ((AbstractStateBossEntity) entity).getStateController().getActiveStateRef();
        return state == null ? "idle" : state.getName();
    }

    public static boolean isActing(Mob entity) {
        return !IDLE_STATES.contains(action(entity));
    }

    public static boolean canStagger(Mob entity) {
        if (entity instanceof YetiEntity yeti) {
            int enrage = yeti.getEntityData().get(YetiEntity.DATA_IS_ENRAGED);
            return !yeti.isCinematic() && !yeti.isDead() && (enrage == 2 || enrage == 5);
        }
        if (entity instanceof InfernalDragonEntity dragon) {
            return !dragon.isCinematic();
        }
        if (entity instanceof UnderworldKnightEntity knight) {
            return !hasPriorityAnimation(entity) && knight.getImmuneStacks() <= 0;
        }
        if (entity instanceof KrakenEntity kraken) {
            return !kraken.isHidden() && !kraken.isDying() && !kraken.isInDyingState()
                    && !action(entity).toString().contains("cinematic");
        }
        return !(entity instanceof SandwormEntity worm) || !worm.isCinematic()
                && !action(entity).toString().contains("intro") && !action(entity).toString().contains("hidden");
    }

    public static void clearAction(Mob entity) {
        if (entity instanceof AbstractBossEntity boss) {
            boss.setState(entity instanceof YetiEntity ? "IDLE" : "idle");
        } else {
            AbstractStateBossEntity boss = (AbstractStateBossEntity) entity;
            boss.getStateController().endAll();
            boss.stopTriggeredAnimation("main_controller", null);
        }
    }

    public static boolean hasPriorityAnimation(Mob entity) {
        if (entity instanceof YetiEntity yeti) {
            return yeti.isCinematic() || yeti.isDead();
        }
        if (entity instanceof InfernalDragonEntity dragon) {
            return dragon.isIntro() || dragon.isTransforming() || dragon.isDeath();
        }
        if (entity instanceof UnderworldKnightEntity knight) {
            String state = action(entity).toString();
            return knight.isCinematic() || Set.of("intro", "fake_dead", "resurrect", "dead").contains(state)
                    || state.equals("jump_back") && knight.getBossPhase() == 3;
        }
        if (entity instanceof KrakenEntity kraken) {
            return kraken.isInDyingState() || action(entity).toString().contains("cinematic");
        }
        return entity instanceof SandwormEntity worm && worm.isCinematic();
    }

    public static void clearReaction(Mob entity) {
        if (!hasPriorityAnimation(entity)) {
            clearAction(entity);
        }
    }

    public static RawAnimation dragonReaction(Mob entity) {
        InfernalDragonEntity dragon = (InfernalDragonEntity) entity;
        return RawAnimation.begin().thenPlayAndHold(dragon.isTransformed() ? "animation.dragon.hit_overlay" : "animation.hit");
    }
}
