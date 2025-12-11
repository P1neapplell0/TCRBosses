package net.shelmarow.tcr_bosses.event;

import com.brass_amber.ba_bt.entity.hostile.golem.BTNetherGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.BTOceanGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.BTSkyGolem;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.tcr_bosses.TCRBosses;


@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvent {
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event){
        Entity attacker = event.getSource().getDirectEntity();
        Entity attacker1 = event.getSource().getEntity();
        Entity hitEntity = event.getEntity();
        //免疫海浪的伤害
        if(hitEntity instanceof BTOceanGolem && attacker instanceof Wave_Entity){
            event.setCanceled(true);
        }

        if(hitEntity instanceof BTNetherGolem && event.getSource().is(DamageTypes.MAGIC) && attacker == null && attacker1 == null){
            event.setCanceled(true);
        }

        if(hitEntity instanceof BTSkyGolem && (attacker == null || attacker1 == null || attacker instanceof Lightning_Storm_Entity || attacker1 instanceof Lightning_Storm_Entity)){
            event.setCanceled(true);
        }
    }
}
