package com.p1nero.tcr_bosses.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.example.GeckoLibMod;

@Mixin(value = GeckoLibMod.class, remap = false)
public class GeckoLibModMixin {
    @Inject(
            method = "shouldRegisterExamples",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void geckoLibModShouldRegisterExamples(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
