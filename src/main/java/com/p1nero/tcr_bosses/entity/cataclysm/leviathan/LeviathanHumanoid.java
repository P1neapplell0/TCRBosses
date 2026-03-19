package com.p1nero.tcr_bosses.entity.cataclysm.leviathan;

import com.github.L_Ender.cataclysm.init.ModSounds;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.nameless.indestructible.client.ClientBossInfo;
import com.nameless.indestructible.client.gui.BossBarGUi;
import com.nameless.indestructible.world.capability.Utils.IBossEventCapability;
import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.cataclysm.BaseBossEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class LeviathanHumanoid extends BaseBossEntity {

    public static final ResourceLocation BOSS_BAR_1 = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID, "textures/gui/boss_bar/leviathan_humanoid/lboss_bar1.png");

    public static final ResourceLocation BOSS_BAR_2 = ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID, "textures/gui/boss_bar/leviathan_humanoid/lboss_bar2.png");

    public LeviathanHumanoid(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(EpicFightAttributes.IMPACT.get(),1.0D)
                .add(EpicFightAttributes.MAX_STAMINA.get(),15.0D)
                .add(EpicFightAttributes.STAMINA_REGEN.get(),1.0D)
                .build();
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        if(this.getPhase() < 2) {
            return;
        }
        super.die(pDamageSource);
    }

    @Override
    public void tick() {
        super.tick();
        if(EpicFightSharedConstants.isPhysicalClient()){
            LivingEntityPatch<?> livingEntityPatch = EpicFightCapabilities.getEntityPatch(this, LivingEntityPatch.class);

            if (livingEntityPatch instanceof IBossEventCapability bossEventCapability) {
                int phase = getPhase();
                ClientBossInfo old = bossEventCapability.getClientBossInfo();
                if(old != null) {
                    if (phase == 1 && !old.getRl().equals(BOSS_BAR_1)) {
                        bossEventCapability.setupClientBossInfo(new ClientBossInfo(old.getDisplayName(), old.getOriginalName(), BOSS_BAR_1, old.getId()));
                        BossBarGUi.BossBarEntities.put(old.getId(), bossEventCapability.getClientBossInfo());
                    } else if (phase == 2 && !old.getRl().equals(BOSS_BAR_2)) {
                        bossEventCapability.setupClientBossInfo(new ClientBossInfo(old.getDisplayName(), old.getOriginalName(), BOSS_BAR_2, old.getId()));
                        BossBarGUi.BossBarEntities.put(old.getId(), bossEventCapability.getClientBossInfo());
                    }
                }
            }
        }
    }

    public int getPhase() {
        return this.hasEffect(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE.get()) ? 2 : 1;
    }

    @Override
    public SoundEvent getBGM() {
        return getPhase() == 1 ? ModSounds.LEVIATHAN_MUSIC_1.get() : ModSounds.LEVIATHAN_MUSIC_2.get();
    }

}
