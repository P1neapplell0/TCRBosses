package com.p1nero.tcr_bosses.entity.battle_tower.sky_golem;

import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.asanginxst.epicfightx.gameassets.animations.AnimationsX;
import com.brass_amber.ba_bt.entity.hostile.golem.SkyGolem;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.google.common.collect.ImmutableMap;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNSwordAnimations;
import com.hm.efn.registries.EFNItem;
import com.legacy.lost_aether.registry.LCItems;
import com.mojang.datafixers.util.Pair;
import com.p1nero.tcr_bosses.mixins.AbstractGolemInvoker;
import net.kenddie.fantasyarmor.item.FAArmorItems;
import net.kenddie.fantasyarmor.item.armor.FAArmorSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.util.BehaviorUtils;
import net.shelmarow.combat_evolution.gameassets.ShieldCounterAnimations;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
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

public class SkyGolemPatch extends CEHumanoidPatch {

    public SkyGolemPatch() {
        super(Factions.UNDEAD);
    }

    @Override
    public void initAI() {
        super.initAI();
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

        ItemStack weapon = new ItemStack(EFNItem.SWORD_OF_PIONEER.get());
        ItemStack shield = new ItemStack(LCItems.shield_of_emile);

        ItemStack chest = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.HERO).get(ArmorItem.Type.CHESTPLATE).get());
        chest.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.CHEST);

        ItemStack legs = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.HERO).get(ArmorItem.Type.LEGGINGS).get());
        legs.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.LEGS);

        ItemStack feet = new ItemStack(FAArmorItems.ARMOR_ITEMS.get(FAArmorSet.HERO).get(ArmorItem.Type.BOOTS).get());
        feet.addAttributeModifier(Attributes.ARMOR, new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.ADDITION),EquipmentSlot.FEET);

        this.original.setItemInHand(InteractionHand.MAIN_HAND, weapon);
        this.original.setItemInHand(InteractionHand.OFF_HAND, shield);
        this.original.setItemSlot(EquipmentSlot.CHEST, chest);
        this.original.setItemSlot(EquipmentSlot.LEGS, legs);
        this.original.setItemSlot(EquipmentSlot.FEET, feet);


        modifyLivingMotionByCurrentItem();
    }


    public static void initAttributes(EntityAttributeModificationEvent event){
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.MAX_HEALTH, 700.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.ATTACK_DAMAGE, 4.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.ARMOR, 25.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), Attributes.ARMOR_TOUGHNESS, 15.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.IMPACT.get(), 0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.STUN_ARMOR.get(), 25.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.MAX_STAMINA.get(), 120.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.STAMINA_REGEN.get(), 1.0D);
        event.add(BTEntityType.SKY_GOLEM.get(), EpicFightAttributes.MAX_STRIKES.get(), 100.0D);
    }

    @Override
    protected void setWeaponMotions() {
        this.weaponLivingMotions.put(CapabilityItem.WeaponCategories.UCHIGATANA,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND, Set.of(
                        Pair.of(LivingMotions.BLOCK, AnimationsX.BIPED_BLOCK),
                        Pair.of(LivingMotions.IDLE, EFNSwordAnimations.NF_SWORD_IDLE),
                        Pair.of(LivingMotions.WALK, EFNSwordAnimations.NF_SWORD_WALK),
                        Pair.of(LivingMotions.RUN, EFNSwordAnimations.NF_SWORD_RUN),
                        Pair.of(LivingMotions.CHASE, EFNSwordAnimations.NF_SWORD_RUN)
                )));

        this.guardHitMotions.put(CapabilityItem.WeaponCategories.UCHIGATANA,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND,
                        List.of(AnimationsX.BIPED_HIT_SHIELD)));

        this.weaponAttackMotions.put(CapabilityItem.WeaponCategories.UCHIGATANA,
                ImmutableMap.of(CapabilityItem.Styles.ONE_HAND, SkyGolemCombatBehavior.SWORD));
    }


    @Override
    public void playGuardBreakSound(){
        this.playSound(EpicFightSounds.NEUTRALIZE_BOSSES.get(), 0F,0F);
    }


    @Override
    public void playGuardHitAnimation(DamageSource damageSource, boolean canCounter){
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

    @Override
    public void onAttackParried(DamageSource damageSource, LivingEntityPatch<?> blocker) {
        CECombatBehaviors.Behavior<?> currentBehavior = BehaviorUtils.getCurrentBehavior(this);
        float staminaRatio = 0.015F;
        if(currentBehavior != null && !currentBehavior.getBehaviorName().equals("sheath_attack")) {
            if(currentBehavior.getBehaviorName().equals("振飞尾端下劈")){
                playAnimationSynchronized(ShieldCounterAnimations.COUNTERED, 0F);
                staminaRatio = 0.15F;
            }
            dealStaminaDamage(null, (float) (original.getAttributeValue(EpicFightAttributes.MAX_STAMINA.get()) * staminaRatio));
        }
    }

    @Override
    public AttackResult tryHurt(DamageSource damageSource, float amount) {
        AttackResult result = super.tryHurt(damageSource, amount);
        if(!damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && getEntityState().getLevel() != 3) {
            CECombatBehaviors.Behavior<?> current = BehaviorUtils.getCurrentBehavior(this);
            if(current != null) {
                String name = current.getBehaviorName();
                if (name != null && name.equals("sheath_attack")) {
                    playSound(EpicFightSounds.CLASH.get(), 0,0);
                    return AttackResult.missed(0);
                }
            }
        }
        return result;
    }

    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        float scale = this.original.getScale()*1.25F;
        return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
    }

    @Override
    public boolean applyStun(StunType stunType, float time){
        time = Mth.clamp(time,0,0.1F);
        return super.applyStun(stunType, time);
    }

    @Override
    public void onDeath(LivingDeathEvent event) {
        super.onDeath(event);
        handleNearByBlock( 20, true);
    }

    public void handleNearByBlock(int r, boolean invisible) {
        Level level = this.original.level();
        SkyGolem skyGolem = ((SkyGolem) this.original);
        BlockPos center = skyGolem.getEntityData().get(((AbstractGolemInvoker)skyGolem).getSpawnPosKey());
        for(BlockPos pos : BlockPos.betweenClosed(center.getX() - r, center.getY() -r,  center.getZ() - r,center.getX() + r, center.getY() + r, center.getZ() + r)) {
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof DoorwayBlock) {
                level.setBlockAndUpdate(pos, state.setValue(DoorwayBlock.INVISIBLE, invisible));
            }
        }
    }

}
