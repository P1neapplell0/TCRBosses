package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.bossbar.CEBossEvent;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = BTAbstractGolem.class)
public abstract class BTAbstractGolemMixin {

    @Shadow(remap = false)
    @Final
    private ServerBossEvent bossBar;

    @Unique
    private final CEBossEvent tcr_bosses$ceBossEvent = new CEBossEvent(Component.empty());

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void init(CallbackInfo ci) {
        BTAbstractGolem abstractGolem = (BTAbstractGolem) (Object) this;
        if(abstractGolem instanceof BTLandGolem){
            tcr_bosses$ceBossEvent.setDisplayType("land_golem");
        }
        if(abstractGolem instanceof BTOceanGolem){
            tcr_bosses$ceBossEvent.setDisplayType("ocean_golem");
        }
        if(abstractGolem instanceof BTNetherGolem){
            tcr_bosses$ceBossEvent.setDisplayType("nether_golem");
        }
        if(abstractGolem instanceof BTCoreGolem){
            tcr_bosses$ceBossEvent.setDisplayType("core_golem");
        }
        if(abstractGolem instanceof BTSkyGolem){
            tcr_bosses$ceBossEvent.setDisplayType("sky_golem");
        }
        if(abstractGolem instanceof BTEndGolem){
            tcr_bosses$ceBossEvent.setDisplayType("end_golem");
        }
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci){
        BTAbstractGolem abstractGolem = (BTAbstractGolem) (Object) this;
        tcr_bosses$ceBossEvent.setName(bossBar.getName());
        tcr_bosses$ceBossEvent.setProgress(bossBar.getProgress());
        if(abstractGolem instanceof BTLandGolem golem){
            tCR_Bosses$syncStamina(golem);
        }
        if(abstractGolem instanceof BTOceanGolem golem){
            tCR_Bosses$syncStamina(golem);
        }
        if(abstractGolem instanceof BTNetherGolem golem){
            tCR_Bosses$syncStamina(golem);
        }
        if(abstractGolem instanceof BTCoreGolem golem){
            tCR_Bosses$syncStamina(golem);
        }
        if(abstractGolem instanceof BTSkyGolem golem){
            tCR_Bosses$syncStamina(golem);
        }
        if(abstractGolem instanceof BTEndGolem golem){
            tCR_Bosses$syncStamina(golem);
        }
    }

    @Unique
    private void tCR_Bosses$syncStamina(BTAbstractGolem golem) {
        LivingEntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(golem, LivingEntityPatch.class);
        if(entityPatch != null){
            tcr_bosses$ceBossEvent.setStaminaStatus(CEPatchUtils.getStaminaStatus(entityPatch));
            tcr_bosses$ceBossEvent.setStamina(CEPatchUtils.getStaminaPercent(entityPatch));
        }
    }

    @Redirect(method = "startSeenByPlayer",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;addPlayer(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void onAddedPlayer(ServerBossEvent serverBossEvent, ServerPlayer serverPlayer) {
        tcr_bosses$ceBossEvent.addPlayer(serverPlayer);
    }

    @Redirect(method = "stopSeenByPlayer",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;removePlayer(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void onRemovePlayer(ServerBossEvent serverBossEvent, ServerPlayer serverPlayer) {
        tcr_bosses$ceBossEvent.removePlayer(serverPlayer);
    }

    @Redirect(method = "setGolemState",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;isVisible()Z")
    )
    private boolean onGetState1(ServerBossEvent instance) {
        return tcr_bosses$ceBossEvent.isVisible();
    }

    @Redirect(method = "setGolemState",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerBossEvent;setVisible(Z)V")
    )
    private void onGetState2(ServerBossEvent instance, boolean b) {
        tcr_bosses$ceBossEvent.setVisible(b);
    }


    @Inject(method = "destroyBlocksNearby",at = @At(value = "HEAD"),cancellable = true,remap = false)
    public void destroyBlocksNearby(CallbackInfo ci){
        ci.cancel();
    }

    @Inject(method = "getTargetingRange",at = @At(value = "HEAD"),cancellable = true,remap = false)
    public void getTargetingRange(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(((BTAbstractGolem)(Object)this).getAllowedTowerRange() * 2);
    }

    @Inject(method = "resetGolem",at=@At(value = "HEAD"),remap = false)
    public void resetGolem(CallbackInfo ci){
        BTAbstractGolem btAbstractGolem = (BTAbstractGolem) (Object) this;
        if (!btAbstractGolem.isDormant() && btAbstractGolem instanceof BTEndGolem endGolem) {
            endGolem.setHealth(endGolem.getMaxHealth());
            EndGolemPatch endGolemPatch = EpicFightCapabilities.getEntityPatch(endGolem,EndGolemPatch.class);
            if (endGolemPatch != null) {
                endGolemPatch.setBossBreakPhase(0);
                CEPatchUtils.setStamina(endGolemPatch,Float.MAX_VALUE);
            }
        }
    }
}
