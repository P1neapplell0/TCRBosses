package com.p1nero.tcr_bosses.client.bossbar;

import com.github.L_Ender.cataclysm.client.gui.CustomBossBar;
import com.github.L_Ender.cataclysm.config.CMClientConfig;
import com.github.L_Ender.cataclysm.ClientProxy;
import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmStaminaPatch;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.bossbar.BossData;
import net.shelmarow.combat_evolution.bossbar.ClientBossData;

@Mod.EventBusSubscriber(modid = TCRBossesMod.MOD_ID, value = Dist.CLIENT)
public final class CataclysmStaminaBar {
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void render(CustomizeGuiOverlayEvent.BossEventProgress event) {
        BossData data = ClientBossData.getBossData(event.getBossEvent().getId());
        if (!CataclysmStaminaPatch.DISPLAY_TYPE.equals(data.displayType)) {
            return;
        }
        int renderType = ClientProxy.bossBarRenderTypes.getOrDefault(event.getBossEvent().getId(), -1);
        CustomBossBar nativeBar = CMClientConfig.customBossBars ? CustomBossBar.customBossBars.get(renderType) : null;
        int barX = event.getX();
        int offsetY = 8;
        int width = 182;
        if (nativeBar != null) {
            barX += nativeBar.getBaseOffsetX();
            width = nativeBar.getProgress();
            offsetY = nativeBar.getBaseOffsetY() + nativeBar.getBaseHeight() + 3;
            if (nativeBar.hasOverlay()) {
                offsetY = Math.max(offsetY, nativeBar.getBaseOffsetY() + nativeBar.getOverlayOffsetY()
                        + nativeBar.getOverlayHeight() + 2);
            }
        }
        int barY = event.getY() + offsetY;
        int progress = Mth.clamp(Math.round(ClientBossData.getStaminaProgress(event.getBossEvent().getId()) * width), 0, width);
        int color = switch (renderType) {
            case 0 -> 0xffc94b37;
            case 1 -> 0xffbd69cc;
            case 2 -> 0xffe2b34e;
            case 3 -> 0xff43bdc4;
            case 4 -> 0xffaf3338;
            case 5, 6 -> 0xff8d54bf;
            case 7 -> 0xffc9b379;
            case 9 -> 0xff74ab67;
            case 12 -> 0xff568bc9;
            default -> 0xffb6b6b6;
        };
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.fill(barX - 1, barY - 1, barX + width + 1, barY + 4, 0xff171717);
        graphics.fill(barX, barY, barX + width, barY + 3, 0xff393939);
        if (data.staminaStatus == StaminaStatus.BREAK) {
            graphics.fill(barX, barY, barX + width, barY + 3, 0xff9c3030);
        } else if (progress > 0) {
            graphics.fill(barX, barY, barX + progress, barY + 3, color);
            graphics.fill(barX, barY, barX + progress, barY + 1, 0x55ffffff);
        }
        event.setIncrement(Math.max(event.getIncrement(), offsetY + 16));
    }
}
