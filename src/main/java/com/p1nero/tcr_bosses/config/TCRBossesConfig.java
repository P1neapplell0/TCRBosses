package com.p1nero.tcr_bosses.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class TCRBossesConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_END_GOLEM_EQUIPMENT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("TCR Bosses 配置").push("general");

        ENABLE_END_GOLEM_EQUIPMENT = builder
                .comment("是否启用end_golem装备（武器、胸甲、护腿、靴子）")
                .define("enableEndGolemEquipment", true);

        builder.pop();

        SPEC = builder.build();
    }
}
