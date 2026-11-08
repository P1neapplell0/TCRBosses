package com.p1nero.tcr_bosses.mixins;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossState;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossCompanion;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class CataclysmStaminaMobMixin implements NativeBossState {
    @Unique
    private static final EntityDataAccessor<Integer> tcr_bosses$reactionState =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void tcr_bosses$defineReactionState(CallbackInfo callback) {
        ((Mob) (Object) this).getEntityData().define(tcr_bosses$reactionState, 0);
    }

    @Override
    public int tcr_bosses$getReactionState() {
        return ((Mob) (Object) this).getEntityData().get(tcr_bosses$reactionState);
    }

    @Override
    public void tcr_bosses$setReactionState(int state) {
        ((Mob) (Object) this).getEntityData().set(tcr_bosses$reactionState, state);
    }

    @Inject(method = "serverAiStep", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$stopStaggeredAi(CallbackInfo callback) {
        if (NativeBossStaminaPatch.isLocked((Mob) (Object) this)
                || (Object) this instanceof NativeBossCompanion companion && companion.tcr_bosses$isOwnerBroken()) {
            callback.cancel();
        }
    }
}
