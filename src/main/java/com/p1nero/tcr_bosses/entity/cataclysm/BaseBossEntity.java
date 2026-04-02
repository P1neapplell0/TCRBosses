package com.p1nero.tcr_bosses.entity.cataclysm;

import com.p1nero.tcr_bosses.entity.custom.BaseSmallBossEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class BaseBossEntity extends BaseSmallBossEntity {

    protected BaseBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

}
