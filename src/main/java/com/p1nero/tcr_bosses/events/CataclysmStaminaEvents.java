package com.p1nero.tcr_bosses.events;

import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.cataclysm.stamina.CataclysmStaminaPatch;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.combat_evolution.api.event.OnExecutionStartEvent;

@Mod.EventBusSubscriber(modid = TCRBossesMod.MOD_ID)
public final class CataclysmStaminaEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingDamageEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getAmount() > 0 && event.getEntity() instanceof Mob mob) {
            CataclysmStaminaPatch patch = CataclysmStaminaPatch.get(mob);
            if (patch != null) {
                patch.onHurt(event.getSource());
            }
        }
    }

    @SubscribeEvent
    public static void onExecution(OnExecutionStartEvent event) {
        if (event.getTarget() instanceof CataclysmStaminaPatch patch) {
            patch.beginExecution(event.getType().totalTick());
        }
    }
}
