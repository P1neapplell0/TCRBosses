package com.p1nero.tcr_bosses.gameassets;

import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.armature.MaledictusHumanoidArmature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class TCRBArmatures {
    public static final Armatures.ArmatureAccessor<HumanoidArmature> MALEDICTUS_HUMANOID = Armatures.ArmatureAccessor.create(TCRBossesMod.MOD_ID, "entity/maledictus_armature", MaledictusHumanoidArmature::new);
}
