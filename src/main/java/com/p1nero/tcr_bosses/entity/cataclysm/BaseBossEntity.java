package com.p1nero.tcr_bosses.entity.cataclysm;

import com.p1nero.tcr_bosses.client.sound.BossMusicPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBossEntity extends PathfinderMob {
    protected BaseBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public abstract SoundEvent getBGM();

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide) {
            BossMusicPlayer.playBossMusic(this, getBGM(), 32);
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        if(level().isClientSide) {
            BossMusicPlayer.stopBossMusic(this);
        }
    }
}
