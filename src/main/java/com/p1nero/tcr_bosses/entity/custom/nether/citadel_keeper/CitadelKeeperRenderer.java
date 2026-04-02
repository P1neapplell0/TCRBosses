package com.p1nero.tcr_bosses.entity.custom.nether.citadel_keeper;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CitadelKeeperRenderer<T extends CitadelKeeperEntity> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper.png");

    public CitadelKeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }
    
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) {
        return TEXTURE;
    }
}
