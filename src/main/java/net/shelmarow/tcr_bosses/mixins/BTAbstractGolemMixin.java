package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.BTAbstractGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
}
