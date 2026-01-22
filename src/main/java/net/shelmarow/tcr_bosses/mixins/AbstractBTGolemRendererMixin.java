package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.client.renderer.golem.AbstractGolemRenderer;
import com.brass_amber.ba_bt.entity.hostile.golem.AbstractGolem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractGolemRenderer.class)
public abstract class AbstractBTGolemRendererMixin<E extends AbstractGolem, M extends EntityModel<E>> extends MobRenderer<E, M> {

    public AbstractBTGolemRendererMixin(EntityRendererProvider.Context pContext, M pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void onInit(EntityRendererProvider.Context context, EntityModel<?> model, String golemType, CallbackInfo ci) {
        this.addLayer(new ItemInHandLayer<>((AbstractGolemRenderer) (Object) this, context.getItemInHandRenderer()));
    }
}
