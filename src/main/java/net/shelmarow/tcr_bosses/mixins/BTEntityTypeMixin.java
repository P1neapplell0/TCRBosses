package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.init.BTEntityType;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BTEntityType.class,remap = false)
public class BTEntityTypeMixin {
    @Redirect(
            method = "lambda$static$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    remap = true
            )
    )
    private static EntityType.Builder<?> reSize1(EntityType.Builder<?> instance, float pWidth, float pHeight){
        return instance.sized(1.0F,2.3F);
    }


    @Redirect(
            method = "lambda$static$1",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    remap = true
            )
    )
    private static EntityType.Builder<?> reSize2(EntityType.Builder<?> instance, float pWidth, float pHeight){
        return instance.sized(1.0F,2.3F);
    }

    @Redirect(
            method = "lambda$static$3",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    remap = true
            )
    )
    private static EntityType.Builder<?> reSize3(EntityType.Builder<?> instance, float pWidth, float pHeight){
        return instance.sized(1.0F,2.3F);
    }

    @Redirect(
            method = "lambda$static$5",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    remap = true
            )
    )
    private static EntityType.Builder<?> reSize4(EntityType.Builder<?> instance, float pWidth, float pHeight){
        return instance.sized(1.0F,2.3F);
    }

    @Redirect(
            method = "lambda$static$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType$Builder;sized(FF)Lnet/minecraft/world/entity/EntityType$Builder;",
                    remap = true
            )
    )
    private static EntityType.Builder<?> reSize5(EntityType.Builder<?> instance, float pWidth, float pHeight){
        return instance.sized(1.0F,2.3F);
    }
}
