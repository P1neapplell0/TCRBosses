package net.shelmarow.tcr_bosses.entity.battle_tower.core_golem;

import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.condition.HealthCheck;
import net.shelmarow.combat_evolution.ai.event.TimeEvent;
import net.shelmarow.combat_evolution.ai.params.AnimationParams;
import net.shelmarow.combat_evolution.client.particle.CEParticles;
import net.shelmarow.combat_evolution.client.particle.follow.CEFollowParticleOptions;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;

public class CoreGolemCombatBehavior {

    public static final CECombatBehaviors.Builder<MobPatch<?>> TACHI;

    static {
        TACHI = CECombatBehaviors.builder()

                //近距离后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 2.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )

                //受攻击闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(8.0F)
                        .maxCooldown(40)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_L, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_R, 0F)
                        )
                )

                //随机闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(8.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_F, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_F, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_L, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_R, 0F)
                        )
                )

                //防御
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4F)
                                .health(0.45F, HealthCheck.Comparator.GREATER_RATIO_CONTAIN)
                                .guard(60)
                                .counterAnimation(
                                        EFNTachiAnimations.NF_TACHI_BLOODLUST_END,
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
                        .priority(1F).weight(10F)
                        .maxCooldown(300).cooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,8)
                                .health(0.15F, HealthCheck.Comparator.GREATER_RATIO_CONTAIN)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B,0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(2,5)
                                        .stopByStun(5)
                                        .wander(100,0F,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .attackLevel(1,2).withinDistance(0,4)
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB, 0F)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(2,5)
                                        .stopByStun(5)
                                        .wander(100,0F,-0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .attackLevel(1,2).withinDistance(0,4)
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B_MOB, 0F)
                                        )
                                )
                        )
                )

                //平a
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(2.0F).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO1,0.2F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO2,
                                                new AnimationParams().impactMultiplier(0.5F).stunType(StunType.SHORT))

                                        //派生1
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_DASH,0F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .randomChance(0.5F)
                                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH,0.35F)
                                                        )
                                                )
                                        )
                                        //派生1-1
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AIRSLASH,
                                                                new AnimationParams().impactMultiplier(10).stunType(StunType.LONG))
                                                        .addExBehavior(mobPatch -> {
                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                                            if(mobPatch.getTarget() != null) {
                                                                Vec3 motion = mobPatch.getTarget().position().subtract(mobPatch.getOriginal().position());
                                                                mobPatch.getOriginal().addDeltaMovement(motion.scale(0.08).add(0,0.75,0));
                                                            }
                                                        })
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(3)
                                                .withinDistance(0.0F, 8.0F)
                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO3,
                                                        new AnimationParams().impactMultiplier(0.5F).stunType(StunType.SHORT))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 8.0F)
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO4,
                                                                new AnimationParams().impactMultiplier(0.5F).stunType(StunType.SHORT))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 8.0F)
                                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO5,
                                                                        new AnimationParams().impactMultiplier(0.5F).stunType(StunType.SHORT))
                                                        )

                                                        //派生2
                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 8.0F)
                                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH,
                                                                        new AnimationParams().impactMultiplier(0.5F).stunType(StunType.SHORT))
                                                                .addExBehavior(mobPatch -> {
                                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                                                })
                                                        )
                                                )

                                                //派生3
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 8.0F)
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO4,
                                                                new AnimationParams().transitionTime(0.4F).playSpeed(1.25F)
                                                                        .impactMultiplier(0.5F).stunType(StunType.SHORT))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 8.0F)
                                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO5,
                                                                        new AnimationParams().transitionTime(0.1F).playSpeed(1.15F).stunType(StunType.LONG))
                                                        )
                                                )

                                                //派生4
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .weight(2)
                                                        .withinDistance(0,6)
                                                        .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST,0.25F)
                                                        .addExBehavior(mobPatch -> {
                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);

                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.BLODDLUST.get(),30));
                                                        })
                                                )
                                        )

                                        //派生5
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(2)
                                                .withinDistance(0,6)
                                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST,0.25F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);

                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.BLODDLUST.get(),30));
                                                })
                                        )
                                )
                        )
                )

                //65%血出现额外普攻招式
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,4)
                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.TACHI_AUTO1,0.25F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,8)
                                        .animationBehavior(Animations.RUSHING_TEMPO1,0.25F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,6)
                                                .animationBehavior(Animations.TACHI_AUTO2,0.25F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,8)
                                                        .animationBehavior(Animations.RUSHING_TEMPO2,0.25F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0,8)
                                                                .animationBehavior(Animations.TACHI_AUTO3,0.25F)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .randomChance(0.5F)
                                                                        .withinDistance(0,8)
                                                                        .animationBehavior(Animations.RUSHING_TEMPO3,0.25F)
                                                                )
                                                        )
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,8)
                                                        .animationBehavior(Animations.TACHI_AUTO3,0.25F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .randomChance(0.5F)
                                                                .withinDistance(0,8)
                                                                .animationBehavior(Animations.RUSHING_TEMPO3,0.25F)
                                                        )
                                                )
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,6)
                                        .animationBehavior(Animations.TACHI_AUTO2,0.25F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,8)
                                                .animationBehavior(Animations.RUSHING_TEMPO2,0.25F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,8)
                                                        .animationBehavior(Animations.TACHI_AUTO3,0.25F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .randomChance(0.5F)
                                                                .withinDistance(0,8)
                                                                .animationBehavior(Animations.RUSHING_TEMPO3,0.25F)
                                                        )
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,8)
                                                .animationBehavior(Animations.TACHI_AUTO3,0.25F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .withinDistance(0,8)
                                                        .animationBehavior(Animations.RUSHING_TEMPO3,0.25F)
                                                )
                                        )
                                )
                        )
                )


                //中距离居合
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(5.5,9)
                                .animationBehavior(Animations.BATTOJUTSU_DASH,new AnimationParams().playSpeed(0.65F))
                        )
                )

                //中远距离跳劈+跑攻
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(5,9)
                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AIRSLASH,new AnimationParams().impactMultiplier(3.5F).stunType(StunType.LONG))
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                    if(mobPatch.getTarget() != null) {
                                        Vec3 motion = mobPatch.getTarget().position().subtract(mobPatch.getOriginal().position());
                                        mobPatch.getOriginal().addDeltaMovement(motion.scale(0.08).add(0,0.75,0));
                                    }
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH,0.25F)
                                )
                        )

                )

                //技能一文字
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(2.0F)
                        .maxCooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,4)
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.TACHI_AUTO3,0.4F)
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,6)
                                        .animationBehavior(Animations.RUSHING_TEMPO3,0F)
                                        .addTimeEvent(
                                                new TimeEvent(mobPatch -> {
                                                    if(mobPatch.getTarget() != null){
                                                        mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET,mobPatch.getTarget().position());
                                                    }
                                                })
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,6)
                                                .animationBehavior(Animations.RUSHING_TEMPO3,0F)
                                                .addTimeEvent(
                                                        new TimeEvent(mobPatch -> {
                                                            if(mobPatch.getTarget() != null){
                                                                mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET,mobPatch.getTarget().position());
                                                            }
                                                        })
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,6)
                                                        .animationBehavior(Animations.RUSHING_TEMPO3,new AnimationParams().transitionTime(0.5F).stunType(StunType.LONG))
                                                        .addTimeEvent(
                                                                new TimeEvent(0,0.5F,mobPatch -> {
                                                                    if(mobPatch.getTarget() != null){
                                                                        mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET,mobPatch.getTarget().position());
                                                                    }
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )


                //溃防连击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(10.0F)
                        .maxCooldown(400)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .interruptedByLevel(3)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_SKILL_FIRST,new AnimationParams().transitionTime(0.6F).impactMultiplier(0.25F).stunType(StunType.LONG))
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                    if(mobPatch.getTarget() != null) {
                                        LivingEntity target = mobPatch.getTarget();
                                        Vec3 pos = target.position();
                                        ParticleOptions options = new CEFollowParticleOptions(CEParticles.BYPASS_GUARD_WARNING.get(),target.getId(),new Vec3(0,1.25,0));
                                        ((ServerLevel)target.level()).sendParticles(options,pos.x, pos.y, pos.z,0,0, 0,0,0);
                                    }
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_SKILL_SECOND, new AnimationParams().stunType(StunType.NEUTRALIZE))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST, new AnimationParams().transitionTime(0.25F).damageMultiplier(2.0F).impactMultiplier(2.0F).stunType(StunType.KNOCKDOWN))
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.BLODDLUST.get(),30));
                                                })
                                        )
                                )
                        )
                )

                //60%血以下额外出现单发剑气攻击，平a能穿插单次剑气攻击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(350)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,4)
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B,0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST,0F)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);

                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.BLODDLUST.get(),30));
                                        })
                                )
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .priority(2)
                                .withinDistance(0,6)
                                .health(0.4F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST,0.35F)
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),200));
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 100));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);

                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.BLODDLUST.get(),200));
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO1,0.25F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO2,
                                                        new AnimationParams().impactMultiplier(0.25F).stunType(StunType.SHORT))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO3,
                                                                new AnimationParams().impactMultiplier(0.25F).stunType(StunType.SHORT))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO4,
                                                                        new AnimationParams().impactMultiplier(0.25F).stunType(StunType.SHORT))

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO5,
                                                                                new AnimationParams().impactMultiplier(0.25F).stunType(StunType.SHORT))

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AIRSLASH,
                                                                                        new AnimationParams().impactMultiplier(0.25F).stunType(StunType.SHORT))
                                                                                .addExBehavior(mobPatch -> {
                                                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                                                                    if(mobPatch.getTarget() != null) {
                                                                                        Vec3 motion = mobPatch.getTarget().position().subtract(mobPatch.getOriginal().position());
                                                                                        mobPatch.getOriginal().addDeltaMovement(motion.scale(0.08).add(0,0.75,0));
                                                                                    }
                                                                                })

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST_END,
                                                                                                new AnimationParams().playSpeed(1.15F))
                                                                                        .addExBehavior(mobPatch -> {
                                                                                            if(mobPatch.getOriginal().hasEffect(EFNMobEffectRegistry.BLODDLUST.get())) {
                                                                                                mobPatch.getOriginal().removeEffect(EFNMobEffectRegistry.BLODDLUST.get());
                                                                                            }
                                                                                        })
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )

                                        )
                                )
                        )
                )
        ;
    }
}
