package com.p1nero.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.OceanGolem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OceanGolem.class)
public class BTOceanGolemMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/brass_amber/ba_bt/entity/hostile/golem/OceanGolem;moveControl:Lnet/minecraft/world/entity/ai/control/MoveControl;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void tcr$redirectMoveControlAssignment(OceanGolem instance, MoveControl value) {
        instance.moveControl = new MoveControl(instance);
    }

    @Inject(method = "createNavigation",at = @At(value = "HEAD"), cancellable = true)
    private void tcr$createNavigation(Level level, CallbackInfoReturnable<PathNavigation> cir){
        cir.setReturnValue(new GroundPathNavigation((OceanGolem)(Object)this, level));
    }

    @Inject(method = "spawnDrowned", at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr$spawnDrowned(ServerLevel level, CallbackInfo ci) {
        ci.cancel();
    }

}
