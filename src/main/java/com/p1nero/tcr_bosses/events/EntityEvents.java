package com.p1nero.tcr_bosses.events;

import com.aetherteam.aether.entity.AetherEntityTypes;
import com.aetherteam.aether.entity.monster.dungeon.boss.SunSpirit;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.brass_amber.ba_bt.entity.hostile.golem.*;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Wave_Entity;
import com.p1nero.tcr_bosses.entity.TCRBossEntities;
import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.combat_evolution.ai.StaminaStatus;
import net.shelmarow.combat_evolution.ai.util.CEPatchUtils;
import net.shelmarow.combat_evolution.damage_source.CEDamageTypeTags;
import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;


@Mod.EventBusSubscriber(modid = TCRBossesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntityEvents {

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
        Entity hitEntity = event.getEntity();

        if(hitEntity instanceof AbstractGolem golem){

            if(event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

            //限制处决伤害，防止残血伤害爆炸
            if(event.getSource().is(CEDamageTypeTags.EXECUTION)) {
                event.setAmount(Math.min(event.getAmount(), golem.getMaxHealth() * 0.15F));
                return;
            }

            float percent = 0.045F;
            float reduce = 1F;
            if(hitEntity instanceof NetherGolem){
                percent = 0.038F;
                reduce = 0.9F;
            }
            if(hitEntity instanceof CoreGolem){
                percent = 0.035F;
                reduce = 0.8F;
            }
            if(hitEntity instanceof SkyGolem){
                percent = 0.032F;
                reduce = 0.7F;
            }
            if(hitEntity instanceof EndGolem){
                percent = 0.030F;
                reduce = 0.65F;
            }
            event.setAmount(Math.min(event.getAmount() * reduce, golem.getMaxHealth() * percent));
        }

        EndGolemPatch endGolemPatch = EpicFightCapabilities.getEntityPatch(hitEntity,EndGolemPatch.class);
        if(endGolemPatch != null){
            float damage = Math.min(event.getAmount(),endGolemPatch.getOriginal().getMaxHealth() * 0.05F);
            if(CEPatchUtils.getStaminaStatus(endGolemPatch) == StaminaStatus.COMMON){
                float damageReduction = 0.15F + 0.3F * endGolemPatch.getBossBreakPhase();
                event.setAmount(damage * damageReduction);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJoin(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof BaseSmallBossEntity boss) {
            boss.setInFighting(true);
        }
        if(!TCRBossesMod.isTcrLoaded()) {
            return;
        }
        //替换小怪
        if(event.getEntity() instanceof KeeperEntity keeper) {
            if(event.getLevel() instanceof ServerLevel serverLevel) {
                TCRBossEntities.CITADEL_KEEPER.get().spawn(serverLevel, keeper.getOnPos(), MobSpawnType.MOB_SUMMONED);
            }
            event.setCanceled(true);
        }
        //替换女武神
        else if(event.getEntity().getType().equals(AetherEntityTypes.VALKYRIE_QUEEN.get())) {
            if(event.getLevel() instanceof ServerLevel serverLevel) {
                TCRBossEntities.VALKYRIE.get().spawn(serverLevel, event.getEntity().getOnPos(), MobSpawnType.MOB_SUMMONED);
            }
            event.setCanceled(true);
        }
        //替换烈阳巨灵
        if(event.getEntity().getType().equals(AetherEntityTypes.SUN_SPIRIT.get())) {
            if(event.getLevel() instanceof ServerLevel serverLevel) {
                TCRBossEntities.GILDED_HUNTER.get().spawn(serverLevel, event.getEntity().getOnPos(), MobSpawnType.MOB_SUMMONED);
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!TCRBossesMod.isTcrLoaded()) {
            event.getEntity().displayClientMessage(Component.translatable("info.tcrbosses.not_tcr_env").withStyle(ChatFormatting.RED), false);
        }
    }

}
