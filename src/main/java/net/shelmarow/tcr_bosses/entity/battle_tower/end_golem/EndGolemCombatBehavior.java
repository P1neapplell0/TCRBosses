package net.shelmarow.tcr_bosses.entity.battle_tower.end_golem;

import com.brass_amber.ba_bt.entity.hostile.golem.BTEndGolem;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.condition.HealthCheck;
import net.shelmarow.combat_evolution.ai.event.HitEvent;
import net.shelmarow.combat_evolution.ai.event.TimeEvent;
import net.shelmarow.combat_evolution.ai.params.AnimationParams;
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

    /*
    战斗流程循环：
    1.普攻，崩离斩，疾走居合和相应派生作为主要攻击连段
    2.次元斩作为概率的派生招和长cd的单独连段出现，复读次数减少或固定
    3.放完一段完整的连段后，闪避取消后摇继续派生后续普攻或者进入后摇，随后根据情况继续派生或者短暂的游荡
    4.游荡时根据情况终止，继续派生普攻流程或者进行额外的反击/追击攻击
     */

    //条件集

    private static @NotNull Function<MobPatch<?>, Boolean> onGround(boolean reverse) {
        return mobPatch -> {
            Level level = mobPatch.getOriginal().level();
            BlockPos blockPos = mobPatch.getOriginal().blockPosition();

            boolean onGround = !level.getBlockState(blockPos).isAir() || !level.getBlockState(blockPos.offset(0, -1, 0)).isAir();

            return reverse != mobPatch.getOriginal().onGround() || onGround;
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> targetOnGround(boolean reverse) {
        return mobPatch -> {
            if(mobPatch.getTarget() != null) {
                Level level = mobPatch.getTarget().level();
                BlockPos blockPos = mobPatch.getTarget().blockPosition();

                boolean onGround = !level.getBlockState(blockPos).isAir() || !level.getBlockState(blockPos.offset(0, -1, 0)).isAir();

                return reverse != mobPatch.getTarget().onGround() || onGround;
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
    private static TimeEvent lookAtTarget() {
        return new TimeEvent(mobPatch -> {
            if (mobPatch.getTarget() != null) {
                mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET, mobPatch.getTarget().position());
            }
        });
    }

    static {
        YAMATO = new CECombatBehaviors.Builder<>()

                /*---------------全局行为序列--------------------------------------------------------------------------------------------------------------------------------------*/

                //闪避取消后摇
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(8)
                        .backAfterFinished(false).rootName("dodge_cancel")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"random_dodge")
                                .custom(distanceY(-0.25,0.25))
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_F,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"random_dodge")
                                .custom(distanceY(-0.25,0.25))
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"random_dodge")
                                .custom(distanceY(-0.25,0.25))
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_L,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"random_dodge")
                                .custom(distanceY(-0.25,0.25))
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_R,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                )

                //随机闪避
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("random_dodge")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .randomChance(0.25F)
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_F,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .randomChance(0.25F)
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .randomChance(0.25F)
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_L,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .randomChance(0.25F)
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_R,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                )

                //次元斩
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(0.5).maxCooldown(100)
                        .backAfterFinished(false).rootName("judgement_cut")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByTime(0.35F,10F)
                                .withinDistance(0,6)
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL,0F)

                                //正常释放-闪避取消后摇或衔接别的招
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash")
                                        .canInterruptParent(true)
                                        .withinDistance(0,5)
                                        .custom(animationTimeBetween(0.35F,0.45F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST_MOB,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )

                                //正常释放-闪避取消后摇或衔接别的招
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash")
                                        .canInterruptParent(true)
                                        .withinDistance(0,5)
                                        .custom(animationTimeBetween(0.45F,2F))
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_JUDEMENCUT,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(EFNSounds.JUDGEMENTCUT.get(), 1,0,0))
                                )

                                //如果2秒内没有释放，闪避取消接疾走居合派生
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"repaid_slash")
                                        .canInterruptParent(true)
                                        .withinDistance(0,100)
                                        .custom(animationTimeBetween(2F,10F))
                                        .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_F,0F)
                                        .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                        .addTimeEvent(lookAtTarget())
                                )
                        )
                )


                //近距离射空斩
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("upper_slash")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"dodge_cancel","ground_to_air")
                                .withinDistance(0,4)
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_UPPERSLASH,0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"dodge_cancel","air_attack","air_to_ground")
                                .withinDistance(0,4)
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_UPPERSLASH_HOLD,0F)
                        )
                )

                //近距离耀斑
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("flare_cut")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"dodge_cancel")
                                .withinDistance(0,4)
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .withinDistance(0,4)
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0F)
                        )
                )

                //疾走居合-全局版
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("repaid_slash")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(4,18)
                                .custom(distanceY(-0.5,0.5))
                                .custom(targetOnGround(false))
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

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"volcano")
                                        .withinDistance(0,4)
                                        .wander(0,0,0)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash","ground_to_air")
                                        .withinDistance(0,4)
                                        .wander(10,0,0)
                                )
                        )
                )
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("repaid_slash2")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,18)
                                .canInsertGlobalBehavior(true,"repaid_slash_repeat2")
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

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"volcano")
                                        .withinDistance(0,4)
                                        .wander(0,0,0)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash2","ground_to_air")
                                        .withinDistance(0,4)
                                        .wander(10,0,0)
                                )
                        )
                )

                //疾走居合复读
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(true).rootName("repaid_slash_repeat")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"repaid_slash_repeat")
                                .withinDistance(4,18)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)
                        )
                )

                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(true).rootName("repaid_slash_repeat2")

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"repaid_slash_repeat2")
                                .randomChance(0.5F)
                                .withinDistance(0,18)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)
                        )
                )

                //近距离火山
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("volcano")

                        //普通款
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"dodge_cancel","ground_to_air")
                                .withinDistance(0,3)
                                .custom(targetOnGround(false))
                                .custom(distanceY(-0.5,0.5))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_VOLCANOL,0F)
                        )

                        //蓄力款
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,3)
                                .custom(targetOnGround(false))
                                .custom(distanceY(-0.5,0.5))
                                .interruptedByTime(0.7F,0.8F)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_VOLCANOL,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","ground_to_air")
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE,0F)
                                )
                        )
                )




                //地面升空
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("ground_to_air")

                        //射空斩蓄力起飞
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .withinDistance(0,5)
                                .custom(distanceY(-100,-0.65))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_UPPERSLASH_HOLD,0.5F)
                        )

                        //耀斑起飞
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .withinDistance(0,5)
                                .custom(distanceY(-100,-0.65))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0.25F)
                        )
                )

                //空中落地
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("air_to_ground")

                        //下瞬移
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .canInsertGlobalBehavior(true,"judgement_cut","dodge_cancel","flare_cut","upper_slash","repaid_slash","volcano")
                                .custom(distance2D(0,4))
                                .custom(distanceY(1.5,10))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_D,0F)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())
                        )

                        //近距离落地-破盔
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .canInsertGlobalBehavior(true,"judgement_cut","dodge_cancel","flare_cut","upper_slash","repaid_slash","volcano")
                                .custom(distance2D(0,4))
                                .custom(distanceY(1.5,10))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_HELMBREAKER,0F)
                                .setPhase(0)
                        )

                        //近距离落地-炼狱坠
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .custom(distance2D(0,4))
                                .custom(distanceY(1.5,10))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_DRIVE_MOB,0F)
                                .setPhase(0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut","dodge_cancel","flare_cut","upper_slash","repaid_slash","volcano")
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_STOMP,0F)
                                )
                        )

                        //远距离落地-杀人蜂+远距离居合
                        //如果在空中命中，额外衔接回空中连段
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .custom(distance2D(3,100))
                                .custom(distanceY(-1,100))
                                .phaseContain(0)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_KILLERBEE,0F)
                                .setPhase(0)
                                .addTimeEvent(lookAtTarget())
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
                                                .canInsertGlobalBehavior(true,"judgement_cut","flare_cut","upper_slash","repaid_slash","volcano")
                                                .withinDistance(0,8)
                                                .wander(0,0,0)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut","flare_cut","upper_slash","repaid_slash","volcano")
                                        .withinDistance(0,8)
                                        .wander(0,0,0)
                                )
                        )
                )

                //空连
                .newGlobalBehavior(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .backAfterFinished(false).rootName("air_attack")

                        //空中平a
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2).weight(2)
                                .custom(distance2D(0,4))
                                .custom(distanceY(-2,1.5))
                                .custom(onGround(true))
                                .custom(targetOnGround(true))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO1,0F)
                                .setPhase(0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .waitTime(30)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO2,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(3)
                                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                                .waitTime(30)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_AERIALRAVE_AUTO3,0F)
                                        )
                                )
                        )

                        //转圈圈
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2).weight(2)
                                .custom(distance2D(0,4))
                                .custom(distanceY(-1,1.5))
                                .custom(onGround(true))
                                .custom(targetOnGround(true))
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .waitTime(20)
                                .animationBehavior(EFNYamatoAnimations.YAMATO_ORBIT_1,0F)
                                .setPhase(0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .weight(3)
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .waitTime(20)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_ORBIT_2,0F)
                                )
                        )

                        //击飞
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .custom(distance2D(0,4))
                                .custom(distanceY(-3,1.5))
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
                                .animationBehavior(EFNAnimations.DMC5_V_JC,0F)
                        )
                )

                //如果玩家停在空中，使用上瞬移攻击或者地升空攻击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(10)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                .custom(distanceY(-10,-0.5))
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_U,0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"ground_to_air")
                                .custom(distanceY(-10,-0.5))
                                .wander(0,0,0)
                        )
                )

                //远距离游荡
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(10).maxCooldown(200).cooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_B,0)
                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                .addTimeEvent(lookAtTarget())

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .stopByStun(5)
                                        .interruptedByTime(2F,3F)
                                        .wander(60,0,0.5F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInsertGlobalBehavior(true,"air_to_ground")
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .withinDistance(0,4)
                                                .waitTime(20)
                                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_U,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                                .addTimeEvent(lookAtTarget())
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInsertGlobalBehavior(true,"air_to_ground")
                                                .withinDistance(4,12)
                                                .waitTime(20)
                                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_U,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                                .addTimeEvent(lookAtTarget())
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .stopByStun(5)
                                        .interruptedByTime(2F,3F)
                                        .wander(60,0,-0.5F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInsertGlobalBehavior(true,"air_to_ground")
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .withinDistance(0,4)
                                                .waitTime(20)
                                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_U,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                                .addTimeEvent(lookAtTarget())
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInsertGlobalBehavior(true,"air_to_ground")
                                                .withinDistance(4,12)
                                                .waitTime(20)
                                                .animationBehavior(EFNDodgeAnimations.YAMATO_STEP_U,0F)
                                                .addExBehavior(mobPatch -> mobPatch.playSound(SoundEvents.ENDERMAN_TELEPORT,1,0,0))
                                                .addTimeEvent(lookAtTarget())
                                        )
                                )
                        )
                )

                //地面连段
                //普攻连段1
                //可衔接内容：疾走居合，耀斑，射空，次元斩，闪避后摇打断
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(4).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .canInsertGlobalBehavior(true,"judgement_cut","repaid_slash")
                                .withinDistance(0,4)
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_NORMAL_AUTO1, new AnimationParams()
                                        .transitionTime(0.25F).playSpeed(0.5F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .weight(3)
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash")
                                        .withinDistance(0,4.5)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_NORMAL_AUTO2, 0.15F)

                                        //正常3a
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(2)
                                                .canInsertGlobalBehavior(true,"dodge_cancel","judgement_cut","flare_cut","upper_slash","repaid_slash","volcano")
                                                .withinDistance(0,5)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_NORMAL_AUTO3, 0F)
                                        )

                                        //乱斩
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(2).weight(0.5F)
                                                .withinDistance(0,5)
                                                .wander(6,0,0)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_EXTEND_AUTO3, 0F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash")
                                                                .withinDistance(0,6)
                                                                .animationBehavior(EFNYamatoAnimations.YAMATO_EXTEND_AUTO4, 0F)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .priority(2)
                                                                        .canInsertGlobalBehavior(true,"judgement_cut","dodge_cancel","flare_cut","upper_slash","repaid_slash","volcano")
                                                                        .withinDistance(0,6)
                                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_EXTEND_AUTO5, 0F)
                                                                )
                                                        )
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .weight(2)
                                                        .withinDistance(0,4)
                                                        .custom(isBlocking())
                                                        .custom(targetOnGround(false))
                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1, 0F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash")
                                                                .withinDistance(0, 4.5)
                                                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2, 0F)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .priority(2)
                                                                        .canInsertGlobalBehavior(true,"dodge_cancel","judgement_cut","flare_cut","upper_slash","repaid_slash","volcano")
                                                                        .withinDistance(0, 5)
                                                                        .interruptedByLevel(3)
                                                                        .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3, 0F)

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .weight(4)
                                                                                .wander(0,0,0)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .weight(2)
                                .withinDistance(0,4)
                                .custom(isBlocking())
                                .custom(targetOnGround(false))
                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO1, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash")
                                        .withinDistance(0, 4.5)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO2, 0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(2)
                                                .canInsertGlobalBehavior(true,"judgement_cut","dodge_cancel","flare_cut","upper_slash","repaid_slash","volcano")
                                                .withinDistance(0, 5)
                                                .interruptedByLevel(3)
                                                .animationBehavior(EFNYamatoAnimations.YAMATO_DIVORCE_AUTO3, 0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .weight(4)
                                                        .wander(0,0,0)
                                                )
                                        )
                                )
                        )
                )

                //疾走居合-普通版
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(6).maxCooldown(20).cooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(4,18)
                                .custom(distanceY(-0.5,0.5))
                                .custom(targetOnGround(false))
                                .canInsertGlobalBehavior(true,"repaid_slash_repeat")
                                .animationBehavior(EFNYamatoAnimations.YAMATO_REPAIDSLASH,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"judgement_cut","dodge_cancel","upper_slash")
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,4)
                                        .canInsertGlobalBehavior(true,"air_attack","air_to_ground")
                                        .animationBehavior(EFNYamatoAnimations.YAMATO_FLARECUT_RISING,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"volcano")
                                        .withinDistance(0,4)
                                        .wander(0,0,0)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInsertGlobalBehavior(true,"dodge_cancel","repaid_slash","repaid_slash2","upper_slash","ground_to_air")
                                        .withinDistance(0,4)
                                        .wander(10,0,0)
                                )
                        )
                )
        ;
    }
}
