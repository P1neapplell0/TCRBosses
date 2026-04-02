package com.p1nero.tcr_bosses.entity.custom.nether.golden_executor;

import com.hm.efn.registries.EFNMobEffectRegistry;
import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class GoldenExecutorEntity extends BaseSmallBossEntity {

    public GoldenExecutorEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
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
        if(this.hasEffect(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE.get())) {
            return SoundRegistry.MUSIC_FIRE_BOSS_MELODY_B.get();
        }
        return SoundRegistry.MUSIC_FIRE_BOSS_MELODY_A.get();
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable("entity.irons_spellbooks.fire_boss");
    }

}
