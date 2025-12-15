package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.BTOceanGolem;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BTOceanGolem.class)
public class BTOceanGolemMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/brass_amber/ba_bt/entity/hostile/golem/BTOceanGolem;moveControl:Lnet/minecraft/world/entity/ai/control/MoveControl;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void redirectMoveControlAssignment(BTOceanGolem instance, MoveControl value) {
        instance.moveControl = new MoveControl(instance);
    }

    @Inject(method = "createNavigation",at = @At(value = "HEAD"),cancellable = true)
    public void createNavigation(Level level, CallbackInfoReturnable<PathNavigation> cir){
        cir.cancel();
        cir.setReturnValue(new GroundPathNavigation((BTOceanGolem)(Object)this, level));
    }
}
