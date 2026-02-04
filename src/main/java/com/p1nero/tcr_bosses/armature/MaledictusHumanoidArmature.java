package com.p1nero.tcr_bosses.armature;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.model.armature.HumanoidArmature;

import java.util.Map;

public class MaledictusHumanoidArmature extends HumanoidArmature {
    public final Joint wing_L;
    public final Joint wing_R;
    public MaledictusHumanoidArmature(String name, int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
        super(name, jointNumber, rootJoint, jointMap);

        this.wing_L = this.getOrLogException(jointMap, "fly_l");
        this.wing_R = this.getOrLogException(jointMap, "fly_r");
    }
}
