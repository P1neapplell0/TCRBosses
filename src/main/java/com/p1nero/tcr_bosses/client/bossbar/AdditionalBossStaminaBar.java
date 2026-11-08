package com.p1nero.tcr_bosses.client.bossbar;

import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.bossbar.ClientBossData;

@Mod.EventBusSubscriber(modid = TCRBossesMod.MOD_ID, value = Dist.CLIENT)
public final class AdditionalBossStaminaBar {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void render(CustomizeGuiOverlayEvent.BossEventProgress event) {
        var data = ClientBossData.getBossData(event.getBossEvent().getId());
        String prefix = NativeBossStaminaPatch.DISPLAY_TYPE + ":";
        if (data.displayType == null || !data.displayType.startsWith(prefix)) {
            return;
        }
        String bossId = data.displayType.substring(prefix.length());
        BarLayout layout = new BarLayout(event.getX(), event.getY() + 8, 182, color(event));
        if (bossId.startsWith("mowziesmobs:") && ModList.get().isLoaded("mowziesmobs")) {
            layout = MowzieStaminaBarLayout.resolve(event, layout);
        } else if (bossId.startsWith("block_factorys_bosses:") && ModList.get().isLoaded("block_factorys_bosses")) {
            layout = BlockFactoryStaminaBarLayout.resolve(event, layout);
        }
        if (layout == null) {
            return;
        }
        int progress = Mth.clamp(Math.round(ClientBossData.getStaminaProgress(event.getBossEvent().getId()) * layout.width()),
                0, layout.width());
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.fill(layout.x() - 1, layout.y() - 1, layout.x() + layout.width() + 1, layout.y() + 4, 0xff171717);
        graphics.fill(layout.x(), layout.y(), layout.x() + layout.width(), layout.y() + 3, 0xff393939);
        if (data.staminaStatus == StaminaStatus.BREAK) {
            graphics.fill(layout.x(), layout.y(), layout.x() + layout.width(), layout.y() + 3, 0xff9c3030);
        } else if (progress > 0) {
            graphics.fill(layout.x(), layout.y(), layout.x() + progress, layout.y() + 3, layout.color());
            graphics.fill(layout.x(), layout.y(), layout.x() + progress, layout.y() + 1, 0x55ffffff);
        }
        event.setIncrement(Math.max(event.getIncrement(), layout.y() - event.getY() + 16));
    }

    private static int color(CustomizeGuiOverlayEvent.BossEventProgress event) {
        return switch (event.getBossEvent().getColor()) {
            case PINK -> 0xffd85e9e;
            case BLUE -> 0xff598cdb;
            case RED -> 0xffc94b42;
            case GREEN -> 0xff72ac55;
            case YELLOW -> 0xffd8b54a;
            case PURPLE -> 0xffa269cc;
            case WHITE -> 0xffd6e3e6;
        };
    }

    public record BarLayout(int x, int y, int width, int color) {
    }
}
