package com.p1nero.tcr_bosses.config;

import com.p1nero.tcr_bosses.entity.stamina.AdditionalBossType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.LinkedHashMap;
import java.util.Map;

public class TCRBossesConfig {
    public static final ForgeConfigSpec SPEC;
    public static final Map<String, BossStaminaConfig> BOSS_STAMINA = new LinkedHashMap<>();

    public static final ForgeConfigSpec.BooleanValue ENABLE_END_GOLEM_EQUIPMENT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("TCR Bosses 配置").push("general");

        ENABLE_END_GOLEM_EQUIPMENT = builder
                .comment("是否启用end_golem装备（武器、胸甲、护腿、靴子）")
                .define("enableEndGolemEquipment", true);

        builder.pop();

        builder.comment("灾变原版八大 Boss 耐力与处决；COMMON 配置位于 config/tcr_bosses-common.toml，所有存档共用。客户端与服务端请保持一致，修改后重启游戏或服务器。")
                .push("cataclysm");
        addBoss(builder, "cataclysm", "ender_guardian", 80, 2, 1000, 0.0, 1200);
        addBoss(builder, "cataclysm", "netherite_monstrosity", 100, 3, 1000, 0.0, 1200);
        addBoss(builder, "cataclysm", "ignis", 80, 2, 800, 0.0, 1200);
        addBoss(builder, "cataclysm", "the_harbinger", 70, 2, 800, 0.0, 1200);
        addBoss(builder, "cataclysm", "the_leviathan", 100, 3, 1000, 0.0, 1200);
        addBoss(builder, "cataclysm", "ancient_remnant", 100, 3, 1000, 0.0, 1200);
        addBoss(builder, "cataclysm", "maledictus", 70, 2, 600, 0.0, 1200);
        addBoss(builder, "cataclysm", "scylla", 70, 2, 600, 0.0, 1200);
        builder.pop();

        for (String modId : new String[]{"bosses_of_mass_destruction", "mowziesmobs", "block_factorys_bosses"}) {
            builder.comment("Boss 耐力与处决；COMMON 配置位于 config/tcr_bosses-common.toml，所有存档共用。",
                    "客户端与服务端请保持一致；修改开关或属性后重启游戏/服务器。")
                    .push(modId);
            for (AdditionalBossType boss : AdditionalBossType.values()) {
                if (boss.modId().equals(modId)) {
                    builder.comment("原版实体：" + boss.id());
                    addBoss(builder, modId, boss.entityId(), boss.defaultStamina(), boss.defaultImpact(),
                            boss.defaultWeight(), 1.0, 60);
                }
            }
            builder.pop();
        }

        SPEC = builder.build();
    }

    private static void addBoss(ForgeConfigSpec.Builder builder, String modId, String entityId, double stamina,
                                double impact, double weight, double attackCost, int regenDelay) {
        builder.push(entityId);
        BOSS_STAMINA.put(modId + ":" + entityId,
                new BossStaminaConfig(builder, stamina, impact, weight, true, attackCost, regenDelay));
        builder.pop();
    }
}
