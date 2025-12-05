package net.shelmarow.tcr_bosses.entity.battle_tower.land_golem;

import com.brass_amber.ba_bt.entity.hostile.golem.BTLandGolem;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.hm.efn.gameasset.animations.EFNClawAnimations;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.AnimationParams;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.TimeEvent;
import net.shelmarow.combat_evolution.ai.efcondition.HealthCheck;
import net.shelmarow.combat_evolution.client.particle.CEParticles;
import net.shelmarow.combat_evolution.client.particle.follow.CEFollowParticleOptions;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Set;

public class LandGolemCombatBehavior {

    public static final CECombatBehaviors.Builder<MobPatch<?>> GREATSWORD;

    static {
        GREATSWORD = CECombatBehaviors.builder()

                //距离较远时会尝试翻滚拉近，距离较近时会尝试拉远
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(6.0F, 8.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_F_MOB, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 2.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )



                //距离较远时，进入游荡
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(6,8)
                                .interruptedByTime(0,4)
                                .wander(80,0,0.35F)


                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .attackLevel(1,2).withinDistance(0,4)
                                        .canInterruptParent(true)
                                        .guardWithWander(80,0,-1)
                                        .counterAnimation(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                        .counterType(CECombatBehaviors.CounterType.END)
                                        .maxGuardHit(2)
                                        .onCounterStart(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .attackLevel(1,2).withinDistance(0,4)
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB, 0F)
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(6,8)
                                .interruptedByTime(0,4)
                                .wander(80,0,-0.35F)


                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .attackLevel(1,2).withinDistance(0,4)
                                        .canInterruptParent(true)
                                        .guardWithWander(80,0,-1)
                                        .counterAnimation(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                        .counterType(CECombatBehaviors.CounterType.END)
                                        .maxGuardHit(2)
                                        .onCounterStart(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .attackLevel(1,2).withinDistance(0,4)
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB, 0F)
                                )
                        )
                )


                //平a三连
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .interruptedByLevel(3)
                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO1, 0.2F)

                                //遭受攻击概率后撤
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .attackLevel(1,2).randomChance(0.65F)
                                        .withinDistance(0.0F, 4.0F)
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0.0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .randomChance(0.65F)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .name("GP")
                                        .attackLevel(1,2)
                                        .canInterruptParent(true)
                                        .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .withinDistance(0.0F, 4.0F)
                                        .interruptedByLevel(3)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH, 0.1F)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING,20));
                                            if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                landGolemPatch.setGpSucceed(false);
                                            }
                                        })

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .custom(mobPatch -> {
                                                    if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                        return landGolemPatch.isGpSucceed();
                                                    }
                                                    return false;
                                                })
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT,0.1F)
                                        )
                                )

                                //1a后遭到攻击概率防御
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .randomChance(0.65F).attackLevel(1,2)
                                        .withinDistance(0.0F, 4.0F)
                                        .canInterruptParent(true)
                                        .guard(60)
                                        .counterAnimation(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                        .counterType(CECombatBehaviors.CounterType.END)
                                        .maxGuardHit(2)
                                        .onCounterStart(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                        })
                                )

                                //正常2a
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .interruptedByLevel(3)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO2, 0.1F)


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .name("GP")
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 4.0F)
                                                .interruptedByLevel(3)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH, 0.1F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING,20));
                                                    if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                        landGolemPatch.setGpSucceed(false);
                                                    }
                                                })

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .custom(mobPatch -> {
                                                            if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                                return landGolemPatch.isGpSucceed();
                                                            }
                                                            return false;
                                                        })
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT,0.1F)
                                                )
                                        )

                                        //遭受攻击概率后撤
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .randomChance(0.1F)
                                                .withinDistance(0.0F, 4.0F)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0.0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                                )
                                        )

                                        //遭到攻击概率防御
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .randomChance(0.1F)
                                                .attackLevel(1,2)
                                                .withinDistance(0.0F, 4.0F)
                                                .guard(60)
                                                .counterAnimation(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                                .counterType(CECombatBehaviors.CounterType.END)
                                                .maxGuardHit(2)
                                                .onCounterStart(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                                })
                                        )

                                        //概率接第三段
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .randomChance(0.1F)
                                                .withinDistance(0.0F, 6.0F)
                                                .interruptedByLevel(3).waitTime(50)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO3, 0.2F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .name("GP")
                                                        .canInterruptParent(true)
                                                        .attackLevel(1,2)
                                                        .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 4.0F)
                                                        .interruptedByLevel(3)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH, 0.1F)
                                                        .addExBehavior(mobPatch -> {
                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING,20));
                                                            if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                                landGolemPatch.setGpSucceed(false);
                                                            }
                                                        })

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .custom(mobPatch -> {
                                                                    if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                                        return landGolemPatch.isGpSucceed();
                                                                    }
                                                                    return false;
                                                                })
                                                                .canInterruptParent(true)
                                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT,0.1F)
                                                        )
                                                )
                                        )
                                )
                        )
                )

                //跑攻起手，接第三段
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(2.0F, 5.0F)
                                .interruptedByLevel(3)
                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH, 0.2F)

                                //小于80%血新增gp
                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .name("GP")
                                        .canInterruptParent(true)
                                        .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .withinDistance(0.0F, 4.0F)
                                        .interruptedByLevel(3)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH, 0.1F)
                                        .addExBehavior(mobPatch -> {
                                            if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING,20));
                                                landGolemPatch.setGpSucceed(false);
                                            }
                                        })

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .custom(mobPatch -> {
                                                    if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                        return landGolemPatch.isGpSucceed();
                                                    }
                                                    return false;
                                                })
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT,0.1F)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .canInterruptParent(true)
                                        .interruptedByLevel(3)
                                        .waitTime(60)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MIN, 0.2F)


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .name("GP")
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 4.0F)
                                                .interruptedByLevel(3)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH, 0.1F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING,20));
                                                    if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                        landGolemPatch.setGpSucceed(false);
                                                    }
                                                })

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .custom(mobPatch -> {
                                                            if(mobPatch instanceof LandGolemPatch landGolemPatch){
                                                                return landGolemPatch.isGpSucceed();
                                                            }
                                                            return false;
                                                        })
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_SKILL_CLASH_HIT,0.1F)
                                                )
                                        )


                                        //80血以下额外派生一段
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 5.0F)
                                                .randomChance(0.03F)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO3, 0.0F)
                                        )

                                        //遭受攻击概率后撤，也可能什么都不做
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .withinDistance(0.0F, 5.0F)
                                                .wander(0,0,0)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .withinDistance(0.0F, 5.0F)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0.0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                                )
                                        )
                                )
                        )
                )

                //原版EF大剑凑凑数
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(0.5F)
                        .maxCooldown(10)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(Animations.GREATSWORD_AUTO1, 0.25F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F).waitTime(50)
                                        .interruptedByLevel(3)
                                        .animationBehavior(Animations.GREATSWORD_AUTO2, 0.1F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(1.0F).weight(1F)
                                                .canInterruptParent(true)
                                                .attackLevel(1,2)
                                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                                })
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(1.0F).weight(0.5F)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 4.0F)
                                                .guard(60)
                                                .counterAnimation(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                                .counterType(CECombatBehaviors.CounterType.END)
                                                .maxGuardHit(2)
                                                .onCounterStart(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                                })
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(1.0F).weight(1F)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 6.0F)
                                                .wander(0,0,0)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .priority(1.0F).weight(0.5F)
                                                .canInterruptParent(true)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.BIPED_MOB_GREATSWORD, 0.1F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                                })
                                        )
                                )
                        )
                )


                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(0.5F)
                        .maxCooldown(20)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .interruptedByLevel(3)
                                .animationBehavior(Animations.GREATSWORD_DASH, 0.1F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .withinDistance(0.0F, 6.0F)
                                        .addCooldown(180)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0.2F)
                                )
                        )
                )

                //闪避接狮子斩或者冲刺攻击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5F)
                        .maxCooldown(40)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .attackLevel(1,2).withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .weight(2)
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .attackLevel(1,2).withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_L_MOB,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .weight(2)
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .attackLevel(1,2).withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_R_MOB,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .weight(2)
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                )
                        )
                )


                //65%血以下，穿插狮子斩
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F).maxCooldown(20)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .withinDistance(0.0F, 5.0F).waitTime(10)
                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO1, 0.1F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .attackLevel(1,2).withinDistance(0.0F, 4.0F)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(2)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F).waitTime(10)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO2, 0.1F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .attackLevel(1,2).withinDistance(0.0F, 4.0F)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .weight(2)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F).waitTime(50)
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AUTO3, 0.1F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .attackLevel(1,2).withinDistance(0.0F, 4.0F)
                                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB,0F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .weight(2)
                                                                .withinDistance(0.0F, 6.0F)
                                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH_NEW,0F)
                                                        )

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 6.0F)
                                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_DASH,0F)
                                                        )
                                                )
                                        )
                                )
                        )
                )

                //50%血以下，开始出现蓄力招
                //距离较近时，更加频繁的后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(6.0F)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .name("GP")
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .withinDistance(3.5F, 10.0F)
                                .interruptedByTime(1,10)
                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_CHARGING_MOB, 0.1F)
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .name("GP")
                                        .canInterruptParent(true)
                                        .attackLevel(1,2)
                                        .withinDistance(0.0F, 5.0F)
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST,0F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_SECOND,0F)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_SECOND,0F)
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(8.0F)
                        .maxCooldown(40)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .withinDistance(0.0F, 2.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )

                //35%血以下，会释放震地
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(16.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .name("P2")
                                .custom(mobPatch -> {
                                    if(mobPatch.getOriginal() instanceof BTLandGolem btLandGolem){
                                        return btLandGolem.isEnraged();
                                    }
                                    return false;
                                })
                                .withinDistance(0.0F, 5.0F)
                                .animationBehavior(
                                        EFNClawAnimations.NF_CLAW_BEASTROAR,
                                        new AnimationParams()
                                                .transitionTime(0.4F)
                                                .impactMultiplier(10F)
                                                .stunType(StunType.LONG)
                                                .damageSource(Set.of(EpicFightDamageTypeTags.UNBLOCKALBE))
                                )
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().forceAddEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(), 60), mobPatch.getOriginal());
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 40));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                    if(mobPatch.getTarget() != null) {
                                        LivingEntity target = mobPatch.getTarget();
                                        Vec3 pos = target.position();
                                        ParticleOptions options = new CEFollowParticleOptions(CEParticles.BYPASS_GUARD_WARNING.get(),target.getId(),new Vec3(0,1.5,0));
                                        ((ServerLevel)target.level()).sendParticles(options,pos.x, pos.y, pos.z,0,0, 0,0,0);
                                    }
                                })
                                .addTimeEvent(
                                        new TimeEvent(0.5F,mobPatch -> {
                                            doEarthquake(
                                                    mobPatch.getOriginal().level(),mobPatch.getOriginal(),
                                                    2F,4,0.0F,0.8F
                                            );
                                        })
                                )
                        )
                )
        ;
    }

    public static void doEarthquake(Level level, LivingEntity sourceEntity,float spreadArc, int distance,float forwardOffset, float intensity) {

        double baseAngle = Math.toRadians(sourceEntity.yBodyRot);
        double centerAngle = baseAngle + Math.PI / 2D;
        double spread = Math.PI * spreadArc;
        int arcSteps = Mth.ceil(distance * spread);

        int baseY = Mth.floor(sourceEntity.getBoundingBox().minY - 0.5F);

        for (int i = 0; i < arcSteps; i++) {
            double theta = ((double) i / ((double) arcSteps - 1D) - 0.5D) * spread + centerAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);

            for (int d = 1; d <= distance; d++) {
                double px = sourceEntity.getX() + vx * d + forwardOffset * Math.cos(Math.toRadians(sourceEntity.yBodyRot + 90F));
                double pz = sourceEntity.getZ() + vz * d + forwardOffset * Math.sin(Math.toRadians(sourceEntity.yBodyRot + 90F));
                int hitX = Mth.floor(px);
                int hitZ = Mth.floor(pz);

                BlockPos pos = new BlockPos(hitX, baseY, hitZ);
                BlockState block = level.getBlockState(pos);

                if (block.getRenderShape() != RenderShape.MODEL) continue;

                if (!level.isClientSide) {
                    Cm_Falling_Block_Entity fallingBlock = new Cm_Falling_Block_Entity(
                            level,
                            hitX + 0.5D,
                            baseY + 1.0D,
                            hitZ + 0.5D,
                            block,
                            20
                    );

                    double upPower = (0.2D + level.getRandom().nextGaussian() * 0.15D) * intensity;
                    fallingBlock.push(0.0D, upPower, 0.0D);
                    level.addFreshEntity(fallingBlock);
                }
            }
        }
    }

}
