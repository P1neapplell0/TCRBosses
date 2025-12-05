package net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem;

import com.brass_amber.ba_bt.entity.hostile.golem.BTLandGolem;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.hm.efn.gameasset.animations.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.AnimationParams;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.HitEvent;
import net.shelmarow.combat_evolution.ai.TimeEvent;
import net.shelmarow.combat_evolution.ai.efcondition.HealthCheck;
import net.shelmarow.combat_evolution.client.particle.CEParticles;
import net.shelmarow.combat_evolution.client.particle.follow.CEFollowParticleOptions;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import net.shelmarow.tcr_bosses.entity.battle_tower.land_golem.LandGolemPatch;
import net.shelmarow.tcr_bosses.gameassets.TCRAnimations;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;
import java.util.Set;

public class SkyGolemCombatBehavior {

    public static final CECombatBehaviors.Builder<MobPatch<?>> SWORD;

    static {
        SWORD = CECombatBehaviors.builder()

                //近距离后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 2.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )

                //受攻击闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F)
                        .maxCooldown(60)

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


                //平a三连
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(Animations.LONGSWORD_AUTO1, 0.2F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.LONGSWORD_AUTO2, 0.2F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.LONGSWORD_AUTO3, 0.2F)
                                        )
                                )
                        )
                )

                //武士刀四连
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F).health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.LONGSWORD_AUTO1, new AnimationParams().transitionTime(0.15F).playSpeed(0.85F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.UCHIGATANA_AUTO2, new AnimationParams().transitionTime(0.15F).playSpeed(0.85F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.UCHIGATANA_AUTO3, new AnimationParams().transitionTime(0.15F).playSpeed(0.85F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_BLOODLUST_END, new AnimationParams().transitionTime(0.15F).playSpeed(0.85F))
                                                )
                                        )
                                )
                        )
                )

                //军刀2~4连
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .interruptedByLevel(3)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO1, 0.2F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 4.0F)
                                        .attackLevel(1,2).randomChance(0.5F)
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .interruptedByLevel(1,3)
                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO2, 0.2F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 4.0F)
                                                .attackLevel(1,2).randomChance(0.5F)
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .interruptedByLevel(1,3)
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO3, 0.2F)
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 4.0F)
                                                        .attackLevel(1,2).randomChance(0.5F)
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .interruptedByLevel(1,3)
                                                        .waitTime(30)
                                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO4, 0.2F)

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 4.0F)
                                                                .canInterruptParent(true)
                                                                .attackLevel(1,2).randomChance(0.5F)
                                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                                        )
                                                )
                                        )
                                )
                        )
                )


                //跑攻
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(3.0F, 6.0F)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH, 0.2F)
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(3.0F, 6.0F)
                                .wander(60,1,0)
                                .interruptedByTime(0,3)
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,60,4));
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 3.0F)
                                        .canInterruptParent(true)
                                        .animationBehavior(Animations.TACHI_DASH, 0.2F)
                                )
                        )
                )

                //跳攻
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AIRSLASH,
                                        new AnimationParams()
                                                .transitionTime(0.2F)
                                                .impactMultiplier(3F)
                                                .stunType(StunType.LONG)
                                )
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                    mobPatch.getOriginal().addDeltaMovement(new Vec3(0F,0.5F,0F));
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(1.5F, 6.0F)
                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH, 0.2F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0F, 1.5F)
                                        .animationBehavior(Animations.LONGSWORD_DASH, 0.2F)
                                )
                        )
                )

                //防御
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(160)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .guard(60)
                                .counterAnimation(
                                        Animations.LONGSWORD_DASH,
                                        new AnimationParams()
                                                .transitionTime(0.25F)
                                                .playSpeed(0.8F)
                                                .stunType(StunType.LONG)
                                )
                                .counterType(CECombatBehaviors.CounterType.RANDOM)
                                .counterChance(0.3)
                                .onCounterStart(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                })
                        )
                )

                //空中突袭
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(8.0F).maxCooldown(200)
                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(TCRAnimations.AIR_STRIKE_RISING, 0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .custom(mobPatch -> {
                                            if(mobPatch.getTarget() != null) {
                                                Vec3 pos = mobPatch.getOriginal().position();
                                                Vec3 targetPos = mobPatch.getTarget().position();
                                                double distance = pos.distanceToSqr(new Vec3(targetPos.x, pos.y, targetPos.z));
                                                return distance > 6 * 6;
                                            }
                                            return false;
                                        })
                                        .animationBehavior(TCRAnimations.AIR_STRIKE_A,0)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                            if(mobPatch.getTarget() != null) {
                                                LivingEntity target = mobPatch.getTarget();
                                                Vec3 pos = target.position();
                                                ParticleOptions options = new CEFollowParticleOptions(CEParticles.BYPASS_GUARD_WARNING.get(),target.getId(),new Vec3(0,1.25,0));
                                                ((ServerLevel)target.level()).sendParticles(options,pos.x, pos.y, pos.z,0,0, 0,0,0);
                                            }
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .custom(mobPatch -> {
                                            if(mobPatch.getTarget() != null) {
                                                Vec3 pos = mobPatch.getOriginal().position();
                                                Vec3 targetPos = mobPatch.getTarget().position();
                                                double distance = pos.distanceToSqr(new Vec3(targetPos.x, pos.y, targetPos.z));
                                                return distance <= 6 * 6;
                                            }
                                            return false;
                                        })
                                        .animationBehavior(TCRAnimations.AIR_STRIKE_B, 0)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                            if(mobPatch.getTarget() != null) {
                                                LivingEntity target = mobPatch.getTarget();
                                                Vec3 pos = target.position();
                                                ParticleOptions options = new CEFollowParticleOptions(CEParticles.BYPASS_DODGE_WARNING.get(),target.getId(),new Vec3(0,1.25,0));
                                                ((ServerLevel)target.level()).sendParticles(options,pos.x, pos.y, pos.z,0,0, 0,0,0);
                                            }
                                        })
                                )
                        )
                )


                //溃防连击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(12.0F)
                        .maxCooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .interruptedByLevel(3)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_SKILL_FIRST,new AnimationParams().transitionTime(0.6F).impactMultiplier(2.5F).stunType(StunType.HOLD))
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
                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO2, new AnimationParams().transitionTime(0.15F).damageMultiplier(2.0F).impactMultiplier(2.0F).stunType(StunType.LONG))
                                                .addHitEvent(
                                                        new HitEvent((mobPatch,target) -> {
                                                            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), EpicFightSounds.BLADE_RUSH_FINISHER.get(), target.getSoundSource(), 1F, 1F);
                                                        })
                                                )
                                        )
                                )
                        )
                )

                //乱斩
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(2.0F).weight(1.0F)
                        .maxCooldown(500)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(1.5F, 6.0F)
                                .health(0.3F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_DASH,
                                        new AnimationParams()
                                                .transitionTime(0.5F)
                                                .playSpeed(1.25F)
                                                .damageMultiplier(0.4F)
                                                .stunType(StunType.LONG)
                                )
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3,
                                                new AnimationParams()
                                                        .transitionTime(0.25F)
                                                        .playSpeed(1.5F)
                                                        .damageMultiplier(0.4F)
                                                        .stunType(StunType.SHORT)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,
                                                        new AnimationParams()
                                                                .transitionTime(0F)
                                                                .playSpeed(1.5F)
                                                                .damageMultiplier(0.25F)
                                                                .stunType(StunType.SHORT)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,
                                                                new AnimationParams()
                                                                        .transitionTime(0F)
                                                                        .playSpeed(1.5F)
                                                                        .damageMultiplier(0.4F)
                                                                        .stunType(StunType.SHORT)
                                                        )

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO6,
                                                                        new AnimationParams()
                                                                                .transitionTime(0.15F)
                                                                                .damageMultiplier(3.0F)
                                                                                .impactMultiplier(6F)
                                                                                .armorNegationMultiplier(1F)
                                                                                .stunType(StunType.KNOCKDOWN)
                                                                                .damageSource(Set.of(EpicFightDamageTypeTags.UNBLOCKALBE))
                                                                )
                                                                .addExBehavior(mobPatch -> {
                                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),30));
                                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                                                    if(mobPatch.getTarget() != null) {
                                                                        LivingEntity target = mobPatch.getTarget();
                                                                        Vec3 pos = target.position();
                                                                        ParticleOptions options = new CEFollowParticleOptions(CEParticles.BYPASS_GUARD_WARNING.get(),target.getId(),new Vec3(0,1.5,0));
                                                                        ((ServerLevel)target.level()).sendParticles(options,pos.x, pos.y, pos.z,0,0, 0,0,0);
                                                                    }
                                                                })
                                                                .addHitEvent(
                                                                        new HitEvent((mobPatch,target) -> {
                                                                            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), EpicFightSounds.BLADE_RUSH_FINISHER.get(), target.getSoundSource(), 1F, 1F);
                                                                        })
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
