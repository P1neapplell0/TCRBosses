package net.shelmarow.tcr_bosses.gameassets;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.main.EpicFightMod;
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

    public static void build(AnimationManager.AnimationBuilder builder) {

        AnimationProperty.PlaybackSpeedModifier CONST_SPEED =
                (self, entitypatch, speed, prevElapsedTime, elapsedTime) -> 1F;

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
                            AnimationEvent.InTimeEvent.create(0.3F,(entitypatch, animation, params) -> {
                                LivingEntity original = entitypatch.getOriginal();
                                if(original.level() instanceof ClientLevel level) {
                                    entitypatch.playSound(SoundEvents.FIRECHARGE_USE,0,0);
                                    level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, original.getX(), original.getY() + 1, original.getZ(), 0, -0.15, 0);
                                    level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, original.getX() - 0.25, original.getY() + 1, original.getZ(), 0, -0.15, 0);
                                    level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, original.getX() - 0.25, original.getY() + 1, original.getZ() - 0.25, -0.15, 0, 0);
                                    level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, original.getX() + 0.25, original.getY() + 1, original.getZ(), 0, -0.15, 0);
                                    level.addAlwaysVisibleParticle(ParticleTypes.CLOUD, original.getX() + 0.25, original.getY() + 1, original.getZ() + 0.25, -0.15, 0, 0);
                                }


                                List<LivingEntity> entities = entitypatch.getOriginal().level()
                                        .getEntitiesOfClass(LivingEntity.class, entitypatch.getOriginal().getBoundingBox().inflate(12))
                                        .stream().filter(livingEntity -> livingEntity != entitypatch.getOriginal()).toList();

                                for (LivingEntity target : entities) {
                                    Vec3 pushStrength = target.position().subtract(entitypatch.getOriginal().position()).normalize().scale(target.getRandom().nextFloat() * 2 + 1);
                                    target.push(pushStrength.x,0,pushStrength.z);
                                }

                            }, AnimationEvent.Side.BOTH),
                            AnimationEvent.SimpleEvent.create((entitypatch, animation, params) -> {
                                if(!(entitypatch instanceof PlayerPatch<?>)){
                                    if(entitypatch.getTarget() != null){
                                        entitypatch.rotateTo(entitypatch.getTarget(), 360,true);
                                    }
                                }
                            }, AnimationEvent.Side.BOTH)
                    )
        );

        AIR_STRIKE_A = builder.nextAccessor("biped/air_strike_a", accessor->
                new AttackAnimation(0.1F,0.8F,0.1F,0.8F,10F,SHIELD,Armatures.BIPED.get().toolL,accessor,Armatures.BIPED)
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.UNBLOCKALBE))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,CONST_SPEED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL,true)
        );

        AIR_STRIKE_B = builder.nextAccessor("biped/air_strike_b", accessor->
                new AttackAnimation(0.1F,accessor,Armatures.BIPED,
                        new AttackAnimation.Phase(0F,0.8F,0.1F,0.7F,0.7F,0.7F,Armatures.BIPED.get().toolL,EMPTY)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)),
                        new AttackAnimation.Phase(0.7F,0.8F,0.7F,0.8F,10F,10F,Armatures.BIPED.get().rootJoint,BODY)
                                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.FINISHER,EpicFightDamageTypeTags.UNBLOCKALBE,EpicFightDamageTypeTags.BYPASS_DODGE))
                                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN))
                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
                        .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER,CONST_SPEED)
                        .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL,true)
                        .addEvents(AnimationEvent.InTimeEvent.create(0.8F, Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT)
                                .params(new Vec3f(0F, -0.25F, 0F), Armatures.BIPED.get().rootJoint, 6D, 1.0F))
        );
    }
}
