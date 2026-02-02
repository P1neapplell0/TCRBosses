package com.p1nero.tcr_bosses;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.p1nero.tcr_bosses.effect.TCRBossEffects;
import com.p1nero.tcr_bosses.entity.TCRBossEntities;
import org.slf4j.Logger;

@Mod(TCRBossesMod.MOD_ID)
public class TCRBossesMod {
    public static final String MOD_ID = "tcr_bosses";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TCRBossesMod(FMLJavaModLoadingContext context){
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        TCRBossEntities.REGISTRY.register(modEventBus);
        TCRBossEffects.REGISTRY.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        event.enqueueWork(TCRBossEntities::registerArmatures);
    }

}
