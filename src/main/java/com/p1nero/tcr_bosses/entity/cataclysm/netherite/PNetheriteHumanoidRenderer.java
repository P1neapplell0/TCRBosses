package com.p1nero.tcr_bosses.entity.cataclysm.netherite;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import com.p1nero.tcr_bosses.client.mesh.NetheriteHumanoidMesh;
import com.p1nero.tcr_bosses.effect.TCRBossEffects;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PNetheriteHumanoidRenderer extends PHumanoidRenderer {
    public PNetheriteHumanoidRenderer(AssetAccessor mesh, EntityRendererProvider.Context context, EntityType entityType) {
        super(mesh, context, entityType);
    }


    public boolean hasEffectOfLevel(LivingEntity entity, MobEffect effect, int targetLevel) {
        if(!entity.hasEffect(effect)) {
            return false;
        }
        return entity.getEffect(effect).getAmplifier() == targetLevel;
    }

    @Override
    protected void prepareModel(SkinnedMesh mesh, LivingEntity entity, LivingEntityPatch entitypatch, LivingEntityRenderer renderer) {
        super.prepareModel(mesh, entity, entitypatch, renderer);
        //没盾buff的时候就隐藏面具
        if(mesh instanceof NetheriteHumanoidMesh netheriteHumanoidMesh) {
            netheriteHumanoidMesh.mask.setHidden(!this.hasEffectOfLevel(entity, MobEffects.DAMAGE_RESISTANCE, 4));
        }
    }
}
