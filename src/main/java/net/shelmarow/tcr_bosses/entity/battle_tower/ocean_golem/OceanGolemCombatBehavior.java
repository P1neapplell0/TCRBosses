package net.shelmarow.tcr_bosses.entity.battle_tower.ocean_golem;

import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.AnimationParams;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.TimeEvent;
import net.shelmarow.combat_evolution.ai.efcondition.HealthCheck;
import net.shelmarow.combat_evolution.client.particle.CEParticles;
import net.shelmarow.combat_evolution.client.particle.follow.CEFollowParticleOptions;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;

import java.util.Set;

public class OceanGolemCombatBehavior {

    public static final CECombatBehaviors.Builder<MobPatch<?>> TRIDENT;

    static {
        TRIDENT = CECombatBehaviors.builder()

                //闪避防御动作
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(2).weight(1)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(Animations.BIPED_ROLL_BACKWARD, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(Animations.BIPED_STEP_LEFT, 0F)
                        )

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .attackLevel(1,2)
                                .animationBehavior(Animations.BIPED_STEP_RIGHT, 0F)
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(10)
                        .maxCooldown(200)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 6.0F)
                                .attackLevel(1,2)
                                .guard(60)
                                .counterType(CECombatBehaviors.CounterType.END)
                                .maxGuardHit(3)
                                .counterAnimation(Animations.GRASPING_SPIRAL_SECOND,0.35F)
                                .onCounterStart(mobPatch -> {
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),60));
                                })
                        )
                )

                //主动后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(5)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 3.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )

                //随机后撤
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(3)
                        .maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 5.0F)
                                .animationBehavior(EFNDodgeAnimations.DODGE_ROLL_B_MOB, 0F)
                        )
                )



                //75%血之后出现两种战技，普攻能够穿插战技使用
                //50%血之后新增一种战技，唤雷额外出现差分攻击
                //平a三连 1
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(Animations.TRIDENT_AUTO1, new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.HEARTPIERCER, new AnimationParams().transitionTime(0.25F).playSpeed(0.75F))
                                )

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.TRIDENT_AUTO2, new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.GRASPING_SPIRAL_FIRST, 0.25F)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.HEARTPIERCER, new AnimationParams().transitionTime(0.25F).playSpeed(0.75F))
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.TRIDENT_AUTO3, new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(Animations.WRATHFUL_LIGHTING, 0.25F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(Animations.HEARTPIERCER, new AnimationParams().transitionTime(0.25F).playSpeed(0.75F))
                                                )
                                        )
                                )
                        )
                )

                //平a三连 2
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO1, 0.2F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO2, 0.2F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.GRASPING_SPIRAL_FIRST, 0.25F)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.HEARTPIERCER, new AnimationParams().transitionTime(0.25F).playSpeed(0.75F))
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO3, 0.2F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(Animations.WRATHFUL_LIGHTING, 0.25F)
                                                )


                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNSwordAnimations.NF_SWORD_AUTO4, 0.5F)
                                                        .addExBehavior(mobPatch->{
                                                            mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                                        })
                                                )
                                        )
                                )
                        )
                )

                //平a三连 3
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1)
                        .maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 4.0F)
                                .animationBehavior(Animations.LONGSWORD_AUTO1, 0.2F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.LONGSWORD_AUTO2, 0.2F)

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.HEARTPIERCER, new AnimationParams().transitionTime(0.25F).playSpeed(0.75F))
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.GRASPING_SPIRAL_FIRST, 0.25F)
                                        )


                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.WRATHFUL_LIGHTING, 0.25F)
                                        )

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .weight(4)
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.LONGSWORD_AUTO3, 0.2F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(Animations.HEARTPIERCER, new AnimationParams().transitionTime(0.25F).playSpeed(0.75F))
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(Animations.WRATHFUL_LIGHTING, 0.25F)
                                                )

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .randomChance(0.5F)
                                                        .health(0.75F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(Animations.GRASPING_SPIRAL_FIRST, 0.25F)
                                                )
                                        )
                                )
                        )
                )

                //50%血新增两个连段
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0F, 4F)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.SPEAR_TWOHAND_AIR_SLASH, 0.4F)
                                .addExBehavior(mobPatch->{
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(),40));
                                    mobPatch.getOriginal().addDeltaMovement(new Vec3(0F,0.5F,0F));
                                })

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.TRIDENT_AUTO2, new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(Animations.TRIDENT_AUTO3, new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))
                                        )
                                )
                        )
                )

                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(5)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0F, 4F)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.SPEAR_TWOHAND_AUTO1, 0.4F)

                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                        .withinDistance(0.0F, 6.0F)
                                        .animationBehavior(Animations.SPEAR_TWOHAND_AUTO2, new AnimationParams().transitionTime(0.2F).playSpeed(1.25F))

                                        .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                .withinDistance(0.0F, 6.0F)
                                                .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO4, 0.2F)

                                                .addNextBehavior(CECombatBehaviors.Behavior.builder()
                                                        .withinDistance(0.0F, 6.0F)
                                                        .animationBehavior(EFNTachiAnimations.NF_TACHI_AUTO5, 0.2F)
                                                )
                                        )
                                )
                        )
                )


                //强化唤雷
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(10).maxCooldown(400)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(4.0F, 8.0F)
                                .health(0.5F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.WRATHFUL_LIGHTING, 0.5F)
                                .addExBehavior(mobPatch -> {
                                    mobPatch.getOriginal().forceAddEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(), 60), mobPatch.getOriginal());
                                    mobPatch.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 40));
                                    mobPatch.playSound(SoundEvents.ANVIL_LAND, 0, 0);
                                })
                                .addTimeEvent(
                                        new TimeEvent(0.5F,mobPatch->{
                                            Level world = mobPatch.getOriginal().level();
                                            LivingEntity caster = mobPatch.getOriginal();
                                            double spawnX = caster.getX();
                                            double spawnY = caster.getY();
                                            double spawnZ = caster.getZ();
                                            float damage = (float)caster.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.85F;
                                            spawnEightWaves(null,world,spawnX,spawnY,spawnZ,0,60,damage,3);
                                            spawnEightWaves(null,world,spawnX,spawnY,spawnZ,22.5F,60,damage,5);
                                        })
                                )

                        )
                )

                //突刺
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(2).weight(1).maxCooldown(600)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(0.0F, 8.0F)
                                .health(0.25F, HealthCheck.Comparator.LESS_RATIO_CONTAIN)
                                .animationBehavior(Animations.TSUNAMI_REINFORCED, new AnimationParams().transitionTime(0.5F)
                                        .damageSource(Set.of(EpicFightDamageTypeTags.GUARD_PUNCTURE,EpicFightDamageTypeTags.UNBLOCKALBE))
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

                        )
                )

                //投掷三叉戟
                .newBehaviorRoot(CECombatBehaviors.BehaviorRoot.builder()
                        .priority(1).weight(1).maxCooldown(100)

                        .addFirstBehavior(CECombatBehaviors.Behavior.builder()
                                .withinDistance(6.0F, 30.0F)
                                .animationBehavior(Animations.BIPED_MOB_THROW, 0.2F)
                                .addTimeEvent(
                                        new TimeEvent(mobPatch -> {
                                           if(mobPatch.getTarget() != null){
                                               mobPatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET, mobPatch.getTarget().position());
                                           }
                                        }),
                                        new TimeEvent(0.5F, mobPatch -> {
                                            LivingEntity original = mobPatch.getOriginal();
                                            Level level = original.level();
                                            ThrownTrident thrownTrident = new ThrownTrident(level, original, original.getMainHandItem());
                                            thrownTrident.shootFromRotation(original, original.getXRot(), original.getYRot(), 0.0F, 5F, 1.0F);
                                            thrownTrident.life = 1100;
                                            thrownTrident.setBaseDamage(original.getAttributeValue(Attributes.ATTACK_DAMAGE)*0.25);
                                            level.addFreshEntity(thrownTrident);
                                        })
                                )
                        )
                )
        ;
    }

    private static void spawnEightWaves(@Nullable LivingEntity caster, Level level, double x, double y, double z, float baseRot, int duration, float damage, double distance){
        for (int i = 0; i < 8; ++i) {
            float angle = baseRot + 45 * i;
            double rad = Math.toRadians(angle);
            double offsetX = -Math.sin(rad) * distance;
            double offsetZ = Math.cos(rad) * distance;
            double dx = x + offsetX;
            double dz = z + offsetZ;

            spawnWave(caster, level, dx, y, dz, angle, duration, damage);
        }
    }

    private static void spawnWave(@Nullable LivingEntity caster, Level level, double spawnX, double spawnY, double spawnZ, float rotation, int duration, float damage){
        Wave_Entity WaveEntity = new Wave_Entity(level, caster, duration, damage);
        WaveEntity.setPos(spawnX, spawnY, spawnZ);
        WaveEntity.setYRot(rotation);
        level.addFreshEntity(WaveEntity);
        WaveEntity.setState(1);
    }
}
