package com.p1nero.tcr_bosses.entity.battle_tower.sky_golem;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Lightning_Spear_Entity;
import com.hm.efn.gameasset.animations.*;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.p1nero.tcr_bosses.gameassets.TCRBAnimations;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.condition.HealthCheck;
import net.shelmarow.combat_evolution.ai.event.HitEvent;
import net.shelmarow.combat_evolution.ai.event.TimeEvent;
import net.shelmarow.combat_evolution.ai.params.AnimationParams;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.client.particle.CEParticles;
import net.shelmarow.combat_evolution.client.particle.follow.CEFollowParticleOptions;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class SkyGolemCombatBehavior {

    public static final CECombatBehaviors.Builder<MobPatch<?>> SWORD;

    static {
        SWORD = CECombatBehaviors.builder()

                //近距离后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 2.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )

                //残血后会更频繁的进行后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                        )
                )

                //受攻击闪避
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(10.0F)
                        .maxCooldown(20)

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

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1F).weight(10F)
                        .maxCooldown(400).cooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4F)
                                .health(0.15F, HealthCheck.Comparator.GREATER_RATIO_CONTAIN)
                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .guardWithWander(80,0,0.5F,true)
                                        .counterAnimation(
                                                AnimationsX.ENDERMAN_KNEE,
                                                new AnimationParams()
                                                        .transitionTime(0.25F)
                                                        .playSpeed(1F)
                                                        .stunType(StunType.LONG)
                                        )
                                        .counterType(CECombatBehaviors.CounterType.END)
                                        .maxGuardHit(3)
                                        .onCounterStart(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .guardWithWander(80,0,-0.5F,true)
                                        .counterAnimation(
                                                AnimationsX.ENDERMAN_KNEE,
                                                new AnimationParams()
                                                        .transitionTime(0.25F)
                                                        .playSpeed(1F)
                                                        .stunType(StunType.LONG)
                                        )
                                        .counterType(CECombatBehaviors.CounterType.END)
                                        .maxGuardHit(3)
                                        .onCounterStart(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(2.5F,6F)
                                        .stopByStun(5)
                                        .wander(120,0F,0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .attackLevel(1,2).withinDistance(0,4)
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                                        )
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(2.5F,6F)
                                        .stopByStun(5)
                                        .wander(120,0F,-0.35F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .attackLevel(1,2).withinDistance(0,4)
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                                        )
                                )
                        )
                )

                //防御
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F)
                        .maxCooldown(140)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .guard(60)
                                .counterAnimation(
                                        AnimationsX.ENDERMAN_KNEE,
                                        new AnimationParams()
                                                .transitionTime(0.25F)
                                                .playSpeed(1F)
                                                .stunType(StunType.LONG)
                                )
                                .counterType(CECombatBehaviors.CounterType.END)
                                .maxGuardHit(4)
                                .onCounterStart(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                })
                        )
                )

                //强化防御反击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F)
                        .maxCooldown(140)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .guard(60)
                                .counterAnimation(
                                        AnimationsX.REVELATION_ONEHAND,
                                        new AnimationParams()
                                                .transitionTime(0.3F)
                                                .playSpeed(0.7F)
                                )
                                .counterType(CECombatBehaviors.CounterType.END)
                                .maxGuardHit(3)
                                .onCounterStart(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .custom(mobPatch -> {
                                            LivingEntity target = mobPatch.getTarget();
                                            if (target != null) {
                                                LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                                                if (entityPatch != null) {
                                                    AssetAccessor<? extends StaticAnimation> tAnimation = Objects.requireNonNull(entityPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
                                                    return tAnimation == AnimationsX.BIPED_COMMON_NEUTRALIZED || tAnimation == AnimationsX.GREATSWORD_GUARD_BREAK;
                                                }
                                            }
                                            return false;
                                        })
                                        .animationBehavior(AnimationsX.THE_GUILLOTINE,new AnimationParams().transitionTime(0.25F))
                                )
                        )
                )

                //军刀4连
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(4F).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.5F)
                                .interruptedByLevel(3)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO1,
                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.4F).impactMultiplier(0.5F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 4.0F)
                                        .attackLevel(1,2).randomChance(0.5F)
                                        .canInterruptParent(true)
                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .interruptedByLevel(1,3)
                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO2,
                                                new AnimationParams().transitionTime(0.2F).playSpeed(1.4F).impactMultiplier(0.5F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 4.0F)
                                                .attackLevel(1,2)
                                                .randomChance(0.5F)
                                                .canInterruptParent(true)
                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                        )

                                        //派生匕首连段
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 4.0F)
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,
                                                        new AnimationParams().transitionTime(0.25F).playSpeed(0.9F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,
                                                                new AnimationParams().playSpeed(0.9F))
                                                )
                                        )

                                        //跳劈+跑攻/居合(需要变速)派生
                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 4.0F)
                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AIRSLASH,0.25F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                                    mobPatch.getOriginal().setDeltaMovement(mobPatch.getOriginal().getDeltaMovement().add(0,0.5,0));
                                                })

                                                //跑攻派生
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH,0.15F)
                                                )

                                                //居合派生
                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .weight(4F)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .interruptedByTime(0.5F,0.55F)
                                                        .animationBehavior(AnimationsX.BATTOJUTSU,
                                                                new AnimationParams().transitionTime(0.2F).playSpeed(1F))
                                                        .addTimeEvent(
                                                                new TimeEvent(0.5F,mobPatch ->{
                                                                    CEPatchUtils.setPlaySpeed(mobPatch,0.03F);
                                                                })
                                                        )


                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInterruptParent(true)
                                                                .interruptedByTime(0F,5F)
                                                                .wander(100,0,0)
                                                                .addExBehavior(mobPatch -> {
                                                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 30));
                                                                    CEPatchUtils.setPlaySpeed(mobPatch,0.03F);
                                                                })

                                                                //居合反击
                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("sheath_attack")
                                                                        .canInterruptParent(true)
                                                                        .withinDistance(0,3.5F)
                                                                        .attackLevel(1,2)
                                                                        .custom(mobPatch -> {
                                                                            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(mobPatch);
                                                                            if(current != null){
                                                                                return current.getBehaviorTime() - current.getTimeCount() <= 12;
                                                                            }
                                                                            return false;
                                                                        })
                                                                        .animationBehavior(AnimationsX.BATTOJUTSU,-0.5F)
                                                                        .addExBehavior(mobPatch -> {
                                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                                                        })

                                                                )
                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("sheath_attack")
                                                                        .canInterruptParent(true)
                                                                        .withinDistance(3.5F,100F)
                                                                        .attackLevel(1,2)
                                                                        .custom(mobPatch -> {
                                                                            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(mobPatch);
                                                                            if(current != null){
                                                                                return current.getBehaviorTime() - current.getTimeCount() <= 12;
                                                                            }
                                                                            return false;
                                                                        })
                                                                        .animationBehavior(AnimationsX.BATTOJUTSU_DASH,-0.5F)
                                                                        .addExBehavior(mobPatch -> {
                                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                                                        })
                                                                )


                                                                //正常攻击
                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("sheath_attack")
                                                                        .canInterruptParent(true)
                                                                        .withinDistance(0,3.5F)
                                                                        .custom(mobPatch -> {
                                                                            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(mobPatch);
                                                                            if(current != null){
                                                                                return current.getBehaviorTime() - current.getTimeCount() > 12;
                                                                            }
                                                                            return false;
                                                                        })
                                                                        .animationBehavior(AnimationsX.BATTOJUTSU,-0.5F)
                                                                        .addExBehavior(mobPatch -> {
                                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                                                        })

                                                                )
                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("sheath_attack")
                                                                        .canInterruptParent(true)
                                                                        .withinDistance(3.5F,100F)
                                                                        .custom(mobPatch -> {
                                                                            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(mobPatch);
                                                                            if(current != null){
                                                                                return current.getBehaviorTime() - current.getTimeCount() > 12;
                                                                            }
                                                                            return false;
                                                                        })
                                                                        .animationBehavior(AnimationsX.BATTOJUTSU_DASH,-0.5F)
                                                                        .addExBehavior(mobPatch -> {
                                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                                                            mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                                                        })
                                                                )
                                                        )
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(0.5F)
                                                .withinDistance(0.0F, 6.0F)
                                                .interruptedByLevel(1,3)
                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO3,
                                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.25F).impactMultiplier(0.5F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 4.0F)
                                                        .attackLevel(1,2)
                                                        .randomChance(0.5F)
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .interruptedByLevel(1,3)
                                                        .waitTime(30)
                                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO4,
                                                                new AnimationParams().transitionTime(0.2F).playSpeed(1.25F).impactMultiplier(0.5F))

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0.0F, 4.0F)
                                                                .canInterruptParent(true)
                                                                .attackLevel(1,2).randomChance(0.5F)
                                                                .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                                        )
                                                )
                                        )


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(0.5F)
                                                .withinDistance(0.0F, 6.0F)
                                                .interruptedByLevel(1,3)
                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO3,
                                                        new AnimationParams().transitionTime(0.2F).playSpeed(1.4F).impactMultiplier(0.5F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 4.0F)
                                                        .attackLevel(1,2)
                                                        .randomChance(0.5F)
                                                        .canInterruptParent(true)
                                                        .animationBehavior(EFNDodgeAnimations.DODGE_STEP_B, 0F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .interruptedByLevel(1,3)
                                                        .waitTime(30)
                                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO4,
                                                                new AnimationParams().transitionTime(0.2F).playSpeed(1.4F).impactMultiplier(0.5F))

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

                //体术攻击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .interruptedByLevel(3)
                                .withinDistance(0.0F, 3.5F)
                                .waitTime(40)
                                .animationBehavior(AnimationsX.ENDERMAN_KICK_COMBO,
                                        new AnimationParams().transitionTime(0.15F))
                                .addExBehavior(mobPatch -> {
                                    if(mobPatch.getTarget() != null) {
                                        Vec3 motion = mobPatch.getTarget().position().subtract(mobPatch.getOriginal().position());
                                        mobPatch.getOriginal().addDeltaMovement(motion.scale(0.04));
                                    }
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .canInterruptParent(true)
                                        .withinDistance(0.0F, 6.0F)
                                        .custom(mobPatch -> mobPatch.getAnimator().getPlayerFor(null).getElapsedTime() >= 0.85F)
                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,
                                                new AnimationParams().transitionTime(0.25F).playSpeed(0.7F))


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(3)
                                                .withinDistance(0.0F, 4.0F)
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,0F)


                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,0F)
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(AnimationsX.BIPED_STEP_BACKWARD,0F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(AnimationsX.BATTOJUTSU_DASH,new AnimationParams().playSpeed(0.65F))
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .interruptedByTime(1F,1.1F)
                                                        .animationBehavior(AnimationsX.ENDERMAN_KICK1,
                                                                new AnimationParams().transitionTime(0.15F).playSpeed(1.35F))
                                                        .addExBehavior(mobPatch -> {
                                                            mobPatch.getOriginal().setDeltaMovement(mobPatch.getOriginal().getDeltaMovement().add(0,0.75,0));
                                                        })

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInterruptParent(true)
                                                                .animationBehavior(AnimationsX.SWEEPING_EDGE,0.25F)
                                                        )

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .canInterruptParent(true)
                                                                .animationBehavior(AnimationsX.THE_GUILLOTINE,0.25F)
                                                        )
                                                )
                                        )
                                )
                        )
                )


                //武士刀四连
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(1.0F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.5F)
                                .health(0.85F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(AnimationsX.LONGSWORD_AUTO1,
                                        new AnimationParams().transitionTime(0.15F).playSpeed(1.25F).impactMultiplier(0.5F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(AnimationsX.UCHIGATANA_AUTO2,
                                                new AnimationParams().transitionTime(0.15F).playSpeed(1.25F).impactMultiplier(0.5F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(AnimationsX.UCHIGATANA_AUTO3,
                                                        new AnimationParams().transitionTime(0.15F).playSpeed(1.25F).impactMultiplier(0.5F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(AnimationsX.SWEEPING_EDGE,
                                                                new AnimationParams().transitionTime(0.15F))
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_DASH,
                                                                new AnimationParams().transitionTime(0.15F).playSpeed(1.25F))
                                                )
                                        )
                                )
                        )
                )

                //短剑攻击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(2.5F)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0,3.5F)
                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO1,
                                        new AnimationParams().transitionTime(0.2F).playSpeed(0.4F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0,4.5F)
                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO2,0.15F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,4.5F)
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3,new AnimationParams().transitionTime(0.35F).playSpeed(0.8F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4.5F)
                                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,0.15F)


                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0,4.5F)
                                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,0F)
                                                        )

                                                        //整一点快慢刀
                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0,4.5F)
                                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,
                                                                        new AnimationParams().transitionTime(0.35F).playSpeed(0.4F))
                                                                .addTimeEvent(
                                                                        new TimeEvent(0.15F, mobPatch -> {
                                                                            CEPatchUtils.setPlaySpeed(mobPatch,1F);
                                                                        })
                                                                )
                                                        )
                                                )
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0,4.5F)
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3, 0.1F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0,4.5F)
                                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,0.15F)


                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0,4.5F)
                                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,0F)
                                                        )

                                                        //整一点快慢刀
                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .withinDistance(0,4.5F)
                                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,
                                                                        new AnimationParams().transitionTime(0.35F).playSpeed(0.25F))
                                                                .addTimeEvent(
                                                                        new TimeEvent(0.15F,mobPatch -> {
                                                                            CEPatchUtils.setPlaySpeed(mobPatch,1.25F);
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )


                        )
                )

                //远距离追击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(5.0F)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(4.65F, 16.0F)
                                .health(0.85F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(AnimationsX.BIPED_PHANTOM_ASCENT_BACKWARD,0F)
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().setDeltaMovement(0,0.8,0);
                                })
                                .addTimeEvent(
                                        new TimeEvent(mobPatch -> {
                                            if(mobPatch.getTarget() != null){
                                                mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET,mobPatch.getTarget().position());
                                            }
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(AnimationsX.METEOR_SLAM, new AnimationParams()
                                                .playSpeed(0.75F).damageMultiplier(1.5F).impactMultiplier(4))
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                            mobPatch.playSound(SoundEvents.ANVIL_LAND,2, 0, 0);
                                        })
                                        .addTimeEvent(
                                                new TimeEvent(0F,0.1F,mobPatch -> {
                                                    if(mobPatch.getTarget() != null){
                                                        mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET,mobPatch.getTarget().position().add(0,-12,0));
                                                    }
                                                }),
                                                new TimeEvent(0.2F,mobPatch -> {
                                                    LevelUtil.circleSlamFracture(mobPatch.getOriginal(), mobPatch.getOriginal().level(),
                                                            mobPatch.getOriginal().position().add(0,-0.5,0), 3.5D, false, false,false);
                                                }),
                                                new TimeEvent(0.4F,mobPatch -> {
                                                    CEPatchUtils.setPlaySpeed(mobPatch,0.5F);
                                                })
                                        )
                                )
                        )
                )

                //踩地板+雷电
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(15)
                        .maxCooldown(400)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(4.65F, 16.0F)
                                .health(0.65F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNSkillAnimations.STOMP,new AnimationParams()
                                        .transitionTime(0.35F).playSpeed(0.75F))
                                .addTimeEvent(
                                        new TimeEvent(0.25F,mobPatch -> {
                                            if(mobPatch.getTarget() != null){
                                                Level level = mobPatch.getOriginal().level();
                                                Vec3 pos = mobPatch.getTarget().position();
                                                float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                summonLightingAroundTarget(level,3,12, pos.x,pos.y,pos.z,damage, null,1.5F);
                                            }
                                        })
                                )
                        )
                )

                //震飞+空中攻击
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(8.0F).maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0F, 4F)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNClawAnimations.NF_CLAW_BEASTROAR,
                                        new AnimationParams().transitionTime(0.15F).playSpeed(1.35F).damageMultiplier(0).impactMultiplier(0).stunType(StunType.NONE)
                                                .damageSource(Set.of(EpicFightDamageTypeTags.BYPASS_DODGE,EpicFightDamageTypeTags.GUARD_PUNCTURE,EpicFightDamageTypeTags.UNBLOCKALBE)))
                                .addExBehavior(mobPatch -> {
                                    if(mobPatch.getTarget() != null){
                                        mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                        mobPatch.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,12,9));
                                    }
                                })
                                .addHitEvent(
                                        new HitEvent(AttackResult.ResultType.SUCCESS,(mobPatch, entity) -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,80));
                                            if(entity instanceof LivingEntity livingEntity) {
                                                livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,80));

                                                Vec3 pos = mobPatch.getOriginal().position();
                                                Vec3 targetPos = livingEntity.position();
                                                double distance = Mth.clamp(targetPos.distanceToSqr(new Vec3(pos.x, targetPos.y, pos.z)),0,4);
                                                Vec3 motion = targetPos.subtract(new Vec3(pos.x,targetPos.y,pos.z));
                                                motion = motion.normalize().scale(0.75 * (4 - distance)/4);
                                                livingEntity.setDeltaMovement(new Vec3(motion.x,0.8,motion.z));
                                            }
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .interruptedByTime(0.5F,0.6F)
                                        .custom(mobPatch -> mobPatch.getOriginal().hasEffect(MobEffects.SLOW_FALLING))
                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AIRSLASH,0.1F)
                                        .addCooldown(300)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().setDeltaMovement(new Vec3(0,0.6,0));
                                            if(mobPatch.getTarget() != null ){
                                                mobPatch.getTarget().addEffect(new MobEffectInstance(EFNMobEffectRegistry.HORIZONTAL_STOP.get(),40));
                                            }
                                        })

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .canInterruptParent(true)
                                                .custom(mobPatch -> mobPatch.getOriginal().hasEffect(MobEffects.SLOW_FALLING))
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,0.1F)
                                                .addExBehavior(mobPatch -> {
                                                    mobPatch.getOriginal().setDeltaMovement(new Vec3(0,0.25,0));
                                                })

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .custom(mobPatch -> mobPatch.getOriginal().hasEffect(MobEffects.SLOW_FALLING))
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AIRSLASH,
                                                                new AnimationParams().transitionTime(0.1F).damageMultiplier(1.8F).impactMultiplier(5F).stunType(StunType.LONG))
                                                        .addExBehavior(mobPatch -> {
                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(EFNMobEffectRegistry.HORIZONTAL_STOP.get(),40));
                                                            mobPatch.getOriginal().removeEffect(MobEffects.SLOW_FALLING);
                                                            if(mobPatch.getTarget() != null) {
                                                                mobPatch.getTarget().removeEffect(MobEffects.SLOW_FALLING);
                                                                mobPatch.getOriginal().setDeltaMovement(new Vec3(0,0.5,0));
                                                            }
                                                        })
                                                        .addTimeEvent(
                                                                new TimeEvent(0.3F,mobPatch -> {
                                                                    Vec3 delta = mobPatch.getOriginal().getDeltaMovement();
                                                                    mobPatch.getOriginal().setDeltaMovement(new Vec3(delta.x,-10,delta.z));
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )



                //乱斩
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(15.0F)
                        .maxCooldown(600)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(1.5F, 4.0F)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_DASH,
                                        new AnimationParams()
                                                .transitionTime(0.5F)
                                                .damageMultiplier(0.4F)
                                                .stunType(StunType.LONG)
                                )
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),100));
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO3,
                                                new AnimationParams()
                                                        .transitionTime(0.25F)
                                                        .damageMultiplier(0.4F)
                                                        .stunType(StunType.SHORT)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO4,
                                                        new AnimationParams()
                                                                .damageMultiplier(0.25F)
                                                                .stunType(StunType.SHORT)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO5,
                                                                new AnimationParams()
                                                                        .damageMultiplier(0.4F)
                                                                        .stunType(StunType.SHORT)
                                                        )

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .animationBehavior(EFNShortSwordAnimations.NF_SHORTSWORD_AUTO6,
                                                                        new AnimationParams()
                                                                                .transitionTime(0.45F)
                                                                                .playSpeed(0.8F)
                                                                                .damageMultiplier(3.0F)
                                                                                .impactMultiplier(6F)
                                                                                .armorNegationMultiplier(1F)
                                                                                .stunType(StunType.KNOCKDOWN)
                                                                                .damageSource(Set.of(EpicFightDamageTypeTags.UNBLOCKALBE, EpicFightDamageTypeTags.GUARD_PUNCTURE))
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


                //空中单次突袭
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(8.0F).maxCooldown(300)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .health(0.8F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .health(0.4F, HealthCheck.Comparator.GREATER_RATIO)
                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .custom(distance2DGreater(6))
                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, 0.35F)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                            mobPatch.playSound(SoundEvents.ANVIL_LAND,3,0,0);
                                            if(mobPatch.getTarget() != null) {
                                                LivingEntity target = mobPatch.getTarget();
                                                Vec3 pos = target.position();
                                                ParticleOptions options = new CEFollowParticleOptions(CEParticles.BYPASS_GUARD_WARNING.get(),target.getId(),new Vec3(0,1.25,0));
                                                ((ServerLevel)target.level()).sendParticles(options,pos.x, pos.y, pos.z,0,0, 0,0,0);
                                            }
                                        })
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .custom(distance2DLess(6))
                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, 0.35F)
                                        .addExBehavior(mobPatch -> {
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
                                            mobPatch.playSound(SoundEvents.ANVIL_LAND,3,0,0);
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

                //多段空中突袭
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1.0F).weight(6.0F)
                        .maxCooldown(400)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .name("起飞1")
                                .withinDistance(0,6)
                                .health(0.4F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .name("突袭1")
                                        .interruptedByLevel(3)
                                        .custom(distance2DGreater(6))
                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                summonAirAttackLighting())

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .name("起飞1-1")
                                                .canInterruptParent(true)
                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .name("突袭1-1")
                                                        .interruptedByLevel(3)
                                                        .custom(distance2DGreater(6))
                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                                summonAirAttackLighting())

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .name("起飞1-1-1")
                                                                .canInterruptParent(true)
                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭1-1-1")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DGreater(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞1-1-1-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
                                                                                                    }
                                                                                                })
                                                                                        )
                                                                                )
                                                                        )
                                                                )


                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭1-1-2")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DLess(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞1-1-2-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
                                                                                                    }
                                                                                                })
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .name("突袭1-2")
                                                        .interruptedByLevel(3)
                                                        .custom(distance2DLess(6))
                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                                summonAirAttackLighting())

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .name("起飞1-2-1")
                                                                .canInterruptParent(true)
                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭1-2-1")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DGreater(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞1-2-1-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
                                                                                                    }
                                                                                                })
                                                                                        )
                                                                                )
                                                                        )
                                                                )


                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭1-2-2")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DLess(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞1-2-2-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
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


                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .name("突袭2")
                                        .interruptedByLevel(3)
                                        .custom(distance2DLess(6))
                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                summonAirAttackLighting())

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .name("起飞2-1")
                                                .canInterruptParent(true)
                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .name("突袭2-1")
                                                        .interruptedByLevel(3)
                                                        .custom(distance2DGreater(6))
                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                                summonAirAttackLighting())

                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .name("起飞2-1-1")
                                                                .canInterruptParent(true)
                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭2-1-1")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DGreater(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞2-1-1-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
                                                                                                    }
                                                                                                })
                                                                                        )
                                                                                )
                                                                        )
                                                                )

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭2-1-2")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DLess(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞2-1-2-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
                                                                                                    }
                                                                                                })
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .name("突袭2-2")
                                                        .interruptedByLevel(3)
                                                        .custom(distance2DLess(6))
                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                                summonAirAttackLighting())


                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                .name("起飞2-2-1")
                                                                .canInterruptParent(true)
                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭2-2-1")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DGreater(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_A, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_GUARD_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞2-2-1-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
                                                                                                    }
                                                                                                })
                                                                                        )
                                                                                )
                                                                        )
                                                                )

                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                        .name("突袭2-2-2")
                                                                        .interruptedByLevel(3)
                                                                        .custom(distance2DLess(6))
                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().stunType(StunType.LONG))
                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()),
                                                                                summonAirAttackLighting())

                                                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                .name("起飞2-2-2-1")
                                                                                .canInterruptParent(true)
                                                                                .animationBehavior(TCRBAnimations.AIR_STRIKE_RISING, 0)

                                                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                                                        .name("突袭结束")
                                                                                        .animationBehavior(TCRBAnimations.AIR_STRIKE_B, new AnimationParams().transitionTime(0.35F).playSpeed(1.3F))
                                                                                        .addExBehavior(airAttackWarning(CEParticles.BYPASS_DODGE_WARNING.get()))
                                                                                        .addTimeEvent(
                                                                                                new TimeEvent(0.5F,mobPatch -> {
                                                                                                    if(mobPatch.getTarget() != null){
                                                                                                        Level level = mobPatch.getOriginal().level();
                                                                                                        Vec3 pos = mobPatch.getTarget().position();
                                                                                                        float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                                                                                                        summonLightingAroundTarget(level,3.5,24, pos.x,pos.y,pos.z,damage, null,1.5F);
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
                )
        ;
    }

    private static @NotNull Consumer<MobPatch<?>> summonAirAttackLighting() {
        return mobPatch -> {
            if (mobPatch.getTarget() != null) {
                Level level = mobPatch.getOriginal().level();
                Vec3 pos = mobPatch.getTarget().position();
                float damage = (float) mobPatch.getOriginal().getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.65F;
                summonLightingAroundTarget(level, 3, 16, pos.x, pos.y, pos.z, damage, null, 1.5F);
            }
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> distance2DLess(double dis) {
        return mobPatch -> {
            if (mobPatch.getTarget() != null) {
                Vec3 pos = mobPatch.getOriginal().position();
                Vec3 targetPos = mobPatch.getTarget().position();
                double distance = pos.distanceToSqr(new Vec3(targetPos.x, pos.y, targetPos.z));
                return distance <= dis * dis;
            }
            return false;
        };
    }

    private static @NotNull Function<MobPatch<?>, Boolean> distance2DGreater(double dis) {
        return mobPatch -> {
            if (mobPatch.getTarget() != null) {
                Vec3 pos = mobPatch.getOriginal().position();
                Vec3 targetPos = mobPatch.getTarget().position();
                double distance = pos.distanceToSqr(new Vec3(targetPos.x, pos.y, targetPos.z));
                return distance > dis * dis;
            }
            return false;
        };
    }

    private static @NotNull Consumer<MobPatch<?>> airAttackWarning(ParticleType<CEFollowParticleOptions> type) {
        return mobPatch -> {
            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(), 60));
            mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 20));
            mobPatch.playSound(SoundEvents.ANVIL_LAND, 3, 0, 0);
            if (mobPatch.getTarget() != null) {
                LivingEntity target = mobPatch.getTarget();
                Vec3 pos = target.position();
                ParticleOptions options = new CEFollowParticleOptions(type, target.getId(), new Vec3(0, 1.25, 0));
                ((ServerLevel) target.level()).sendParticles(options, pos.x, pos.y, pos.z, 0, 0, 0, 0, 0);
            }
        };
    }

    //召唤斯库拉的闪电进行攻击
    private static void summonLightingAroundTarget(Level world,double range,int num, double x, double y, double z, float damage, @Nullable LivingEntity caster, float size){
        //锁定目标位置，在其周围随机生成闪电
        for (int i = 0; i<num; i++){
            double randomX = x + world.getRandom().nextDouble() * 2 * range - range;
            double randomZ = z + world.getRandom().nextDouble() * 2 * range - range;
            int delay = world.getRandom().nextInt(19) + 10;
            spawnLightningStorm(world,randomX,y,randomZ,delay,damage,caster,size);
        }

    }

    public static void spawnLightningStorm(Level world, double x, double y, double z, int delay, float damage,@Nullable LivingEntity caster, float size) {
        if (world != null) {
            BlockPos blockpos = BlockPos.containing(x, y, z);
            boolean foundGround = false;
            double groundY = 0.0F;

            do {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = world.getBlockState(blockpos1);
                if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                    if (!world.isEmptyBlock(blockpos)) {
                        BlockState blockstate1 = world.getBlockState(blockpos);
                        VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                        if (!voxelshape.isEmpty()) {
                            groundY = voxelshape.max(Direction.Axis.Y);
                        }
                    }

                    foundGround = true;
                    break;
                }

                blockpos = blockpos.below();
            } while(blockpos.getY() >= Mth.floor(y) - 1);

            if (foundGround) {
                Lightning_Storm_Entity storm = new Lightning_Storm_Entity(world, x, (double)blockpos.getY() + groundY, z, 0, delay, damage, 0, caster, size);
                world.addFreshEntity(storm);
            }

        }
    }

    private static void createHomingLightningSpears(Level world, LivingEntity caster, LivingEntity target, int spearCount, float spreadAngle, double damage, double areaDamage) {
        if (world != null && !world.isClientSide() && caster != null && target != null) {
            Vec3 casterPos = caster.position();
            Vec3 targetPos = target.position();
            Vec3 direction = targetPos.subtract(casterPos).normalize();

            for(int i = 0; i < spearCount; ++i) {
                float angle = -spreadAngle / 2.0F + spreadAngle * (float)i / (float)(spearCount - 1);
                double angleRad = (double)angle * (Math.PI / 180D);
                Vec3 spearDirection = direction.yRot((float)angleRad).normalize();
                double spawnX = casterPos.x + spearDirection.x * (double)2.0F;
                double spawnY = casterPos.y + (double)caster.getBbHeight() * 0.7;
                double spawnZ = casterPos.z + spearDirection.z * (double)2.0F;
                Lightning_Spear_Entity spear = spawnLightningSpear(world, caster, spearDirection, spawnX, spawnY, spawnZ, (float) damage, (float)areaDamage, 2.0F);
                if (spear != null) {
                    spear.accelerationPower = 0.15;
                }
            }

        }
    }

    private static Lightning_Spear_Entity spawnLightningSpear(Level world, LivingEntity caster, Vec3 direction, double x, double y, double z, float damage, float areaDamage, float areaRadius) {
        if (world == null) {
            return null;
        } else {
            Lightning_Spear_Entity spear = new Lightning_Spear_Entity(caster, direction, world, damage);
            spear.setAreaDamage(areaDamage);
            spear.setAreaRadius(areaRadius);
            spear.setPos(x, y, z);
            world.addFreshEntity(spear);
            return spear;
        }
    }
}
