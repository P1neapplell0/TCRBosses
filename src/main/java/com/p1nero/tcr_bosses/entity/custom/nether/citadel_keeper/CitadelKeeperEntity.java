package com.p1nero.tcr_bosses.entity.custom.nether.citadel_keeper;

import com.p1nero.tcr_bosses.entity.custom.BaseMobEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class CitadelKeeperEntity extends BaseMobEntity {
    public CitadelKeeperEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    public @NotNull Component getName() {
        return Component.translatable("entity.irons_spellbooks.citadel_keeper");
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundRegistry.KEEPER_DEATH.get();
    }

    @Override
    public void playAmbientSound() {
        this.playSound(this.getAmbientSound(), 1.0F, (float) Mth.randomBetweenInclusive(this.getRandom(), 5, 10) * 0.1F);
    }

    @Override
    protected @NotNull SoundEvent getAmbientSound() {
        return SoundRegistry.KEEPER_IDLE.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundRegistry.KEEPER_HURT.get();
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound(SoundRegistry.KEEPER_STEP.get(), 0.25F, 1.0F);
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource pSource) {
        return super.isInvulnerableTo(pSource) || pSource.is(DamageTypeTags.IS_FALL);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
