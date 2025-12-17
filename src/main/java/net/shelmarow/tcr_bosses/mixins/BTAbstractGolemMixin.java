package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.BTAbstractGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.BTEndGolem;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(value = BTAbstractGolem.class,remap = false)
public class BTAbstractGolemMixin {

    @Inject(method = "destroyBlocksNearby",at = @At(value = "HEAD"),cancellable = true)
    public void destroyBlocksNearby(CallbackInfo ci){
        ci.cancel();
    }

    @Inject(method = "getTargetingRange",at = @At(value = "HEAD"),cancellable = true)
    public void getTargetingRange(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(((BTAbstractGolem)(Object)this).getAllowedTowerRange() * 2);
    }

    @Inject(method = "resetGolem",at=@At(value = "HEAD"))
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
