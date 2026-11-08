package com.p1nero.tcr_bosses.entity.stamina;

import com.p1nero.tcr_bosses.config.BossStaminaConfig;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.core.animation.RawAnimation;

public interface NativeBossType {
    String id();

    BossStaminaConfig config();

    Object action(Mob entity);

    boolean isActing(Mob entity);

    void playReaction(Mob entity, boolean executed);

    void clearReaction(Mob entity);

    ServerBossEvent bossEvent(Mob entity);

    default boolean canStagger(Mob entity) {
        return true;
    }

    default void tickReaction(Mob entity, boolean executed) {
    }

    default boolean shouldEndReaction(Mob entity) {
        return false;
    }

    default RawAnimation reactionAnimation(Mob entity, boolean executed) {
        return null;
    }
}
