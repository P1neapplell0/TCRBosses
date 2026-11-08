package com.p1nero.tcr_bosses.client.bossbar;

import com.bobmowzie.mowziesmobs.client.ClientProxy;
import com.bobmowzie.mowziesmobs.client.gui.CustomBossBar;
import com.bobmowzie.mowziesmobs.server.config.ConfigHandler;
import com.p1nero.tcr_bosses.client.bossbar.AdditionalBossStaminaBar.BarLayout;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;

public final class MowzieStaminaBarLayout {
    public static BarLayout resolve(CustomizeGuiOverlayEvent.BossEventProgress event, BarLayout fallback) {
        if (!ConfigHandler.CLIENT.customBossBars.get()) {
            return fallback;
        }
        var registryName = ClientProxy.bossBarRegistryNames.get(event.getBossEvent().getId());
        CustomBossBar bar = CustomBossBar.customBossBars.get(registryName);
        if (bar == null) {
            return fallback;
        }
        int offset = bar.getBaseOffsetY() + bar.getBaseHeight() + 3;
        if (bar.hasOverlay()) {
            offset = Math.max(offset, bar.getBaseOffsetY() + bar.getOverlayOffsetY() + bar.getOverlayHeight() + 2);
        }
        int color = switch (registryName.getPath()) {
            case "ferrous_wroughtnaut" -> 0xffbd4b3b;
            case "frostmaw" -> 0xffb4dae9;
            case "umvuthi" -> 0xffd6ad43;
            default -> fallback.color();
        };
        return new BarLayout(event.getX() + 1, event.getY() + offset, 182, color);
    }
}
