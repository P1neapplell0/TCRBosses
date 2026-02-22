package com.p1nero.tcr_bosses.entity.battle_tower.ocean_golem;

import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.mojang.datafixers.util.Pair;
import net.kenddie.fantasyarmor.item.FAArmorItems;
import net.kenddie.fantasyarmor.item.armor.FAArmorSet;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OceanGolemPatch extends CEHumanoidPatch {

    public OceanGolemPatch() {
        super(Factions.UNDEAD);
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

        ItemStack weapon = new ItemStack(Items.TRIDENT);
        weapon.addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier("attack_damage",  3F, AttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        ItemStack head = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.OLD_KNIGHT).get(ArmorItem.Type.HELMET).get());
        head.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.HEAD);

        ItemStack chest = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.DARK_LORD).get(ArmorItem.Type.CHESTPLATE).get());
        chest.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.CHEST);

        ItemStack legs = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.DARK_LORD).get(ArmorItem.Type.LEGGINGS).get());
        legs.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.LEGS);

        ItemStack feet = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.DARK_LORD).get(ArmorItem.Type.BOOTS).get());
        feet.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", 0, AttributeModifier.Operation.ADDITION), EquipmentSlot.FEET);

        this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        this.original.setItemSlot(EquipmentSlot.HEAD, head);
        this.original.setItemSlot(EquipmentSlot.CHEST, chest);
        this.original.setItemSlot(EquipmentSlot.LEGS, legs);
        this.original.setItemSlot(EquipmentSlot.FEET, feet);

        modifyLivingMotionByCurrentItem();
    }


    public static void initAttributes(EntityAttributeModificationEvent event) {
        event.add(BTEntityType.OCEAN_GOLEM.get(), Attributes.MAX_HEALTH, 550.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), Attributes.MOVEMENT_SPEED, 0.3D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), Attributes.ATTACK_DAMAGE, 1.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), Attributes.ARMOR, 5.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 15.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 1.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), EpicFightAttributes.STUN_ARMOR.get(), 25.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 45.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.OCEAN_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.TRIDENT,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, AnimationsX.SPEAR_GUARD),
                        Pair.of(LivingMotions.IDLE, EFNSwordAnimations.NF_SWORD_IDLE),
                        Pair.of(LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK),
                        Pair.of(LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN),
                        Pair.of(LivingMotions.CHASE, EFNSwordAnimations.NF_SWORD_RUN)
                )));

        this.guardHitMotions.put(CapabilityItem.WeaponCategories.TRIDENT,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND, List.of(AnimationsX.SPEAR_GUARD_HIT)));

    }

    @Override
    protected CECombatBehaviors.Builder<MobPatch<?>> getCustomWeaponMotionBuilder() {
        return OceanGolemCombatBehavior.TRIDENT;
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
    public void playGuardBreakSound() {
        this.playSound(EpicFightSounds.NEUTRALIZE_BOSSES.get(), 0F, 0F);
    }


    @Override
    public void onAttackParried(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        CECombatBehaviors.Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(this);
        if (currentBehavior != null) {
            dealStaminaDamage(null, (float) (original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()) * 0.05F));
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
