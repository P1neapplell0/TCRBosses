package net.shelmarow.tcr_bosses.event;

import com.brass_amber.ba_bt.entity.hostile.golem.CoreGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.NetherGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.OceanGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.SkyGolem;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.damage_source.CEDamageTypeTags;
import net.shelmarow.tcr_bosses.TCRBosses;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;


@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvent {

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event){
        if(event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        Entity attacker = event.getSource().getDirectEntity();
        Entity attacker1 = event.getSource().getEntity();
        Entity hitEntity = event.getEntity();
        //免疫海浪的伤害
        if(hitEntity instanceof OceanGolem && attacker instanceof Wave_Entity){
            event.setCanceled(true);
        }

        if(hitEntity instanceof CoreGolem && event.getSource().is(DamageTypes.MAGIC) && attacker == null && attacker1 == null){
            event.setCanceled(true);
        }

        if(hitEntity instanceof SkyGolem && (attacker == null || attacker1 == null || attacker instanceof Lightning_Storm_Entity || attacker1 instanceof Lightning_Storm_Entity)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event){
        if(event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) || event.getSource().is(CEDamageTypeTags.EXECUTION)) return;
        Entity hitEntity = event.getEntity();
        EndGolemPatch endGolemPatch = EpicFightCapabilities.getEntityPatch(hitEntity,EndGolemPatch.class);
        if(endGolemPatch != null){
            float damage = Math.min(event.getAmount(),endGolemPatch.getOriginal().getMaxHealth() * 0.05F);
            if(CEPatchUtils.getStaminaStatus(endGolemPatch) == StaminaStatus.COMMON){
                float damageReduction = 0.15F + 0.3F * endGolemPatch.getBossBreakPhase();
                event.setAmount(damage * damageReduction);
            }
            else if(CEPatchUtils.getStaminaStatus(endGolemPatch) == StaminaStatus.BREAK){
                event.setAmount(damage * 1.5F);
            }
        }
    }
}
