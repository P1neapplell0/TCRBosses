package net.shelmarow.tcr_bosses.gameassets;

import net.shelmarow.tcr_bosses.TCRBosses;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class TCRArmatures {
    public static final Armatures.ArmatureAccessor<HumanoidArmature> YAN_MO = Armatures.ArmatureAccessor.create(TCRBosses.MOD_ID, "entity/yanmo", HumanoidArmature::new);
}
