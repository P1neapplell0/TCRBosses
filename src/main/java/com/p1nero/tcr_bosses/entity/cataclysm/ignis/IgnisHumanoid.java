package com.p1nero.tcr_bosses.entity.cataclysm.ignis;

import com.github.L_Ender.cataclysm.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import com.p1nero.tcr_bosses.effect.TCRBossEffects;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield.IgnisShieldEntity;
import net.shelmarow.combat_evolution.ai.CEBossEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class IgnisHumanoid extends CEBossEntity {

    private IgnisShieldEntity shield;

    public IgnisHumanoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
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
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        if(this.hasEffect(TCRBossEffects.SHIELD.get())) {
            //TODO 换盾挡音效
            level().playSound(null, this.getOnPos(), ModSounds.IGNIS_HURT.get(),
                    SoundSource.AMBIENT, 0.7F, 0.5F + level().random.nextFloat() * 0.3F);

            return false;
        }
        return super.hurt(pSource, pAmount);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.IGNIS_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.IGNIS_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.IGNIS_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide) {
            if(shield == null && this.hasEffect(TCRBossEffects.SHIELD.get())) {
                shield = new IgnisShieldEntity(this);
                level().addFreshEntity(shield);
            }
        }
    }
}
