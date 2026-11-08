package com.p1nero.tcr_bosses.entity.stamina.compat;

import com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity;
import com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity;
import net.minecraft.world.entity.Mob;

public final class BomdStaminaCompat {
    public static Object action(Mob entity) {
        return ((BomdBossAccess) entity).tcr_bosses$getActionSerial();
    }

    public static boolean isActing(Mob entity) {
        return ((BomdBossAccess) entity).tcr_bosses$isActing();
    }

    public static boolean canStagger(Mob entity) {
        return !(entity instanceof ObsidilithEntity obsidilith) || !obsidilith.isShielded();
    }

    public static void clearAction(Mob entity) {
        ((BomdBossAccess) entity).tcr_bosses$clearActions();
        if (entity instanceof GauntletEntity gauntlet) {
            gauntlet.getEntityData().set(GauntletEntity.laserTarget, 0);
        }
    }
}
