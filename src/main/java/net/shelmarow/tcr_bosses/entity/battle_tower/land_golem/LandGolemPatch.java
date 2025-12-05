package net.shelmarow.tcr_bosses.entity.battle_tower.land_golem;

import com.brass_amber.ba_bt.entity.ai.goal.GolemFireballAttackGoal;
import com.brass_amber.ba_bt.entity.ai.goal.GolemStompAttackGoal;
import com.brass_amber.ba_bt.entity.hostile.golem.BTLandGolem;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.registries.EFNItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.shelmarow.combat_evolution.effect.CEMobEffects;
import net.shelmarow.combat_evolution.iml.ILivingEntityData;
import net.shelmarow.tcr_bosses.mixins.BTAbstractGolemAccessor;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.AttackResult;
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

public class LandGolemPatch extends CEHumanoidPatch {

    private boolean gpSucceed = false;
    private ServerBossEvent serverBossEvent;

    public LandGolemPatch() {
        super(Factions.UNDEAD);
    }

    public static void initAttributes(EntityAttributeModificationEvent event){
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.MAX_HEALTH, 280.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ATTACK_DAMAGE, 4.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ARMOR, 10.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 10.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 2.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.ARMOR_NEGATION.get(), 30.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.STUN_ARMOR.get(), 50.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 50.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.LAND_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    public void onAddedToWorld(){

        //SB高塔傀儡，让我的耐力不同步，这里需要手动矫正
        ILivingEntityData entityData = (ILivingEntityData) this;
        entityData.combat_evolution$setStamina(original, (float)original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()));
        entityData.combat_evolution$setStaminaStatus(original, StaminaStatus.COMMON);

        if (original instanceof BTAbstractGolemAccessor accessor) {
            serverBossEvent = accessor.getBossEvent();
        }

        if (this.original.getMainHandItem().isEmpty()) {
            this.original.setDropChance(EquipmentSlot.MAINHAND, 0);
            ItemStack weapon = new ItemStack(EFNItem.RUINSGREATSWORD.get());
            weapon.addAttributeModifier(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier("attack_damage", -0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL),
                    EquipmentSlot.MAINHAND
            );
            this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
            modifyLivingMotionByCurrentItem();
        }
    }

    @Override
    public void tick(LivingEvent.LivingTickEvent event){
        super.tick(event);

        if(serverBossEvent != null){
            //同步耐力值
            if(this.original instanceof BTLandGolem entity && entity.isAlive()){
                double maxStamina = entity.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get());
                ILivingEntityData entityData = (ILivingEntityData) this;

                ClientBossData.partialUpdate(serverBossEvent.getId(),data->{
                    data.displayType = Component.literal("land_golem");
                    data.staminaO = ClientBossData.getStaminaProgress(data);
                    data.stamina = (float) (entityData.combat_evolution$getStamina(original)/maxStamina);
                    data.staminaSetTime = Util.getMillis();
                });
            }
        }

        if(original instanceof BTLandGolem btLandGolem){
            if(btLandGolem.isEnraged() && !btLandGolem.hasEffect(CEMobEffects.FULL_STUN_IMMUNITY.get())){
                btLandGolem.addEffect(new MobEffectInstance(CEMobEffects.FULL_STUN_IMMUNITY.get(), 100, 0));
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
    public AttackResult tryHurt(DamageSource damageSource, float amount) {
        AttackResult result = super.tryHurt(damageSource, amount);
        if(!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && getEntityState().getLevel() >= 0 && getEntityState().getLevel() <= 2) {
            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(this);
            if(current != null) {
                String name = current.getBehaviorName();
                if (name != null && name.equals("GP")) {
                    setGpSucceed(true);
                    playSound(EpicFightSounds.CLASH.get(), 0,0);
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
        this.getOriginal().goalSelector.removeAllGoals(goal -> goal instanceof GolemStompAttackGoal || goal instanceof GolemFireballAttackGoal);
        for(WrappedGoal goal : original.targetSelector.getAvailableGoals()){
            goal.start();
        }
    }

    @Override
    public void initAnimator(Animator animator) {
        super.initAnimator(animator);
        animator.addLivingAnimation(LivingMotions.BLOCK, Animations.GREATSWORD_GUARD);
        animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
        animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_RUN);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.GREATSWORD,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, Animations.GREATSWORD_GUARD),
                        Pair.of(LivingMotions.IDLE, EFNGreatSwordAnimations.NG_GREATSWORD_IDLE),
                        Pair.of(LivingMotions.WALK, EFNGreatSwordAnimations.NG_GREATSWOED_WALK),
                        Pair.of(LivingMotions.RUN, EFNGreatSwordAnimations.NG_GREATSWORD_RUN),
                        Pair.of(LivingMotions.CHASE, EFNGreatSwordAnimations.NG_GREATSWORD_RUN)
                )));

        this.guardHitMotions.put(CapabilityItem.WeaponCategories.GREATSWORD,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND,
                        List.of(
                                Animations.GREATSWORD_GUARD_HIT
                        )));

        this.weaponAttackMotions.put(CapabilityItem.WeaponCategories.GREATSWORD,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, LandGolemCombatBehavior.GREATSWORD));
    }

    @Override
    public void playGuardBreakSound(){
        this.playSound(EpicFightSounds.NEUTRALIZE_BOSSES.get(), 0F,0F);
    }


    @Override
    public void onAttackParried(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        CECombatBehaviors.Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(this);
        if(currentBehavior != null) {
            String name = currentBehavior.getBehaviorName();
            if(name == null || !name.equals("P2")) {
                dealStaminaDamage(null, (float) (original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()) * 0.025F));
            }
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
