package com.p1nero.tcr_bosses.entity.custom.aether.valkyrie;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ValkyrieQueenEntity extends BaseSmallBossEntity {

    public ValkyrieQueenEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public SoundEvent getBGM() {
        return AetherSoundEvents.MUSIC_BOSS_VALKYRIE_QUEEN.get();
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(EpicFightAttributes.IMPACT.get(),1.0D)
                .add(EpicFightAttributes.MAX_STAMINA.get(),15.0D)
                .add(EpicFightAttributes.STAMINA_REGEN.get(),1.0D)
                .build();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return AetherSoundEvents.ENTITY_VALKYRIE_QUEEN_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return AetherSoundEvents.ENTITY_VALKYRIE_QUEEN_DEATH.get();
    }

}
