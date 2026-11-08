package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoModel;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.state.BoneSnapshot;

import java.util.Map;

@Mixin(value = AnimationController.class, remap = false)
public abstract class NativeBossControllerMixin {
    @Shadow(remap = false) @Final
    protected String name;

    @Inject(method = "process", at = @At("HEAD"), cancellable = true)
    private void tcr_bosses$pauseNativeControllers(CoreGeoModel<?> model, AnimationState<?> state,
                                                  Map<String, CoreGeoBone> bones, Map<String, BoneSnapshot> snapshots,
                                                  double seekTime, boolean crashOnMissingBone, CallbackInfo callback) {
        if (!name.equals("tcr_stamina_reaction") && state.getAnimatable() instanceof Mob mob
                && NativeBossStaminaPatch.isLocked(mob)) {
            callback.cancel();
        }
    }
}
