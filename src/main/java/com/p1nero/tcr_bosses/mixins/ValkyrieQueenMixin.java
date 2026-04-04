package com.p1nero.tcr_bosses.mixins;

import com.aetherteam.aether.entity.monster.dungeon.AbstractValkyrie;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.TCRBossEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ValkyrieQueen.class)
public abstract class ValkyrieQueenMixin extends AbstractValkyrie {

    public ValkyrieQueenMixin(EntityType<? extends AbstractValkyrie> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tcr$tick(CallbackInfo ci) {
        if(level() instanceof ServerLevel serverLevel) {
            if(!TCRBossesMod.isTcrLoaded()) {
                return;
            }
            Entity entity = TCRBossEntities.VALKYRIE.get().spawn(serverLevel, this.getOnPos().above(), MobSpawnType.MOB_SUMMONED);
            if(entity != null) {
                this.discard();
            }
        }
    }

}
