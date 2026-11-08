package com.p1nero.tcr_bosses.entity.cataclysm.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import net.minecraft.world.entity.Mob;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class CataclysmStaminaPatch extends NativeBossStaminaPatch {
    public static final String DISPLAY_TYPE = "tcr_cataclysm_stamina";

    public CataclysmStaminaPatch(CataclysmBossType bossType) {
        super(bossType);
    }

    public static CataclysmStaminaPatch get(Mob entity) {
        return EpicFightCapabilities.getEntityPatch(entity, CataclysmStaminaPatch.class);
    }

    @Override
    public String displayType() {
        return DISPLAY_TYPE;
    }
}
