package net.shelmarow.tcr_bosses.entity.cataclysm.remnant;

import net.minecraft.world.entity.Entity;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

import java.util.List;
import java.util.Map;

public class AncientRemnantPatch extends CEHumanoidPatch {

    public AncientRemnantPatch() {
        super(Factions.UNDEAD);
        this.recoverTime = 120;
    }

    @Override
    protected void setWeaponMotions() {
        this.guardHitMotions.put(CapabilityItem.WeaponCategories.SPEAR,
                Map.of(CapabilityItem.Styles.COMMON, List.of(
                        Animations.LONGSWORD_GUARD_ACTIVE_HIT1,
                        Animations.LONGSWORD_GUARD_ACTIVE_HIT2,
                        Animations.SWORD_GUARD_ACTIVE_HIT1,
                        Animations.SWORD_GUARD_ACTIVE_HIT2,
                        Animations.SWORD_GUARD_ACTIVE_HIT3
                ))
        );

        this.weaponAttackMotions.put(CapabilityItem.WeaponCategories.SPEAR,
                Map.of(CapabilityItem.Styles.COMMON, AncientRemnantAI.SPEAR));
    }


    @Override
    public OpenMatrix4f getModelMatrix(float partialTicks) {
        return super.getModelMatrix(partialTicks).scale(1.35F,1.35F,1.35F);
    }
}
