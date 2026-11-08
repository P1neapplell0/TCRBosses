package com.p1nero.tcr_bosses.entity.stamina;

import com.p1nero.tcr_bosses.config.TCRBossesConfig;
import com.p1nero.tcr_bosses.config.BossStaminaConfig;
import com.p1nero.tcr_bosses.entity.stamina.compat.BlockFactoryStaminaCompat;
import com.p1nero.tcr_bosses.entity.stamina.compat.BomdStaminaCompat;
import com.p1nero.tcr_bosses.entity.stamina.compat.MowzieStaminaCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Arrays;
import java.util.List;

public enum AdditionalBossType implements NativeBossType {
    LICH("bosses_of_mass_destruction", "lich", 70, 2, 400, "rage_mode"),
    GAUNTLET("bosses_of_mass_destruction", "gauntlet", 90, 3, 900, "punch_stop"),
    OBSIDILITH("bosses_of_mass_destruction", "obsidilith", 100, 3, 1000, "death"),
    VOID_BLOSSOM("bosses_of_mass_destruction", "void_blossom", 80, 2, 700, "death"),
    WROUGHTNAUT("mowziesmobs", "ferrous_wroughtnaut", 90, 3, 1000, null),
    FROSTMAW("mowziesmobs", "frostmaw", 80, 3, 900, null),
    UMVUTHI("mowziesmobs", "umvuthi", 70, 2, 600, "hurt"),
    SCULPTOR("mowziesmobs", "sculptor", 70, 2, 500, "hurt"),
    YETI("block_factorys_bosses", "yeti", 80, 3, 900, "animation.get_hit"),
    INFERNAL_DRAGON("block_factorys_bosses", "infernal_dragon", 100, 3, 1000, "animation.hit"),
    UNDERWORLD_KNIGHT("block_factorys_bosses", "underworld_knight", 80, 2, 700, "animation.knocked_down"),
    SANDWORM("block_factorys_bosses", "sandworm", 100, 3, 1000,
            "animation.block_factory.bosses_rise.sandworm.knocked_down"),
    KRAKEN("block_factorys_bosses", "kraken", 100, 3, 1000, "stun_fall");

    private final String modId;
    private final String entityId;
    private final double stamina;
    private final double impact;
    private final double weight;
    private final String reaction;

    AdditionalBossType(String modId, String entityId, double stamina, double impact, double weight, String reaction) {
        this.modId = modId;
        this.entityId = entityId;
        this.stamina = stamina;
        this.impact = impact;
        this.weight = weight;
        this.reaction = reaction;
    }

    public String modId() {
        return modId;
    }

    public String entityId() {
        return entityId;
    }

    @Override
    public String id() {
        return modId + ":" + entityId;
    }

    public double defaultStamina() {
        return stamina;
    }

    public double defaultImpact() {
        return impact;
    }

    public double defaultWeight() {
        return weight;
    }

    public static List<AdditionalBossType> installed() {
        return Arrays.stream(values()).filter(boss -> ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(boss.modId, boss.entityId))).toList();
    }

    @SuppressWarnings("unchecked")
    public EntityType<? extends Mob> entityType() {
        return (EntityType<? extends Mob>) ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(modId, entityId));
    }

    @Override
    public BossStaminaConfig config() {
        return TCRBossesConfig.BOSS_STAMINA.get(id());
    }

    @Override
    public ServerBossEvent bossEvent(Mob entity) {
        return ((NativeBossBarAccess) entity).tcr_bosses$getHealthBossEvent();
    }

    @Override
    public Object action(Mob entity) {
        return switch (modId) {
            case "bosses_of_mass_destruction" -> BomdStaminaCompat.action(entity);
            case "mowziesmobs" -> MowzieStaminaCompat.action(entity);
            default -> BlockFactoryStaminaCompat.action(entity);
        };
    }

    @Override
    public boolean isActing(Mob entity) {
        return switch (modId) {
            case "bosses_of_mass_destruction" -> BomdStaminaCompat.isActing(entity);
            case "mowziesmobs" -> MowzieStaminaCompat.isActing(entity);
            default -> BlockFactoryStaminaCompat.isActing(entity);
        };
    }

    @Override
    public boolean canStagger(Mob entity) {
        return switch (modId) {
            case "bosses_of_mass_destruction" -> BomdStaminaCompat.canStagger(entity);
            case "mowziesmobs" -> MowzieStaminaCompat.canStagger(entity);
            default -> BlockFactoryStaminaCompat.canStagger(entity);
        };
    }

    @Override
    public void playReaction(Mob entity, boolean executed) {
        switch (modId) {
            case "bosses_of_mass_destruction" -> BomdStaminaCompat.clearAction(entity);
            case "mowziesmobs" -> MowzieStaminaCompat.playReaction(entity);
            default -> BlockFactoryStaminaCompat.clearAction(entity);
        }
    }

    @Override
    public void clearReaction(Mob entity) {
        switch (modId) {
            case "bosses_of_mass_destruction" -> BomdStaminaCompat.clearAction(entity);
            case "mowziesmobs" -> MowzieStaminaCompat.clearReaction(entity);
            default -> BlockFactoryStaminaCompat.clearReaction(entity);
        }
    }

    @Override
    public boolean shouldEndReaction(Mob entity) {
        return modId.equals("block_factorys_bosses") && BlockFactoryStaminaCompat.hasPriorityAnimation(entity);
    }

    @Override
    public void tickReaction(Mob entity, boolean executed) {
        if (modId.equals("mowziesmobs")) {
            MowzieStaminaCompat.tickReaction(entity);
        }
    }

    @Override
    public RawAnimation reactionAnimation(Mob entity, boolean executed) {
        if (this == KRAKEN) {
            return RawAnimation.begin().thenPlay("stun_fall").thenLoop("stun_idle");
        }
        if (this == INFERNAL_DRAGON) {
            return BlockFactoryStaminaCompat.dragonReaction(entity);
        }
        return reaction == null ? null : RawAnimation.begin().thenPlayAndHold(reaction);
    }
}
