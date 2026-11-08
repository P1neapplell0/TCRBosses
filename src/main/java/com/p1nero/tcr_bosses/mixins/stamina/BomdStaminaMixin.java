package com.p1nero.tcr_bosses.mixins.stamina;

import com.cerbon.cerbons_api.api.general.event.EventScheduler;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossBarAccess;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import com.p1nero.tcr_bosses.entity.stamina.compat.BomdBossAccess;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.cerbon.bosses_of_mass_destruction.entity.util.BaseEntity")
public abstract class BomdStaminaMixin extends PathfinderMob implements NativeBossBarAccess, BomdBossAccess {
    @Shadow(remap = false)
    protected ServerBossEvent bossBar;
    @Shadow(remap = false) @Final
    protected EventScheduler preTickEvents;
    @Shadow(remap = false) @Final
    protected EventScheduler postTickEvents;
    @Unique
    private int tcr_bosses$actionSerial;
    @Unique
    private int tcr_bosses$actionUntil;

    protected BomdStaminaMixin(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    public ServerBossEvent tcr_bosses$getHealthBossEvent() {
        return bossBar;
    }

    @Override
    public int tcr_bosses$getActionSerial() {
        return tcr_bosses$actionSerial;
    }

    @Override
    public boolean tcr_bosses$isActing() {
        return tickCount < tcr_bosses$actionUntil;
    }

    @Override
    public void tcr_bosses$recordAction(byte status) {
        String entityId = ForgeRegistries.ENTITY_TYPES.getKey(getType()).getPath();
        boolean startsAction = switch (entityId) {
            case "lich" -> status >= 5 && status <= 12;
            case "gauntlet" -> status == 4 || status == 8 || status == 10 || status == 11;
            case "obsidilith" -> status >= 5 && status <= 9;
            case "void_blossom" -> status == 4 || status == 5 || status == 7 || status == 8 || status == 9;
            default -> false;
        };
        if (startsAction) {
            tcr_bosses$actionSerial++;
            int duration = switch (entityId) {
                case "lich" -> status == 5 || status == 6 ? 80 : 65;
                case "gauntlet" -> status == 8 ? 120 : status == 10 ? 65 : 80;
                case "void_blossom" -> status == 7 ? 65 : 130;
                default -> 40;
            };
            tcr_bosses$actionUntil = tickCount + duration;
        } else if (status == (entityId.equals("lich") ? 4 : 6)) {
            tcr_bosses$actionUntil = tickCount;
        }
    }

    @Override
    public void tcr_bosses$clearActions() {
        ((BomdSchedulerAccess) preTickEvents).tcr_bosses$getEvents().clear();
        ((BomdSchedulerAccess) preTickEvents).tcr_bosses$getPendingEvents().clear();
        ((BomdSchedulerAccess) postTickEvents).tcr_bosses$getEvents().clear();
        ((BomdSchedulerAccess) postTickEvents).tcr_bosses$getPendingEvents().clear();
        tcr_bosses$actionUntil = tickCount;
        byte stopEvent = (byte) (ForgeRegistries.ENTITY_TYPES.getKey(getType()).getPath().equals("lich") ? 4 : 6);
        level().broadcastEntityEvent(this, stopEvent);
    }

    @Inject(method = {"tick", "m_8119_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$freezeScripts(CallbackInfo callback) {
        if (NativeBossStaminaPatch.isLocked(this)) {
            super.tick();
            NativeBossStaminaPatch.get(this).freeze();
            callback.cancel();
        }
    }

    @Inject(method = {"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", "m_6469_(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},
            at = @At("HEAD"), cancellable = true, remap = false)
    private void tcr_bosses$unshieldBrokenBoss(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        if (NativeBossStaminaPatch.isLocked(this)) {
            callback.setReturnValue(super.hurt(source, amount));
        }
    }
}
