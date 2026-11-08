package com.p1nero.tcr_bosses.events;

import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.stamina.AdditionalBossType;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.GlobalMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

@Mod.EventBusSubscriber(modid = TCRBossesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AdditionalBossStaminaModEvents {
    @SubscribeEvent
    public static void registerPatches(EntityPatchRegistryEvent event) {
        for (AdditionalBossType boss : AdditionalBossType.installed()) {
            var previous = event.getTypeEntry().get(boss.entityType());
            event.getTypeEntry().put(boss.entityType(), entity -> () -> {
                if (!(entity instanceof Mob mob)) {
                    return null;
                }
                if (boss.config().enabled.get()) {
                    return new NativeBossStaminaPatch(boss);
                }
                if (previous != null) {
                    EntityPatch<?> previousPatch = previous.apply(entity).get();
                    if (previousPatch != null) {
                        return previousPatch;
                    }
                }
                return EpicFightGameRules.GLOBAL_STUN.getRuleValue(mob.level()) ? new GlobalMobPatch() : null;
            });
        }
    }

    @SubscribeEvent
    public static void attributes(EntityAttributeModificationEvent event) {
        for (AdditionalBossType boss : AdditionalBossType.installed()) {
            event.add(boss.entityType(), EpicFightAttributes.MAX_STAMINA.get());
            event.add(boss.entityType(), EpicFightAttributes.STAMINA_REGEN.get());
            event.add(boss.entityType(), EpicFightAttributes.IMPACT.get());
            event.add(boss.entityType(), EpicFightAttributes.ARMOR_NEGATION.get());
            event.add(boss.entityType(), EpicFightAttributes.STUN_ARMOR.get());
            event.add(boss.entityType(), EpicFightAttributes.WEIGHT.get());
            event.add(boss.entityType(), EpicFightAttributes.MAX_STRIKES.get());
            event.add(boss.entityType(), EpicFightAttributes.EXECUTION_RESISTANCE.get());
        }
    }
}
