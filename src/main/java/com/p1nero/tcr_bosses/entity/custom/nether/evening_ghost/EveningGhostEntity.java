package com.p1nero.tcr_bosses.entity.custom.nether.evening_ghost;

import com.brass_amber.ba_bt.sound.BTSoundEvents;
import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.kenddie.fantasyarmor.item.FAArmorItems;
import net.kenddie.fantasyarmor.item.armor.FAArmorSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class EveningGhostEntity extends BaseSmallBossEntity {

    public EveningGhostEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setItemSlot(EquipmentSlot.HEAD, FAArmorItems.getArmorItem(FAArmorSet.EVENING_GHOST, ArmorItem.Type.HELMET).get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.CHEST, FAArmorItems.getArmorItem(FAArmorSet.EVENING_GHOST, ArmorItem.Type.CHESTPLATE).get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.LEGS, FAArmorItems.getArmorItem(FAArmorSet.EVENING_GHOST, ArmorItem.Type.LEGGINGS).get().getDefaultInstance());
        this.setItemSlot(EquipmentSlot.FEET, FAArmorItems.getArmorItem(FAArmorSet.EVENING_GHOST, ArmorItem.Type.BOOTS).get().getDefaultInstance());
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(EpicFightAttributes.IMPACT.get(),1.0D)
                .add(EpicFightAttributes.MAX_STAMINA.get(),15.0D)
                .add(EpicFightAttributes.STAMINA_REGEN.get(),1.0D)
                .build();
    }

    @Override
    public SoundEvent getBGM() {
        return BTSoundEvents.MUSIC_CORE_GOLEM_FIGHT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundRegistry.FIRE_BOSS_DEATH.get();
    }

}
