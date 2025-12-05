package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.BTOceanGolem;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BTOceanGolem.class)
public class BTOceanGolemMixin {
    @Inject(method = "createNavigation",at = @At(value = "HEAD"),cancellable = true)
    public void createNavigation(Level level, CallbackInfoReturnable<PathNavigation> cir){
        cir.cancel();
        cir.setReturnValue(new GroundPathNavigation((BTOceanGolem)(Object)this, level));
    }
}
