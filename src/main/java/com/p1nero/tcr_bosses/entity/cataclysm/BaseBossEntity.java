package com.p1nero.tcr_bosses.entity.cataclysm;

import com.nameless.indestructible.world.capability.Utils.IAdvancedCapability;
import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class BaseBossEntity extends BaseSmallBossEntity {

    protected BaseBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        if(serverPlayer.serverLevel().players().size() <= 1) {
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            this.setHealth(this.getMaxHealth());
            EpicFightCapabilities.getUnparameterizedEntityPatch(this, LivingEntityPatch.class).ifPresent(entityPatch -> {
                if(entityPatch instanceof IAdvancedCapability advancedCapability) {
                    advancedCapability.setPhase(0);
                }
            });
        }
    }

    @Override
    public boolean isInFighting() {
        return true;
    }
}
