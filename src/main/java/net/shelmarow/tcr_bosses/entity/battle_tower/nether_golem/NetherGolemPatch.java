package net.shelmarow.tcr_bosses.entity.battle_tower.nether_golem;

import com.brass_amber.ba_bt.entity.hostile.golem.BTNetherGolem;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.gameasset.animations.EFNTachiAnimations;
import com.hm.efn.registries.EFNItem;
import com.mojang.datafixers.util.Pair;
import net.kenddie.fantasyarmor.item.FAArmorItems;
import net.kenddie.fantasyarmor.item.armor.FAArmorSet;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
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
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import net.shelmarow.combat_evolution.client.bossbar.ClientBossData;
import net.shelmarow.combat_evolution.iml.ILivingEntityData;
import net.shelmarow.tcr_bosses.mixins.BTAbstractGolemAccessor;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
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

public class NetherGolemPatch extends CEHumanoidPatch {

    private ServerBossEvent serverBossEvent;

    public NetherGolemPatch() {
        super(Factions.UNDEAD);
    }

    @Override
    public void onAddedToWorld() {

        ILivingEntityData entityData = (ILivingEntityData) this;
        entityData.combat_evolution$setStamina((float)original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()));
        entityData.combat_evolution$setStaminaStatus(StaminaStatus.COMMON);

        if (original instanceof BTAbstractGolemAccessor accessor) {
            serverBossEvent = accessor.getBossEvent();
        }

        this.original.setDropChance(EquipmentSlot.MAINHAND, 0);
        this.original.setDropChance(EquipmentSlot.OFFHAND, 0);
        this.original.setDropChance(EquipmentSlot.HEAD, 0);
        this.original.setDropChance(EquipmentSlot.CHEST, 0);
        this.original.setDropChance(EquipmentSlot.LEGS, 0);
        this.original.setDropChance(EquipmentSlot.FEET, 0);

        ItemStack weapon = new ItemStack(EFNItem.CO_TACHI.get());
        weapon.addAttributeModifier(Attributes.ATTACK_DAMAGE,new AttributeModifier("attack_damage", 6F, AttributeModifier.Operation.ADDITION),EquipmentSlot.MAINHAND);

        ItemStack head = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.RONIN).get(ArmorItem.Type.HELMET).get());
        head.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.HEAD);

        ItemStack chest = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.RONIN).get(ArmorItem.Type.CHESTPLATE).get());
        chest.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.CHEST);

        ItemStack legs = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.RONIN).get(ArmorItem.Type.LEGGINGS).get());
        legs.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.LEGS);

        ItemStack feet = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.RONIN).get(ArmorItem.Type.BOOTS).get());
        feet.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.FEET);

        this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        this.original.setItemSlot(EquipmentSlot.HEAD, head);
        this.original.setItemSlot(EquipmentSlot.CHEST, chest);
        this.original.setItemSlot(EquipmentSlot.LEGS, legs);
        this.original.setItemSlot(EquipmentSlot.FEET, feet);

        modifyLivingMotionByCurrentItem();
    }

    @Override
    public void playGuardHitAnimation(DamageSource damageSource,boolean canCounter){
        AnimationManager.AnimationAccessor<? extends StaticAnimation> guardHit;
        if(canCounter){
            guardHit = original.tickCount % 2 == 0 ? EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1 : EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2;
            this.playSound(EFNSounds.PARRY.get(),0.5F, 1.0F, 1.0F);
        }
        else{
            guardHit = this.getGuardHitAnimation(damageSource);
            this.playGuardHitSound();
        }
        this.playAnimationSynchronized(guardHit, 0.0F, SPAnimatorControl::new);
    }


    public static void initAttributes(EntityAttributeModificationEvent event){
        event.add(BTEntityType.NETHER_GOLEM.get(), Attributes.MAX_HEALTH, 480.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), Attributes.ATTACK_DAMAGE, 2.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), Attributes.ARMOR, 20.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 15.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 1D);
        event.add(BTEntityType.NETHER_GOLEM.get(), EpicFightAttributes.STUN_ARMOR.get(), 30.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 60.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.NETHER_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.TACHI,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, EFNSwordAnimations.NF_SWORD_GUARD),
                        Pair.of(LivingMotions.IDLE, EFNTachiAnimations.NF_TACHI_IDLE),
                        Pair.of(LivingMotions.WALK, EFNTachiAnimations.NF_TACHI_WALK),
                        Pair.of(LivingMotions.RUN, EFNTachiAnimations.NF_TACHI_RUN),
                        Pair.of(LivingMotions.CHASE, EFNTachiAnimations.NF_TACHI_RUN)
                )));

        this.guardHitMotions.put(CapabilityItem.WeaponCategories.TACHI,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, List.of(
                        Animations.SWORD_GUARD_ACTIVE_HIT1,
                        Animations.SWORD_GUARD_ACTIVE_HIT2,
                        Animations.SWORD_GUARD_ACTIVE_HIT3
                )));

        this.weaponAttackMotions.put(
                CapabilityItem.WeaponCategories.TACHI,
                ImmutableMap.of(CapabilityItem.Styles.TWO_HAND, NetherGolemCombatBehavior.TACHI)
        );
    }

    @Override
    public void tick(LivingEvent.LivingTickEvent event) {
        super.tick(event);


        if (serverBossEvent != null) {
            //同步耐力值
            if (this.original instanceof BTNetherGolem entity && entity.isAlive()) {
                double maxStamina = entity.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get());
                ILivingEntityData entityData = (ILivingEntityData) this;

                ClientBossData.partialUpdate(serverBossEvent.getId(), data->{
                    data.displayType = Component.literal("nether_golem");
                    data.staminaO = ClientBossData.getStaminaProgress(data);
                    data.stamina = (float) (entityData.combat_evolution$getStamina()/maxStamina);
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
