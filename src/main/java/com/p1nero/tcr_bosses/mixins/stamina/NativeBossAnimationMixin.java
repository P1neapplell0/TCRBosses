package com.p1nero.tcr_bosses.mixins.stamina;

import com.p1nero.tcr_bosses.entity.stamina.NativeBossStaminaPatch;
import com.p1nero.tcr_bosses.entity.stamina.NativeBossState;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

@Pseudo
@Mixin(targets = {"com.cerbon.bosses_of_mass_destruction.entity.custom.lich.LichEntity",
        "com.cerbon.bosses_of_mass_destruction.entity.custom.gauntlet.GauntletEntity",
        "com.cerbon.bosses_of_mass_destruction.entity.custom.obsidilith.ObsidilithEntity",
        "com.cerbon.bosses_of_mass_destruction.entity.custom.void_blossom.VoidBlossomEntity",
        "com.bobmowzie.mowziesmobs.server.entity.umvuthana.EntityUmvuthi",
        "com.bobmowzie.mowziesmobs.server.entity.sculptor.EntitySculptor",
        "net.unusual.block_factorys_bosses.entity.boss.yeti.YetiEntity",
        "net.unusual.block_factorys_bosses.entity.boss.dragon.boss.InfernalDragonEntity",
        "net.unusual.block_factorys_bosses.entity.boss.knight.UnderworldKnightEntity",
        "net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenEntity",
        "net.unusual.block_factorys_bosses.entity.boss.sandworm.SandwormEntity"}, remap = false)
public abstract class NativeBossAnimationMixin {
    @Inject(method = "registerControllers", at = @At("TAIL"))
    private void tcr_bosses$registerReaction(ControllerRegistrar controllers, CallbackInfo callback) {
        Mob entity = (Mob) (Object) this;
        if (!entity.level().isClientSide()) {
            return;
        }
        int[] lastState = {0};
        controllers.add(new AnimationController<>((GeoEntity) this, "tcr_stamina_reaction", 2, state -> {
            NativeBossStaminaPatch patch = NativeBossStaminaPatch.get(entity);
            if (patch == null || !entity.isAlive() || !patch.isBroken()) {
                lastState[0] = 0;
                state.getController().forceAnimationReset();
                return PlayState.STOP;
            }
            int reactionState = ((NativeBossState) entity).tcr_bosses$getReactionState();
            var animation = patch.bossType().reactionAnimation(entity, reactionState == 2);
            if (animation == null) {
                return PlayState.STOP;
            }
            if (lastState[0] != reactionState) {
                state.getController().forceAnimationReset();
                lastState[0] = reactionState;
            }
            return state.setAndContinue(animation);
        }));
    }
}
