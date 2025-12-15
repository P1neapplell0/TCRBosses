package net.shelmarow.tcr_bosses.entity.battle_tower.end_golem;

import com.brass_amber.ba_bt.entity.hostile.golem.BTEndGolem;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.AnimationParams;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.HitEvent;
import net.shelmarow.combat_evolution.ai.TimeEvent;
import net.shelmarow.combat_evolution.ai.condition.HealthCheck;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.function.Function;

public class EndGolemCombatBehavior {
    public static final CECombatBehaviors.Builder<MobPatch<?>> YAMATO;

    //条件集
    private static @NotNull Function<MobPatch<?>, Boolean> onGround(boolean reverse) {
        return mobPatch -> {
            return reverse != mobPatch.getOriginal().onGround();
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> targetOnGround(boolean reverse) {
        return mobPatch -> {
            if(mobPatch.getTarget() != null) {
                return reverse != mobPatch.getTarget().onGround();
            }
            return false;
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> distanceY(double min, double max) {
        return (mobPatch)->{
            if(mobPatch.getTarget() != null){
                double selfY = mobPatch.getOriginal().position().y;
                double targetY = mobPatch.getTarget().position().y;
                double delta = selfY - targetY;
                return min <= delta && delta <= max;
            }
            return false;
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> distance2D(double min, double max) {
        return (mobPatch)->{
            if(mobPatch.getTarget() != null){
                Vec3 pos = new Vec3(mobPatch.getOriginal().position().x, 0, mobPatch.getOriginal().position().z);
                Vec3 targetPos = new Vec3(mobPatch.getTarget().position().x, 0, mobPatch.getTarget().position().z);
                double distance = pos.distanceToSqr(targetPos);
                return min * min <= distance && distance < max * max;
            }
            return false;
        };
    }


    private static @NotNull Function<MobPatch<?>, Boolean> isBlocking() {
        return mobPatch -> {
            if (mobPatch.getTarget() != null) {
                LivingEntity target = mobPatch.getTarget();
                LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                if (entityPatch instanceof PlayerPatch<?> playerPatch) {
                    return playerPatch.getSkill(SkillSlots.GUARD).isActivated();
                }
            }
            return false;
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> animationTimeBetween(float start, float end) {
        return mobPatch -> {
            AnimationPlayer animationPlayer = mobPatch.getAnimator().getPlayerFor(null);
            if (animationPlayer != null) {
                float elapsedTime = animationPlayer.getElapsedTime();
                return start <= elapsedTime && elapsedTime < end;
            }
            return false;
        };
    }

    //时间戳事件集
    private static @NotNull TimeEvent LookAtTarget() {
        return new TimeEvent(mobPatch -> {
            if (mobPatch.getTarget() != null) {
                mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET, mobPatch.getTarget().position());
            }
        });
    }

    static {
        YAMATO = new CECombatBehaviors.Builder<>()

                /*---------------全局行为序列--------------------------------------------------------------------------------------------------------------------------------------*/

                //攻击间隙的游荡和随机闪避

                //距离较远时有概率进入游荡
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(20)
                        .backAfterFinished(false).rootName("randomWander")

                        //概率直接退出
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .weight(2)
                                .phaseBetween(0,3)
                                .withinDistance(4,12)
                                .custom(onGround(false))
                                .wander(1,0,0)
                        )

                        //或者进入游荡
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .stopByStun(5)
                                .phaseBetween(0,3)
                                .withinDistance(6,12)
                                .custom(onGround(false))
                                .interruptedByTime(3F,5F)
                                .wander(100,0,0.45F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByTime(0.35F,10F)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(6,18)
                                                .custom(onGround(false))
                                                .canInsertGlobalBehavior(true,"repaid_slash_repeat")
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4)
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4)
                                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0F)
                                                )
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .stopByStun(5)
                                .phaseBetween(0,3)
                                .withinDistance(6,12)
                                .custom(onGround(false))
                                .interruptedByTime(3F,5F)
                                .wander(100,0,-0.45F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.2F)
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByTime(0.35F,10F)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(6,18)
                                                .custom(onGround(false))
                                                .canInsertGlobalBehavior(true,"repaid_slash_repeat")
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4)
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4)
                                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0F)
                                                )
                                        )
                                )
                        )
                )

                //闪避
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(20)
                        .backAfterFinished(false).rootName("randomDodgeGround")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .phaseBetween(0,3)
                                .withinDistance(4,12)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_F,0F)
                                .addPhase(1)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .phaseBetween(0,3)
                                .withinDistance(0,4)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0F)
                                .addPhase(1)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .phaseBetween(0,3)
                                .withinDistance(0,8)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_L,0F)
                                .addPhase(1)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .phaseBetween(0,3)
                                .withinDistance(0,8)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_R,0F)
                                .addPhase(1)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )
                )

                //普攻概率衔接耀斑和射空
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(100)
                        .backAfterFinished(false).rootName("ex_combo")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"ground_to_air")
                                .withinDistance(0,4)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_UPPERSLASH,0.1F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,4)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0.1F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"air_attack")
                                .withinDistance(0,4)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0.1F)
                        )
                )

                //普通次元斩
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(100)
                        .backAfterFinished(false).rootName("judgement_cut")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByTime(0.35F,10F)
                                .randomChance(0.4F)
                                .withinDistance(0,6)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                        .canInterruptParent(true)
                                        .withinDistance(0,6)
                                        .custom(animationTimeBetween(0.35F,0.45F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST_MOB,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                        .canInterruptParent(true)
                                        .withinDistance(0,6)
                                        .custom(animationTimeBetween(0.45F,3F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )
                        )
                )

                //次元斩复读
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(100)
                        .backAfterFinished(false).rootName("judgement_cut_repeat")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByTime(0.35F,10F)
                                .randomChance(0.5F)
                                .withinDistance(0,8)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                        .canInterruptParent(true)
                                        .custom(animationTimeBetween(0.35F,3F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )
                        )
                )


                //疾走居合复读
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(20)
                        .backAfterFinished(true).rootName("repaid_slash_repeat")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"repaid_slash_repeat")
                                .withinDistance(4,18)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)
                        )
                )


                //地面升空
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(20)
                        .backAfterFinished(false).rootName("ground_to_air")


                        //射空斩蓄力起飞
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .withinDistance(0,5)
                                .custom(distanceY(-100,-0.65))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_UPPERSLASH_HOLD,0.5F)
                        )

                        //耀斑起飞
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .withinDistance(0,5)
                                .custom(distanceY(-100,-0.65))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0.25F)
                        )
                )

                //空中落地
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(20)
                        .backAfterFinished(false).rootName("air_to_ground")

                        //下瞬移
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .custom(distance2D(0,4))
                                .custom(distanceY(2,10))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_D,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(0.35F,10F)
                                        .withinDistance(0,8)
                                        .phaseContain(0)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                                .canInterruptParent(true)
                                                .custom(animationTimeBetween(0.35F,3F))
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                        )
                                )
                        )

                        //近距离落地-破盔
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .custom(distance2D(0,4))
                                .custom(distanceY(2,10))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_HELMBREAKER,0F)
                                .setPhase(0)
                        )

                        //近距离落地-炼狱坠
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .custom(distance2D(0,4))
                                .custom(distanceY(2,10))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_DRIVE_MOB,0F)
                                .setPhase(0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .custom(distance2D(0,4))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_STOMP,0F)
                                )
                        )

                        //远距离落地-杀人蜂+远距离居合
                        //如果在空中命中，额外衔接回空中连段
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .custom(distance2D(3,100))
                                .custom(distanceY(2,10))
                                .phaseContain(0)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_KILLERBEE,0F)
                                .setPhase(0)
                                .addTimeEvent(LookAtTarget())
                                .addHitEvent(
                                        new HitEvent((mobPatch, entity) -> {
                                            mobPatch.playAnimationSynchronized(EFNYamatoAnimations.YAMATO_KILLERBEE_HIT,0F);
                                            CEPatchUtils.setPhase(mobPatch,10);
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .phaseContain(10)
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .wander(0,0,0)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByTime(0.35F,10F)
                                                .withinDistance(0,8)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .custom(animationTimeBetween(0.35F,3F))
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                                )
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(0.35F,10F)
                                        .withinDistance(3,8)
                                        .phaseContain(0)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .custom(animationTimeBetween(0.35F,3F))
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                        )
                                )
                        )
                )

                //空连
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(0)
                        .backAfterFinished(false).rootName("air_attack")

                        //空中平a
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .weight(2)
                                .custom(distance2D(0,4))
                                .custom(distanceY(-5,2))
                                .custom(onGround(true))
                                .custom(targetOnGround(true))
                                .waitTime(20)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO1,0F)
                                .setPhase(0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .waitTime(20)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO2,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                                .waitTime(20)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO3,0F)
                                        )
                                )
                        )

                        //转圈圈
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .weight(2)
                                .custom(distance2D(0,4))
                                .custom(distanceY(-5,2))
                                .custom(onGround(true))
                                .custom(targetOnGround(true))
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .waitTime(20)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_ORBIT_1,0F)
                                .setPhase(0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .waitTime(20)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_ORBIT_2,0F)
                                )
                        )

                        //击飞
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .custom(distance2D(0,4))
                                .custom(distanceY(-5,2))
                                .custom(onGround(true))
                                .custom(targetOnGround(true))
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .waitTime(20)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_AIRFLUSH,0F)
                                .setPhase(0)
                        )
                )

                /*---------------普通行为序列--------------------------------------------------------------------------------------------------------------------------------------*/

                //开局次元斩
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(100).weight(1).maxCooldown(9999)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .health(1F, HealthCheck.Comparator.GREATER_RATIO_CONTAIN)
                                .withinDistance(0,12)
                                .custom(onGround(false))
                                .animationBehavior(EFNAnimations.DMC5_V_JC,0F)
                        )
                )

                //转阶段次元斩
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(10).weight(1).maxCooldown(600)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .custom(mobPatch -> {
                                    if(mobPatch.getOriginal() instanceof BTEndGolem btEndGolem){
                                        return btEndGolem.isEnraged();
                                    }
                                    return false;
                                })
                                .withinDistance(0,12)
                                .custom(onGround(false))
                                .animationBehavior(EFNAnimations.DMC5_V_JC,0F)
                        )
                )


                //闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(20).maxCooldown(100).cooldown(160)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(3,12)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_F,0F)
                                .setPhase(0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,3)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0F)
                                .setPhase(0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,8)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_L,0F)
                                .setPhase(0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,8)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"randomDodgeGround","randomWander")
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_R,0F)
                                .setPhase(0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(LookAtTarget())
                        )
                )

                //地面连段
                //普攻连段1
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(4).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,4)
                                .custom(onGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_NORMAL_AUTO1, new AnimationParams()
                                        .transitionTime(0.25F).playSpeed(0.5F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"ex_combo","judgement_cut")
                                        .withinDistance(0,4.5)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_NORMAL_AUTO2, 0.15F)

                                        //正常3a
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,5)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_NORMAL_AUTO3, 0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .interruptedByTime(0.35F,10F)
                                                        .withinDistance(2.5,12)
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                                                .canInterruptParent(true)
                                                                .withinDistance(0,8)
                                                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                                                .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                                        )
                                                )
                                        )

                                        //乱斩
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,5)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_EXTEND_AUTO3, 0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,5)
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_EXTEND_AUTO4, 0F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInsertGlobalBehavior(true,"ex_combo","judgement_cut_repeat")
                                                                .withinDistance(0,5)
                                                                .animationBehavior(EFNYamatoAnimations.YAMATO_EXTEND_AUTO5, 0F)
                                                        )
                                                )
                                        )
                                )
                        )
                )

                //普攻连段2
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(2).weight(1).maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,3.5)
                                .custom(isBlocking())
                                .custom(onGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0, 4.5)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2, 0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0, 5)
                                                .interruptedByLevel(3)
                                                .waitTime(50)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3, 0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .attackLevel(1,2)
                                                        .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B, 0F)
                                                )
                                        )
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,3.5)
                                .custom(onGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0, 4.5)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2, 0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0, 5)
                                                .interruptedByLevel(3)
                                                .waitTime(50)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3, 0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .attackLevel(1,2)
                                                        .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B, 0F)
                                                )
                                        )
                                )
                        )
                )


                //近距离火山+升空派生
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(100)

                        //普通款
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"ground_to_air","judgement_cut_repeat")
                                .withinDistance(0,4)
                                .custom(onGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_VOLCANOL,0F)
                        )

                        //蓄力款
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,4)
                                .custom(onGround(false))
                                .interruptedByTime(0.7F,0.8F)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_VOLCANOL,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"ground_to_air","judgement_cut_repeat")
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE,0F)
                                )
                        )
                )

                //普通次元斩
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(8).maxCooldown(60)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByTime(0.35F,10F)
                                .withinDistance(4,16)
                                .custom(onGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                        .canInterruptParent(true)
                                        .withinDistance(0,6)
                                        .custom(animationTimeBetween(0.35F,0.45F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST_MOB,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut_repeat")
                                        .canInterruptParent(true)
                                        .withinDistance(0,6)
                                        .custom(animationTimeBetween(0.45F,3F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )

                                //如果三秒内没有释放，闪避取消接居合派生
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .withinDistance(0,100)
                                        .custom(animationTimeBetween(3F,10F))
                                        .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_F,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,3.5)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(3.5,100)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4)
                                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0F)
                                                )
                                        )
                                )
                        )
                )

                //远距离疾走居合
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(8).maxCooldown(60).cooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(6,18)
                                .custom(onGround(false))
                                .canInsertGlobalBehavior(true,"repaid_slash_repeat")
                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,4)
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0F)
                                )
                        )
                )
        ;
    }
}
