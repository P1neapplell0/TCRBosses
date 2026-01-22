package net.shelmarow.tcr_bosses;

import com.brass_amber.ba_bt.init.BTEntityType;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.shelmarow.tcr_bosses.entity.TCREntities;
import org.slf4j.Logger;
import yesman.epicfight.gameasset.Armatures;

@Mod(TCRBosses.MOD_ID)
public class TCRBosses {
    public static final String MOD_ID = "tcr_bosses";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TCRBosses(FMLJavaModLoadingContext context){
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        TCREntities.REGISTRY.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event){
        event.enqueueWork(TCREntities::registerArmatures);
    }

}
