package net.shelmarow.tcr_bosses.entity.battle_tower.core_golem;

import com.hm.efn.gameasset.animations.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.shelmarow.combat_evolution.ai.AnimationParams;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.TimeEvent;
import net.shelmarow.combat_evolution.ai.efcondition.HealthCheck;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;

public class CoreGolemCombatBehavior {

    public static final CECombatBehaviors.Builder<MobPatch<?>> DUAL_SWORD;

    static {
        DUAL_SWORD = CECombatBehaviors.builder()

                //近距离后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F).maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 2.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )
                )

                //50%血量以下，出现概率闪避反击
                //受攻击闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(15.0F).maxCooldown(10)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_L, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_R, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )
                )

                //随机闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F).maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_F, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_F, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_L, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_R, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.75F)
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                        )
                                )
                        )
                )

                //防御
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F).maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .attackLevel(1,2)
                                .withinDistance(0.0F, 4F)
                                .health(0.45F, HealthCheck.Comparator.GREATER_RATIO_CONTAIN)
                                .guard(60)
                                .counterAnimation(
                                        EFNDualSwordAnimations.NF_DUAL_AUTO4,
                                        new AnimationParams()
                                                .transitionTime(0.25F)
                                                .playSpeed(0.8F)
                                                .stunType(StunType.LONG)
                                )
                                .counterType(CECombatBehaviors.CounterType.END)
                                .maxGuardHit(3)
                                .onCounterStart(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                })
                        )
                )

                //随机游荡
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(5,12)
                                .interruptedByLevel(0)
                                .wander(80,0F,0.45F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .priority(2)
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .randomChance(0.75F)
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                                )
                                        )
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(5,12)
                                .interruptedByLevel(0)
                                .wander(80,0F,-0.45F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .priority(2)
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0,4)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .randomChance(0.75F)
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)
                                                )
                                        )
                                )
                        )
                )

                //平a
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(2.0F).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO1,0.25F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 5.0F)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO2, new AnimationParams().stunType(StunType.SHORT))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO3, new AnimationParams().stunType(StunType.SHORT))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4, new AnimationParams().stunType(StunType.SHORT))
                                                )
                                        )


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(3)
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DODGE,0.35F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_STORMATK,0.25F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 6.0F)
                                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO3, new AnimationParams().stunType(StunType.SHORT))

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .withinDistance(0.0F, 6.0F)
                                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4, new AnimationParams().stunType(StunType.SHORT))
                                                                )
                                                        )
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_DASH, new AnimationParams().stunType(StunType.LONG))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .interruptedByLevel(3)
                                                                .withinDistance(0.0F, 4.0F)
                                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL,
                                                                        new AnimationParams().transitionTime(0.2F))

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .canInterruptParent(true)
                                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND,
                                                                                new AnimationParams().transitionTime(0.2F))
                                                                )
                                                        )

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 6.0F)
                                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4, new AnimationParams().stunType(StunType.SHORT))
                                                        )
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .withinDistance(0.0F, 4.0F)
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL,
                                                        new AnimationParams().transitionTime(0.2F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND,
                                                                new AnimationParams().transitionTime(0.2F))
                                                )
                                        )
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(2.0F).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByTime(0.75F,0.85F)
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(Animations.BIPED_MOB_SWORD_DUAL1,
                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(0.95F,1.05F)
                                        .canInterruptParent(true)
                                        .withinDistance(0.0F, 5.0F)
                                        .animationBehavior(Animations.BIPED_MOB_SWORD_DUAL2,
                                                new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO3, new AnimationParams().stunType(StunType.SHORT))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4, new AnimationParams().stunType(StunType.SHORT))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 6.0F)
                                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4,
                                                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.25F).stunType(StunType.LONG))
                                                        )
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .withinDistance(0.0F, 4.0F)
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL,
                                                        new AnimationParams().transitionTime(0.2F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND,
                                                                new AnimationParams().transitionTime(0.2F))
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 3.5F)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AIRSLASH,
                                                        new AnimationParams().transitionTime(0.2F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 4.0F)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4,
                                                                new AnimationParams().transitionTime(0.2F).playSpeed(1.25F).stunType(StunType.LONG))
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(Animations.SWORD_DUAL_AUTO2,
                                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(Animations.SWORD_DUAL_AUTO1,
                                                                new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .interruptedByLevel(3)
                                                                .withinDistance(0.0F, 4.0F)
                                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL,
                                                                        new AnimationParams().transitionTime(0.2F))

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .canInterruptParent(true)
                                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND,
                                                                                new AnimationParams().transitionTime(0.2F))
                                                                )
                                                        )

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 6.0F)
                                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AUTO4,
                                                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.25F).stunType(StunType.LONG))
                                                        )
                                                )
                                        )
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(Animations.SWORD_DUAL_AUTO3, new AnimationParams().transitionTime(0.2F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(0.75F,0.85F)
                                        .withinDistance(0.0F, 4.0F)
                                        .animationBehavior(Animations.BIPED_MOB_DAGGER_TWOHAND1,
                                                new AnimationParams().transitionTime(0.25F).stunType(StunType.LONG))
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 40));
                                        })
                                        .addTimeEvent(
                                                new TimeEvent(mobPatch -> {
                                                    if(mobPatch.getTarget() != null){
                                                        mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET, mobPatch.getTarget().position());
                                                    }
                                                })
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(Animations.BIPED_MOB_DAGGER_TWOHAND2,
                                                        new AnimationParams().playSpeed(1.25F).stunType(StunType.KNOCKDOWN))
                                                .addTimeEvent(
                                                        new TimeEvent(mobPatch -> {
                                                            if(mobPatch.getTarget() != null){
                                                                mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET, mobPatch.getTarget().position());
                                                            }
                                                        })
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .withinDistance(0.0F, 4.0F)
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL,
                                                        new AnimationParams().transitionTime(0.2F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND,
                                                                new AnimationParams().transitionTime(0.2F))
                                                )
                                        )
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_AIRSLASH,
                                        new AnimationParams().transitionTime(0.2F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 5.0F)
                                        .animationBehavior(Animations.DANCING_EDGE,
                                                new AnimationParams().transitionTime(0.2F))
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F).maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(4.0F, 5.5F)
                                .animationBehavior(Animations.SWORD_DUAL_DASH,
                                        new AnimationParams().transitionTime(0.2F))
                        )
                )

                //75%血量以下出现双刀战技，并能够在普攻连段中派生
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(2.0F).weight(1.0F).maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByLevel(3)
                                .withinDistance(0.0F, 4.0F)
                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL,
                                        new AnimationParams().transitionTime(0.2F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNDualSwordAnimations.NF_DUAL_SKILL_EXTEND,
                                                new AnimationParams().transitionTime(0.2F))
                                )
                        )
                )

                //65%血量以下，出现放逐之刃的普攻连段
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(2.0F).maxCooldown(10)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByLevel(3)
                                .withinDistance(0.0F, 3.0F)
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_A,
                                        new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                //一段派生
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByLevel(3)
                                        .canInterruptParent(true)
                                        .withinDistance(0.0F, 5.0F)
                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AB,
                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABA,
                                                        new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABAA,
                                                                new AnimationParams().transitionTime(0.25F).playSpeed(1.25F))
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABAB,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )
                                        )


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABB,
                                                        new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABBA,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_ABBB,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )
                                        )
                                )

                                //二段
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByLevel(3)
                                        .canInterruptParent(true)
                                        .withinDistance(0.0F, 5.0F)
                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AA,
                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                        //二段派生
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAB,
                                                        new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AABA,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AABB,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )
                                        )

                                        //三段
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .interruptedByLevel(3)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAA,
                                                        new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))

                                                //三段派生
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAAB,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )

                                                //四段
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .canInterruptParent(true)
                                                        .withinDistance(0.0F, 5.0F)
                                                        .animationBehavior(EFNExsiliumgladiusAnimations.EXSILIUMGLADIUS_AAAA,
                                                                new AnimationParams().transitionTime(0.1F).playSpeed(1.75F))
                                                )
                                        )
                                )
                        )
                )
        ;
    }
}
