package com.p1nero.tcr_bosses.entity.stamina.compat;

import com.bobmowzie.mowziesmobs.server.ability.AbilityHandler;
import com.bobmowzie.mowziesmobs.server.entity.MowzieGeckoEntity;
import com.bobmowzie.mowziesmobs.server.entity.MowzieLLibraryEntity;
import com.bobmowzie.mowziesmobs.server.entity.frostmaw.EntityFrostmaw;
import com.bobmowzie.mowziesmobs.server.entity.sculptor.EntitySculptor;
import com.bobmowzie.mowziesmobs.server.entity.wroughtnaut.EntityWroughtnaut;
import com.ilexiconn.llibrary.server.animation.Animation;
import com.ilexiconn.llibrary.server.animation.AnimationHandler;
import com.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.world.entity.Mob;

public final class MowzieStaminaCompat {
    public static Object action(Mob entity) {
        return entity instanceof MowzieLLibraryEntity animated ? animated.getAnimation()
                : ((MowzieGeckoEntity) entity).getActiveAbilityType();
    }

    public static boolean isActing(Mob entity) {
        return entity instanceof MowzieLLibraryEntity animated ? animated.getAnimation() != IAnimatedEntity.NO_ANIMATION
                : ((MowzieGeckoEntity) entity).getActiveAbility() != null;
    }

    public static boolean canStagger(Mob entity) {
        if (entity instanceof EntityWroughtnaut wroughtnaut) {
            return wroughtnaut.isActive() && wroughtnaut.getAnimation() != EntityWroughtnaut.ACTIVATE_ANIMATION
                    && wroughtnaut.getAnimation() != EntityWroughtnaut.DEACTIVATE_ANIMATION;
        }
        if (entity instanceof EntityFrostmaw frostmaw) {
            return frostmaw.getActive() && frostmaw.getAnimation() != EntityFrostmaw.ACTIVATE_ANIMATION
                    && frostmaw.getAnimation() != EntityFrostmaw.ACTIVATE_NO_CRYSTAL_ANIMATION
                    && frostmaw.getAnimation() != EntityFrostmaw.DEACTIVATE_ANIMATION;
        }
        return !(entity instanceof EntitySculptor sculptor) || sculptor.isFighting() && !sculptor.isTesting();
    }

    public static void playReaction(Mob entity) {
        if (entity instanceof MowzieLLibraryEntity animated) {
            AnimationHandler.INSTANCE.sendAnimationMessage(animated, reaction(animated));
        } else {
            interruptAbilities((MowzieGeckoEntity) entity);
        }
    }

    public static void tickReaction(Mob entity) {
        if (entity instanceof MowzieLLibraryEntity animated) {
            Animation reaction = reaction(animated);
            if (animated.getAnimation() != reaction) {
                if (entity.level().isClientSide()) {
                    animated.setAnimation(reaction);
                } else {
                    AnimationHandler.INSTANCE.sendAnimationMessage(animated, reaction);
                }
            }
            animated.setAnimationTick(Math.min(animated.getAnimationTick(), Math.max(1, reaction.getDuration() / 2)));
        }
    }

    public static void clearReaction(Mob entity) {
        if (entity instanceof MowzieLLibraryEntity animated) {
            AnimationHandler.INSTANCE.sendAnimationMessage(animated, IAnimatedEntity.NO_ANIMATION);
        } else {
            interruptAbilities((MowzieGeckoEntity) entity);
        }
    }

    private static Animation reaction(MowzieLLibraryEntity entity) {
        return entity instanceof EntityFrostmaw ? EntityFrostmaw.LAND_ANIMATION : EntityWroughtnaut.HURT_ANIMATION;
    }

    private static void interruptAbilities(MowzieGeckoEntity entity) {
        var capability = entity.getAbilityCapability();
        if (capability != null) {
            for (var ability : capability.getAbilities()) {
                if (ability.isUsing()) {
                    AbilityHandler.INSTANCE.sendInterruptAbilityMessage(entity, ability.getAbilityType());
                }
            }
        }
    }
}
