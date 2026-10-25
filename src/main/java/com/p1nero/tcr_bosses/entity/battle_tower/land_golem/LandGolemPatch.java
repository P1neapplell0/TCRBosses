package com.p1nero.tcr_bosses.entity.battle_tower.land_golem;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.brass_amber.ba_bt.entity.ai.goal.LandGolemFireballAttackGoal;
import com.brass_amber.ba_bt.entity.ai.goal.GolemStompAttackGoal;
import com.brass_amber.ba_bt.entity.hostile.golem.LandGolem;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.registries.EFNItem;
import com.mojang.datafixers.util.Pair;
import com.p1nero.tcr_bosses.entity.battle_tower.GolemPatch;
import net.kenddie.fantasyarmor.item.FAArmorItems;
import net.kenddie.fantasyarmor.item.armor.FAArmorSet;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.*;

public class LandGolemPatch extends GolemPatch<LandGolem> {

    private boolean gpSucceed = false;

    public LandGolemPatch() {
        super(Factions.UNDEAD);
    }

    public static void initAttributes(EntityAttributeModificationEvent event) {
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.MAX_HEALTH, 750.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ATTACK_DAMAGE, 0.5D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ATTACK_SPEED, 0.5D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ARMOR, 5.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 10.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 2.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.ARMOR_NEGATION.get(), 0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.STUN_ARMOR.get(), 50.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 30.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.original.setDropChance(EquipmentSlot.MAINHAND, 0);
        this.original.setDropChance(EquipmentSlot.OFFHAND, 0);
        this.original.setDropChance(EquipmentSlot.HEAD, 0);
        this.original.setDropChance(EquipmentSlot.CHEST, 0);
        this.original.setDropChance(EquipmentSlot.LEGS, 0);
        this.original.setDropChance(EquipmentSlot.FEET, 0);

        ItemStack weapon = new ItemStack(EFNItem.RUINSGREATSWORD.get());
        weapon.addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier("attack_damage", 3F, AttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);
        weapon.addAttributeModifier(Attributes.ATTACK_SPEED, new AttributeModifier("attack_speed", 0.5F, AttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        ItemStack chest = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.DRAGONSLAYER).get(ArmorItem.Type.CHESTPLATE).get());
        chest.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.CHEST);

        ItemStack legs = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.DRAGONSLAYER).get(ArmorItem.Type.LEGGINGS).get());
        legs.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.LEGS);


        ItemStack feet = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.DRAGONSLAYER).get(ArmorItem.Type.BOOTS).get());
        feet.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.FEET);

        this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        this.original.setItemSlot(EquipmentSlot.CHEST, chest);
        this.original.setItemSlot(EquipmentSlot.LEGS, legs);
        this.original.setItemSlot(EquipmentSlot.FEET, feet);

        modifyLivingMotionByCurrentItem(true);
    }

    @Override
    public void onDeath(LivingDeathEvent event) {
        playAnimationSynchronized(animator.getLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH), 0F);
        this.currentLivingMotion = LivingMotions.DEATH;
    }

    @Override
    public void tick(LivingEvent.LivingTickEvent event) {
        super.tick(event);
        LandGolem btLandGolem = original;
        if (btLandGolem.isEnraged() && !btLandGolem.hasEffect(CEMobEffects.FULL_STUN_IMMUNITY.get())) {
            btLandGolem.addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(), 100, 0));
        }
    }

    @Override
    public void playGuardHitAnimation(DamageSource damageSource, boolean canCounter) {
        AnimationManager.AnimationAccessor<? extends StaticAnimation> guardHit;
        if (canCounter) {
            guardHit = original.tickCount % 2 == 0 ? EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1 : EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2;
            this.playSound(EFNSounds.PARRY.get(), 0.5F, 1.0F, 1.0F);
        } else {
            guardHit = this.getGuardHitAnimation(damageSource);
            this.playGuardHitSound();
        }
        this.playAnimationSynchronized(guardHit, 0.0F, SPAnimatorControl::new);
    }

    @Override
    public float getHurtImpactPercent(DamageSource damageSource) {
        return 0.5F;
    }


    @Override
    public AttackResult tryHurt(DamageSource damageSource, float amount) {
        AttackResult result = super.tryHurt(damageSource, amount);
        if (!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && getEntityState().getLevel() >= 0 && getEntityState().getLevel() <= 2) {
            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(this);
            if (current != null) {
                String name = current.getBehaviorName();
                if (name != null && name.equals("GP")) {
                    setGpSucceed(true);
                    playSound(EpicFightSounds.CLASH.get(), 0, 0);
                    return AttackResult.missed(0);
                }
            }
        }
        return result;
    }

    public boolean isGpSucceed() {
        return gpSucceed;
    }

    public void setGpSucceed(boolean gpSucceed) {
        this.gpSucceed = gpSucceed;
    }

    @Override
    public void initAI() {
        super.initAI();
        this.getOriginal().goalSelector.removeAllGoals(goal -> goal instanceof GolemStompAttackGoal || goal instanceof LandGolemFireballAttackGoal);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.GREATSWORD,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, AnimationsX.GREATSWORD_GUARD),
                        Pair.of(LivingMotions.IDLE, EFNGreatSwordAnimations.NG_GREATSWORD_IDLE),
                        Pair.of(LivingMotions.WALK, EFNGreatSwordAnimations.NG_GREATSWOED_WALK),
                        Pair.of(LivingMotions.RUN, EFNGreatSwordAnimations.NG_GREATSWORD_RUN),
                        Pair.of(LivingMotions.CHASE, EFNGreatSwordAnimations.NG_GREATSWORD_RUN)
                )));

        this.guardHitMotions.put(CapabilityItem.WeaponCategories.GREATSWORD,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, List.of(
                        AnimationsX.GREATSWORD_GUARD_HIT
                )));

        this.weaponAttackMotions.put(CapabilityItem.WeaponCategories.GREATSWORD,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, LandGolemCombatBehavior.GREATSWORD.get()));
    }

    @Override
    public void playGuardBreakSound() {
        this.playSound(EpicFightSounds.NEUTRALIZE_BOSSES.get(), 0F, 0F);
    }


    @Override
    public void onAttackParried(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        CECombatBehaviors.Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(this);
        if (currentBehavior != null) {
            String name = currentBehavior.getBehaviorName();
            if (name == null || !name.equals("P2")) {
                dealStaminaDamage(null, (float) (original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()) * 0.025F));
            }
        }
    }


    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = this.original.getScale() * 1.25F;
        return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
    }

    @Override
    public boolean applyStun(StunType stunType, float time) {
        time = Mth.clamp(time, 0, 0.25F);
        return super.applyStun(stunType, time);
    }

}
