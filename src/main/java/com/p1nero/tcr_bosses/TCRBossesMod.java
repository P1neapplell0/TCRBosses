package com.p1nero.tcr_bosses;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.p1nero.tcr_bosses.config.TCRBossesConfig;
import com.p1nero.tcr_bosses.effect.TCRBossEffects;
import com.p1nero.tcr_bosses.entity.TCRBossEntities;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(TCRBossesMod.MOD_ID)
public class TCRBossesMod {
    public static final String MOD_ID = "tcr_bosses";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static boolean tcrLoaded;

    public TCRBossesMod(FMLJavaModLoadingContext context){
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        TCRBossEntities.REGISTRY.register(modEventBus);
        TCRBossEffects.REGISTRY.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, TCRBossesConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        event.enqueueWork(TCRBossEntities::registerArmatures);
        event.enqueueWork(() -> {
            tcrLoaded = ModList.get().isLoaded("tcrcore");
        });
    }

    public static boolean isTcrLoaded() {
//        return tcrLoaded;
        return true;
    }
}
