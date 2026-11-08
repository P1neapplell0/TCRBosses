package com.p1nero.tcr_bosses.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class BossStaminaConfig {
    public final ForgeConfigSpec.BooleanValue enabled;
    public final ForgeConfigSpec.BooleanValue executionEnabled;
    public final ForgeConfigSpec.DoubleValue hurtImpactMultiplier;
    public final ForgeConfigSpec.DoubleValue vanillaHitCost;
    public final ForgeConfigSpec.DoubleValue attackCost;
    public final ForgeConfigSpec.DoubleValue blockedAttackCost;
    public final ForgeConfigSpec.IntValue regenDelay;
    public final ForgeConfigSpec.IntValue breakTicks;
    public final ForgeConfigSpec.IntValue recoverTicks;
    public final ForgeConfigSpec.DoubleValue maxStamina;
    public final ForgeConfigSpec.DoubleValue staminaRegen;
    public final ForgeConfigSpec.DoubleValue impact;
    public final ForgeConfigSpec.DoubleValue armorNegation;
    public final ForgeConfigSpec.DoubleValue stunArmor;
    public final ForgeConfigSpec.DoubleValue weight;
    public final ForgeConfigSpec.IntValue maxStrikes;
    public final ForgeConfigSpec.IntValue executionResistance;

    public BossStaminaConfig(ForgeConfigSpec.Builder builder, double stamina, double defaultImpact, double defaultWeight,
                             boolean defaultEnabled, double defaultAttackCost, int defaultRegenDelay) {
        enabled = builder.comment("启用该原版 Boss 的耐力条、破防与处决兼容；默认" + (defaultEnabled ? "开启。" : "关闭。"))
                .define("enabled", defaultEnabled);
        builder.push("stamina");
        hurtImpactMultiplier = builder.comment("受到史诗战斗攻击时：耐力损失 = 冲击力 × 此倍率。")
                .defineInRange("hurtImpactMultiplier", 1.0, 0.0, 1000.0);
        vanillaHitCost = builder.comment("成功受到非史诗战斗攻击时的基础耐力损失，也乘以 hurtImpactMultiplier。")
                .defineInRange("vanillaHitCost", 0.5, 0.0, 10000.0);
        attackCost = builder.comment("开始一次原模组原生动画/招式时消耗的耐力；0 表示不消耗。")
                .defineInRange("attackCost", defaultAttackCost, 0.0, 10000.0);
        blockedAttackCost = builder.comment("攻击被史诗战斗格挡/弹反时额外消耗的耐力。")
                .defineInRange("blockedAttackCost", 4.0, 0.0, 10000.0);
        regenDelay = builder.comment("最后一次受击/行动结束后，开始自然恢复前等待的 tick；20 tick = 1 秒。")
                .defineInRange("regenDelayTicks", defaultRegenDelay, 0, 12000);
        breakTicks = builder.comment("耐力耗尽后的破防窗口（tick），窗口内可处决；处决过程中暂停计时。")
                .defineInRange("breakTicks", 80, 1, 12000);
        recoverTicks = builder.comment("破防结束后线性回满耐力所需 tick；恢复期不再损失耐力、不可处决。")
                .defineInRange("recoverTicks", 60, 1, 12000);
        builder.pop().push("execution");
        executionEnabled = builder.comment("允许在破防窗口内使用 CombatEvolution 的处决键；仍受其全局处决设置限制。")
                .define("enabled", true);
        builder.pop().push("epic_fight_attributes");
        maxStamina = builder.comment("EpicFightAttributes.MAX_STAMINA（epicfight:staminar），耐力上限。")
                .defineInRange("maxStamina", stamina, 1.0, 1024.0);
        staminaRegen = builder.comment("epicfight:stamina_regen，自然恢复每 tick 为最大耐力 × 0.01 × 此值。")
                .defineInRange("staminaRegen", 1.0, 0.0, 30.0);
        impact = builder.comment("epicfight:impact。原模组原生招式仍使用原模组自己的伤害计算。")
                .defineInRange("impact", defaultImpact, 0.0, 1024.0);
        armorNegation = builder.comment("epicfight:armor_negation（百分比）。")
                .defineInRange("armorNegation", 0.0, 0.0, 100.0);
        stunArmor = builder.comment("epicfight:stun_armor；该兼容只允许耐力耗尽引发原生破防动画。")
                .defineInRange("stunArmor", 20.0, 0.0, 1024.0);
        weight = builder.comment("epicfight:weight。")
                .defineInRange("weight", defaultWeight, 1.0, 1024.0);
        maxStrikes = builder.comment("epicfight:max_strikes。")
                .defineInRange("maxStrikes", 10, 1, 1024);
        executionResistance = builder.comment("epicfight:execution_resistance；史诗战斗原生即死处决抗性，非 CombatEvolution 伤害倍率。")
                .defineInRange("executionResistance", 1, 0, 10);
        builder.pop();
    }
}
