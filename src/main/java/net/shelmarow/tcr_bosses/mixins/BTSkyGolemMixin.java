package net.shelmarow.tcr_bosses.mixins;

import com.brass_amber.ba_bt.entity.hostile.golem.AbstractGolem;
import com.brass_amber.ba_bt.entity.hostile.golem.SkyGolem;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyGolem.class)
public class BTSkyGolemMixin extends AbstractGolem {

    protected BTSkyGolemMixin(EntityType<? extends Monster> type, Level levelIn, BossEvent.BossBarColor bossBarColor) {
        super(type, levelIn, bossBarColor);
    }

    @Inject(
            method = "addBehaviorGoals",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false
    )
    public void addBehaviorGoals(CallbackInfo ci){
        ci.cancel();
    }

    @Inject(
            method = "tick",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void tick(CallbackInfo ci){
        ci.cancel();
        super.tick();
    }

    @Inject(
            method = "registerGoals",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void registerGoals(CallbackInfo ci){
        ci.cancel();
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
    }


    @Redirect(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/brass_amber/ba_bt/entity/hostile/golem/SkyGolem;moveControl:Lnet/minecraft/world/entity/ai/control/MoveControl;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void init(SkyGolem instance, MoveControl value){
        instance.moveControl = new MoveControl((SkyGolem) (Object) this);
        entityData.set(DATA_LIVING_ENTITY_FLAGS, (byte)2);
    }

    @Override
    protected void addBehaviorGoals() {

    }
}
