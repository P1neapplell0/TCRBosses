package net.shelmarow.tcr_bosses.gameassets;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.tcr_bosses.TCRBosses;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCRAnimations {


    public static AnimationManager.AnimationAccessor<ActionAnimation> AIR_STRIKE_RISING;
    public static AnimationManager.AnimationAccessor<AttackAnimation> AIR_STRIKE_A;
    public static AnimationManager.AnimationAccessor<AttackAnimation> AIR_STRIKE_B;

    @SubscribeEvent
    public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder(TCRBosses.MOD_ID, TCRAnimations::build);
    }

    private static final AnimationProperty.PlaybackSpeedModifier CONST_SPEED = (self, entityPatch, speed, prevElapsedTime, elapsedTime) -> 1F;

    public static void build(AnimationManager.AnimationBuilder builder) {


        Collider EMPTY = new MultiOBBCollider(3, 0D, 0D, 0D, 0.0D, 0.0D, 0D);
        Collider SHIELD = new MultiOBBCollider(3, 0.5D, 1D, 1D, 0.0D, 0.0D, 0D);
        Collider BODY = new MultiOBBCollider(3, 6D, 0.15D, 6D, 0.0D, 0.0D, 0D);


        AIR_STRIKE_RISING = builder.nextAccessor("biped/air_strike_rising", accessor->
            new ActionAnimation(0.1F,accessor, Armatures.BIPED)
                    .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL,true)
                    .newTimePair(0,10F)
                    .addStateRemoveOld(EntityState.TURNING_LOCKED,false)
                    .addState(EntityState.ATTACK_RESULT,damageSource -> damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? AttackResult.ResultType.SUCCESS : AttackResult.ResultType.MISSED)
                    .addEvents(
                            AnimationEvent.InTimeEvent.create(0.5F,(entitypatch, animation, params) -> {
                                LivingEntity original = entitypatch.getOriginal();
                                Vec3 pos = original.position();
                                if(original.level() instanceof ClientLevel level) {
                                    entitypatch.playSound(SoundEvents.FIRECHARGE_USE,2.5F,0,0);
                                    spawnSmoke(level,pos.x,pos.y + 1.5,pos.z,30,0.5,0.25,0.2);
                                    spawnSmoke(level,pos.x,pos.y + 2.0,pos.z,30,0.75,0.5,0.4);
                                    spawnSmoke(level,pos.x,pos.y + 2.5,pos.z,30,1.25,0.75,0.6);
                                    spawnSmoke(level,pos.x,pos.y + 3.0,pos.z,30,1.5,1.0,0.8);
                                }


                                List<LivingEntity> entities = entitypatch.getOriginal().level()
                                        .getEntitiesOfClass(LivingEntity.class, entitypatch.getOriginal().getBoundingBox().inflate(12))
                                        .stream().filter(livingEntity -> livingEntity != entitypatch.getOriginal()).toList();

                                for (LivingEntity target : entities) {
                                    Vec3 targetPos = target.position();
                                    Vec3 pushStrength = targetPos.subtract(new Vec3(pos.x, targetPos.y,pos.z)).normalize().scale(target.getRandom().nextFloat() * 2.0 + 1.0);
                                    target.push(pushStrength.x,0.25F,pushStrength.z);
                                }

                            }, AnimationEvent.Side.BOTH),
                            AnimationEvent.SimpleEvent.create((entitypatch, animation, params) -> {
                                if(!(entitypatch instanceof PlayerPatch<?>)){
                                    if(entitypatch.getTarget() != null){
                                        entitypatch.getOriginal().lookAt(EntityAnchorArgument.Anchor.FEET, entitypatch.getTarget().position());
                                    }
                                }
                            }, AnimationEvent.Side.BOTH)
                    )
        );

        AIR_STRIKE_A = builder.nextAccessor("biped/air_strike_a", accessor->
                new AttackAnimation(0.1F,0.8F,0.1F,0.8F,10F,SHIELD,Armatures.BIPED.get().toolL,accessor,Armatures.BIPED)
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.UNBLOCKALBE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,CONST_SPEED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL,true)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0,0.8F))
        );

        AIR_STRIKE_B = builder.nextAccessor("biped/air_strike_b", accessor->
                new AttackAnimation(0.1F,accessor,Armatures.BIPED,
                        new AttackAnimation.Phase(0F,0.8F,0.1F,0.7F,0.7F,0.7F,Armatures.BIPED.get().toolL,EMPTY)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)),
                        new AttackAnimation.Phase(0.7F,0.8F,0.7F,0.8F,10F,10F,Armatures.BIPED.get().rootJoint,BODY)
                                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER, EpicFightDamageTypeTags.BYPASS_DODGE))
                                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN))
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,CONST_SPEED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL,true)
                        .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0,0.8F))
                        .addEvents(AnimationEvent.InTimeEvent.create(0.8F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT)
                                .params(new Vec3f(0F, -0.25F, 0F), Armatures.BIPED.get().rootJoint, 6D, 1.0F))
        );
    }

    public static void spawnSmoke(ClientLevel level, double x, double y, double z, int count, double radius, double outwardSpeed, double downwardSpeed) {
        if (level == null) return;
        if (!level.isClientSide()) return;

        final var rand = level.random;

        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * Math.PI * 2.0;
            double dist = Math.sqrt(rand.nextDouble()) * radius;

            double px = x + Math.cos(angle) * dist;
            double pz = z + Math.sin(angle) * dist;
            double py = y + (rand.nextDouble() * 0.1 - 0.05);

            double horizontalFactor = (0.3 + rand.nextDouble() * 0.7); // 使得靠近中心的粒子也能有一定水平速度
            double vx = Math.cos(angle) * outwardSpeed * horizontalFactor * (dist / Math.max(radius, 0.0001));
            double vz = Math.sin(angle) * outwardSpeed * horizontalFactor * (dist / Math.max(radius, 0.0001));
            double vy = -Math.abs(downwardSpeed) * (0.6 + rand.nextDouble() * 0.8);

            level.addParticle(ParticleTypes.CLOUD, px, py, pz, vx, vy, vz);
        }
    }
}
