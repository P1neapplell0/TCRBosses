package net.shelmarow.tcr_bosses.client.bossbar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.combat_evolution.client.bossbar.ClientBossData;
import net.shelmarow.tcr_bosses.TCRBosses;

@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class BossBarRenderEvent {

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        LerpingBossEvent boss = event.getBossEvent();


        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        int x = event.getX();
        int y = event.getY();

        ClientBossData.BossData bossData = ClientBossData.getBossData(boss.getId());

        if(bossData.displayType.getString().equals("land_golem")){
            event.setCanceled(true);
            float progress = Mth.clamp(boss.getProgress(),0,1);
            float stamina = ClientBossData.getStaminaProgress(bossData);
            float currentStamina = ClientBossData.getStamina(boss.getId());
            //绘制名字
            guiGraphics.drawCenteredString(mc.font, boss.getName(),x + 91,y,0xFFFFFF);
            //绘制背景
            guiGraphics.blit(BossBarTextures.LandGolemTextures.BOSS_BAR_BG,x + 91 - 256/2, y, 0,0,256,32,256,32);
            //绘制血量进度 13 + 230 + 13
            guiGraphics.blit(BossBarTextures.LandGolemTextures.BOSS_BAR_HEALTH,x + 91 - 256/2 + 13,y,13,0,Math.round(230*progress),32,256,32);
            //绘制耐力进度 11 + 234 + 11
            if(currentStamina <= 0){
                guiGraphics.blit(BossBarTextures.STAMINA_EMPTY_RED,x + 91 - 256/2 + 11,y,11,0,234,32,256,32);
            }
            else{
                guiGraphics.blit(BossBarTextures.LandGolemTextures.BOSS_BAR_STAMINA,x + 91 - 256/2 + 11,y,11,0,Math.round(234*stamina),32,256,32);
            }

            event.setIncrement(16 + 20);
        }
        else if(bossData.displayType.getString().equals("ocean_golem")){
            event.setCanceled(true);
            float progress = Mth.clamp(boss.getProgress(),0,1);
            float stamina = ClientBossData.getStaminaProgress(bossData);
            float currentStamina = ClientBossData.getStamina(boss.getId());
            //绘制名字
            guiGraphics.drawCenteredString(mc.font, boss.getName(),x + 91,y,0xFFFFFF);
            //绘制背景
            guiGraphics.blit(BossBarTextures.OceanGolemTextures.BOSS_BAR_BG,x + 91 - 256/2, y, 0,0,256,32,256,32);
            //绘制血量进度 13 + 230 + 13
            guiGraphics.blit(BossBarTextures.OceanGolemTextures.BOSS_BAR_HEALTH,x + 91 - 256/2 + 13,y,13,0,Math.round(230*progress),32,256,32);
            //绘制耐力进度 11 + 234 + 11
            if(currentStamina <= 0){
                guiGraphics.blit(BossBarTextures.STAMINA_EMPTY_RED,x + 91 - 256/2 + 11,y,11,0,234,32,256,32);
            }
            else{
                guiGraphics.blit(BossBarTextures.OceanGolemTextures.BOSS_BAR_STAMINA,x + 91 - 256/2 + 11,y,11,0,Math.round(234*stamina),32,256,32);
            }

            event.setIncrement(16 + 20);
        }
        else if(bossData.displayType.getString().equals("nether_golem")){
            event.setCanceled(true);
            float progress = Mth.clamp(boss.getProgress(),0,1);
            float stamina = ClientBossData.getStaminaProgress(bossData);
            float currentStamina = ClientBossData.getStamina(boss.getId());
            //绘制名字
            guiGraphics.drawCenteredString(mc.font, boss.getName(),x + 91,y,0xFFFFFF);
            //绘制背景
            guiGraphics.blit(BossBarTextures.NetherGolemTextures.BOSS_BAR_BG,x + 91 - 256/2, y, 0,0,256,32,256,32);
            //绘制血量进度 13 + 230 + 13
            guiGraphics.blit(BossBarTextures.NetherGolemTextures.BOSS_BAR_HEALTH,x + 91 - 256/2 + 13,y,13,0,Math.round(230*progress),32,256,32);
            //绘制耐力进度 11 + 234 + 11
            if(currentStamina <= 0){
                guiGraphics.blit(BossBarTextures.STAMINA_EMPTY_RED,x + 91 - 256/2 + 11,y,11,0,234,32,256,32);
            }
            else{
                guiGraphics.blit(BossBarTextures.NetherGolemTextures.BOSS_BAR_STAMINA,x + 91 - 256/2 + 11,y,11,0,Math.round(234*stamina),32,256,32);
            }

            event.setIncrement(16 + 20);
        }
        else if(bossData.displayType.getString().equals("core_golem")){
            event.setCanceled(true);
            float progress = Mth.clamp(boss.getProgress(),0,1);
            float stamina = ClientBossData.getStaminaProgress(bossData);
            float currentStamina = ClientBossData.getStamina(boss.getId());
            //绘制名字
            guiGraphics.drawCenteredString(mc.font, boss.getName(),x + 91,y,0xFFFFFF);
            //绘制背景
            guiGraphics.blit(BossBarTextures.NetherGolemTextures.BOSS_BAR_BG,x + 91 - 256/2, y, 0,0,256,32,256,32);
            //绘制血量进度 13 + 230 + 13
            guiGraphics.blit(BossBarTextures.NetherGolemTextures.BOSS_BAR_HEALTH,x + 91 - 256/2 + 13,y,13,0,Math.round(230*progress),32,256,32);
            //绘制耐力进度 11 + 234 + 11
            if(currentStamina <= 0){
                guiGraphics.blit(BossBarTextures.STAMINA_EMPTY_RED,x + 91 - 256/2 + 11,y,11,0,234,32,256,32);
            }
            else{
                guiGraphics.blit(BossBarTextures.NetherGolemTextures.BOSS_BAR_STAMINA,x + 91 - 256/2 + 11,y,11,0,Math.round(234*stamina),32,256,32);
            }

            event.setIncrement(16 + 20);
        }
        else if(bossData.displayType.getString().equals("sky_golem")){
            event.setCanceled(true);
            float progress = Mth.clamp(boss.getProgress(),0,1);
            float stamina = ClientBossData.getStaminaProgress(bossData);
            float currentStamina = ClientBossData.getStamina(boss.getId());
            //绘制名字
            guiGraphics.drawCenteredString(mc.font, boss.getName(),x + 91,y - 6,0xFFFFFF);
            //绘制血量进度 13 + 230 + 13
            guiGraphics.blit(BossBarTextures.SkyGolemTextures.BOSS_BAR_HEALTH,x + 91 - 256/2 + 13,y,13,0,Math.round(230*progress),32,256,32);
            //绘制耐力进度 11 + 234 + 11
            if(currentStamina <= 0){
                guiGraphics.blit(BossBarTextures.STAMINA_EMPTY_RED,x + 91 - 256/2 + 11,y,11,0,234,32,256,32);
            }
            else{
                guiGraphics.blit(BossBarTextures.SkyGolemTextures.BOSS_BAR_STAMINA,x + 91 - 256/2 + 11,y,11,0,Math.round(234*stamina),32,256,32);
            }
            //绘制背景
            guiGraphics.blit(BossBarTextures.SkyGolemTextures.BOSS_BAR_BG,x + 91 - 256/2, y, 0,0,256,32,256,32);

            event.setIncrement(16 + 20);
        }

    }
}
