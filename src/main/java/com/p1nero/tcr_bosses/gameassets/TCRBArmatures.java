package com.p1nero.tcr_bosses.gameassets;

import com.p1nero.tcr_bosses.TCRBossesMod;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class TCRBArmatures {
    public static final Armatures.ArmatureAccessor<HumanoidArmature> YAN_MO = Armatures.ArmatureAccessor.create(TCRBossesMod.MOD_ID, "entity/yanmo", HumanoidArmature::new);
}
