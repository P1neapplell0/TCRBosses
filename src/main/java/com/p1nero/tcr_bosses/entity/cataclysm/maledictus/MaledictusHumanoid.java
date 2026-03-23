package com.p1nero.tcr_bosses.entity.cataclysm.maledictus;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModSounds;
import com.p1nero.tcr_bosses.entity.cataclysm.BaseBossEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class MaledictusHumanoid extends BaseBossEntity {


    public MaledictusHumanoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
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
    public boolean hurt(@NotNull DamageSource source, float pAmount) {
        if(this.shouldNotDie() && pAmount > this.getHealth()) {
            if(this.getHealth() < this.getMaxHealth() * 0.05) {
                return false;
            }
            return super.hurt(source, pAmount / 2);
        }
        return super.hurt(source, pAmount);
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        if(this.shouldNotDie()) {
            return;
        }
        super.die(pDamageSource);
    }

    protected boolean shouldNotDie() {
        return !this.hasEffect(ModEffect.EFFECTGHOST_SICKNESS.get());
    }

    @Override
    public SoundEvent getBGM() {
        return ModSounds.MALEDICTUS_MUSIC.get();
    }

}
