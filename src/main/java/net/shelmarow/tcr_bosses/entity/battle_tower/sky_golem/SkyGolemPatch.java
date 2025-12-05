package net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem;

import com.aetherteam.aether.item.AetherItems;
import com.brass_amber.ba_bt.entity.hostile.golem.BTSkyGolem;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.legacy.lost_aether.registry.LCItems;
import com.magistuarmory.item.ModItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.shelmarow.combat_evolution.ai.BehaviorUtils;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.client.bossbar.ClientBossData;
import net.shelmarow.combat_evolution.iml.ILivingEntityData;
import net.shelmarow.tcr_bosses.mixins.BTAbstractGolemAccessor;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.List;
import java.util.Set;

public class SkyGolemPatch extends CEHumanoidPatch {

    private ServerBossEvent serverBossEvent;

    public SkyGolemPatch() {
        super(Factions.UNDEAD);
    }

    @Override
    public void initAI() {
        super.initAI();
    }

    @Override
    public void onAddedToWorld() {

        ILivingEntityData entityData = (ILivingEntityData) this;
        entityData.combat_evolution$setStamina(original, (float)original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()));
        entityData.combat_evolution$setStaminaStatus(original, StaminaStatus.COMMON);

        if (original instanceof BTAbstractGolemAccessor accessor) {
            serverBossEvent = accessor.getBossEvent();
        }

        this.original.setDropChance(EquipmentSlot.MAINHAND, 0);
        this.original.setDropChance(EquipmentSlot.OFFHAND, 0);
        this.original.setDropChance(EquipmentSlot.HEAD, 0);
        this.original.setDropChance(EquipmentSlot.CHEST, 0);
        this.original.setDropChance(EquipmentSlot.LEGS, 0);
        this.original.setDropChance(EquipmentSlot.FEET, 0);

        ItemStack weapon = new ItemStack(AetherItems.HOLY_SWORD.get());
        ItemStack shield = new ItemStack(LCItems.shield_of_emile);
        this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        this.original.setItemInHand(InteractionHand.OFF_HAND, shield);
        if (ModItems.WINGED_HUSSAR_CHESTPLATE != null) {
            this.original.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.WINGED_HUSSAR_CHESTPLATE.get()));
        }

        modifyLivingMotionByCurrentItem();
    }


    public static void initAttributes(EntityAttributeModificationEvent event){
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.MAX_HEALTH, 200.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.ATTACK_DAMAGE, 1.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.ARMOR, 5.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 5.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 2.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.STUN_ARMOR.get(), 25.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 40.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.SWORD,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, Animations.BIPED_BLOCK),
                        Pair.of(LivingMotions.IDLE, EFNSwordAnimations.NF_SWORD_IDLE),
                        Pair.of(LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK),
                        Pair.of(LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN),
                        Pair.of(LivingMotions.CHASE, EFNSwordAnimations.NF_SWORD_RUN)
                )));

        this.guardHitMotions.put(CapabilityItem.WeaponCategories.SWORD,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND,
                        List.of(Animations.BIPED_HIT_SHIELD )));

        this.weaponAttackMotions.put(CapabilityItem.WeaponCategories.SWORD,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND, SkyGolemCombatBehavior.SWORD));
    }

    @Override
    public void tick(LivingEvent.LivingTickEvent event) {
        super.tick(event);

        if (serverBossEvent != null) {
            //同步耐力值
            if (this.original instanceof BTSkyGolem entity && entity.isAlive()) {
                double maxStamina = entity.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get());
                ILivingEntityData entityData = (ILivingEntityData) this;

                ClientBossData.partialUpdate(serverBossEvent.getId(), data->{
                    data.displayType = Component.literal("sky_golem");
                    data.staminaO = ClientBossData.getStaminaProgress(data);
                    data.stamina = (float) (entityData.combat_evolution$getStamina(original)/maxStamina);
                    data.staminaSetTime = Util.getMillis();
                });
            }
        }
    }

    @Override
    public void onDeath(LivingDeathEvent event) {
        super.onDeath(event);
        ClientBossData.removeBoss(serverBossEvent.getId());
        serverBossEvent.setVisible(false);
    }


    @Override
    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.BLOCK, Animations.SWORD_GUARD);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
    }

    @Override
    public void playGuardBreakSound(){
        this.playSound(EpicFightSounds.NEUTRALIZE_BOSSES.get(), 0F,0F);
    }


    @Override
    public void onAttackParried(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        CECombatBehaviors.Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(this);
        if(currentBehavior != null) {
            dealStaminaDamage(null, (float) (original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()) * 0.025F));
        }
    }


    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = this.original.getScale()*1.25F;
        return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
    }

    @Override
    public boolean applyStun(StunType stunType, float time){
        time = Mth.clamp(time,0,0.25F);
        return super.applyStun(stunType, time);
    }

}
