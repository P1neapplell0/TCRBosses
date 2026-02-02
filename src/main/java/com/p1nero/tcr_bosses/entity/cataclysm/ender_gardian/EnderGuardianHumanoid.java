package com.p1nero.tcr_bosses.entity.cataclysm.ender_gardian;

import com.github.L_Ender.cataclysm.init.ModSounds;
import com.p1nero.tcr_bosses.entity.cataclysm.BaseBossEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class EnderGuardianHumanoid extends BaseBossEntity {

    public EnderGuardianHumanoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
        return ModSounds.ENDERGUARDIAN_MUSIC_2.get();
    }

}
