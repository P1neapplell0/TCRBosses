package com.p1nero.tcr_bosses.client.bossbar;

import com.p1nero.tcr_bosses.client.bossbar.AdditionalBossStaminaBar.BarLayout;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.unusual.block_factorys_bosses.configuration.ClientConfiguration;
import net.unusual.block_factorys_bosses.util.BossHandling;
import net.unusual.block_factorys_bosses.util.BossHandling.HealthBarStyle;

public final class BlockFactoryStaminaBarLayout {
    public static BarLayout resolve(CustomizeGuiOverlayEvent.BossEventProgress event, BarLayout fallback) {
        var boss = BossHandling.TRACKED_BOSSES.get(event.getBossEvent().getId());
        if (boss == null) {
            return fallback;
        }
        HealthBarStyle style = switch (boss.type()) {
            case 3 -> ClientConfiguration.YETI_BOSSBAR.get().resolve();
            case 4 -> ClientConfiguration.DRAGON_BOSSBAR.get().resolve();
            case 5 -> ClientConfiguration.SANDWORM_BOSSBAR.get().resolve();
            case 6, 7 -> ClientConfiguration.KNIGHT_BOSSBAR.get().resolve();
            case 8, 9 -> ClientConfiguration.KRAKEN_BOSSBAR.get().resolve();
            default -> HealthBarStyle.VANILLA;
        };
        if (style == HealthBarStyle.OFF) {
            return null;
        }
        if (style == HealthBarStyle.VANILLA) {
            return fallback;
        }
        int offset = switch (boss.type()) {
            case 3 -> 30;
            case 8, 9 -> 34;
            default -> 28;
        };
        int color = switch (boss.type()) {
            case 3 -> 0xffc52644;
            case 4 -> 0xffba380e;
            case 5 -> 0xffd58900;
            case 6, 7 -> 0xff009ea7;
            default -> 0xffd32c49;
        };
        var bar = BossHandling.INFO[boss.type()];
        int barX = (Minecraft.getInstance().getWindow().getGuiScaledWidth() - 280) / 2 + bar.barStart();
        return new BarLayout(barX, event.getY() + offset, bar.barLength(), color);
    }
}
