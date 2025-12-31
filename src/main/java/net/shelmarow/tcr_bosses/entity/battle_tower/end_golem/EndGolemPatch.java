package net.shelmarow.tcr_bosses.entity.battle_tower.end_golem;

import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.gameasset.EFNWeaponCategories;
import com.hm.efn.gameasset.animations.EFNDodgeAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.registries.EFNItem;
import com.mojang.datafixers.util.Pair;
import net.kenddie.fantasyarmor.item.FAArmorItems;
import net.kenddie.fantasyarmor.item.armor.FAArmorSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EndGolemPatch extends CEHumanoidPatch {

    private int bossBreakPhase = 0;

    public EndGolemPatch() {
        super(Factions.UNDEAD);
        breakTime = 15 * 20;
    }

    @Override
    public void onAddedToWorld() {
        //重置耐力
        AttributeInstance instance = original.getAttribute(EpicFightAttributes.MAX_STAMINA.get());
        if(instance != null) {
            double maxStamina = original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get());
            instance.setBaseValue(Mth.clamp(maxStamina + 15D * bossBreakPhase,30,90));
        }

        super.onAddedToWorld();

        this.original.setDropChance(EquipmentSlot.MAINHAND, 0);
        this.original.setDropChance(EquipmentSlot.OFFHAND, 0);
        this.original.setDropChance(EquipmentSlot.HEAD, 0);
        this.original.setDropChance(EquipmentSlot.CHEST, 0);
        this.original.setDropChance(EquipmentSlot.LEGS, 0);
        this.original.setDropChance(EquipmentSlot.FEET, 0);

        ItemStack weapon = new ItemStack(EFNItem.YAMATO_DMC_IN_SHEATH.get());

        ItemStack chest = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.TWINNED).get(ArmorItem.Type.CHESTPLATE).get());
        chest.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.CHEST);

        ItemStack legs = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.TWINNED).get(ArmorItem.Type.LEGGINGS).get());
        legs.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.LEGS);


        ItemStack feet = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.TWINNED).get(ArmorItem.Type.BOOTS).get());
        feet.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.FEET);

        this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        this.original.setItemSlot(EquipmentSlot.CHEST, chest);
        this.original.setItemSlot(EquipmentSlot.LEGS, legs);
        this.original.setItemSlot(EquipmentSlot.FEET, feet);

        modifyLivingMotionByCurrentItem();
    }

    public static void initAttributes(EntityAttributeModificationEvent event){
        event.add(BTEntityType.END_GOLEM.get(), Attributes.MAX_HEALTH, 800.0D);
        event.add(BTEntityType.END_GOLEM.get(), Attributes.MOVEMENT_SPEED, 0.3D);
        event.add(BTEntityType.END_GOLEM.get(), Attributes.ATTACK_DAMAGE, 3.0D);
        event.add(BTEntityType.END_GOLEM.get(), Attributes.ARMOR, 30.0D);
        event.add(BTEntityType.END_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 20.0D);
        event.add(BTEntityType.END_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 1.0D);
        event.add(BTEntityType.END_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 15.0D);
        event.add(BTEntityType.END_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.END_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(EFNWeaponCategories.EFN_YAMATO,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, Animations.SWORD_GUARD),
                        Pair.of(LivingMotions.IDLE, EFNYamatoAnimations.YAMATO_IDLE),
                        Pair.of(LivingMotions.WALK, EFNYamatoAnimations.YAMATO_WALK),
                        Pair.of(LivingMotions.RUN, EFNYamatoAnimations.YAMATO_RUN),
                        Pair.of(LivingMotions.CHASE, EFNYamatoAnimations.YAMATO_RUN)
                )));

        this.guardHitMotions.put(EFNWeaponCategories.EFN_YAMATO,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, List.of(
                        Animations.SWORD_GUARD_ACTIVE_HIT1,
                        Animations.SWORD_GUARD_ACTIVE_HIT2,
                        Animations.SWORD_GUARD_ACTIVE_HIT3
                )));

        this.weaponAttackMotions.put(EFNWeaponCategories.EFN_YAMATO,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, EndGolemCombatBehavior.YAMATO));
    }


    @Override
    public void tick(LivingEvent.LivingTickEvent event) {
        super.tick(event);

        //硬直脱离
        if(!isLogicalClient() && original.isAlive() && this.getEntityState().hurtLevel() > 0 && CEPatchUtils.getStaminaStatus(this) != StaminaStatus.BREAK){
            this.playAnimationSynchronized(EFNDodgeAnimations.YAMATO_STEP_B,0,SPAnimatorControl::new);
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1,0,0);
        }
    }

    public void setBossBreakPhase(int phase){
        this.bossBreakPhase = phase;
    }

    public int getBossBreakPhase() {
        return bossBreakPhase;
    }

    @Override
    public float getHurtImpactPercent(DamageSource damageSource) {
        return 1F;
    }

    @Override
    public void onBreak(DamageSource damageSource) {
        bossBreakPhase++;
        AttributeInstance instance = original.getAttribute(EpicFightAttributes.MAX_STAMINA.get());
        if(instance != null) {
            instance.setBaseValue(Mth.clamp(15D + 45D * bossBreakPhase,10,150));
        }

        super.onBreak(damageSource);
    }

    @Override
    public void playGuardBreakSound(){
        this.playSound(EpicFightSounds.NEUTRALIZE_BOSSES.get(), 0F,0F);
    }


    @Override
    public void onAttackParried(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        CECombatBehaviors.Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(this);
        if(currentBehavior != null) {
            dealStaminaDamage(null, (float) (original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()) * 0.035F));
        }
    }


    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = this.original.getScale()*1.25F;
        return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
    }

    @Override
    public boolean applyStun(StunType stunType, float time){
        if(stunType == StunType.NEUTRALIZE || CEPatchUtils.getStaminaStatus(this) == StaminaStatus.BREAK) {
            if(time <= 0.5F && stunType == StunType.SHORT){
                stunType = StunType.LONG;
            }
            return super.applyStun(stunType, time);
        }
        return false;
    }
}
