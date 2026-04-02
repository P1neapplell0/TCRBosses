package com.p1nero.tcr_bosses.entity.custom.nether.golden_executor;

import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import io.redspace.ironsspellbooks.api.network.IClientEventEntity;
import io.redspace.ironsspellbooks.api.util.BossbarManager;
import io.redspace.ironsspellbooks.api.util.FogManager;
import io.redspace.ironsspellbooks.api.util.MusicManager;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossMusicHandler;
import io.redspace.ironsspellbooks.network.EntityEventPacket;
import io.redspace.ironsspellbooks.setup.PacketDistributor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import static io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity.*;

public class GoldenExecutorEntity extends BaseSmallBossEntity implements IClientEventEntity {

    public GoldenExecutorEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new EntityEventPacket<>(this, (byte)1));
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer pServerPlayer) {
        super.stopSeenByPlayer(pServerPlayer);
        PacketDistributor.sendToPlayer(pServerPlayer, new EntityEventPacket<>(this, (byte)0));
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
        //用铁魔法自己的播放器
        return null;
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable("entity.irons_spellbooks.fire_boss");
    }

    @Override
    public void handleClientEvent(byte eventId) {
        switch (eventId) {
            case CLIENT_STOP_TRACKING -> {
                FogManager.stopEvent(this.uuid);
                MusicManager.stopEvent(this.uuid);
                BossbarManager.stopTracking(this.uuid);
            }
            case CLIENT_START_TRACKING -> MusicManager.createEvent(this, new FireBossMusicHandler(true));
            case START_MUSIC -> MusicManager.createEvent(this, new FireBossMusicHandler(true));
            case STOP_MUSIC -> MusicManager.stopEvent(this.uuid);
        }
    }

}
