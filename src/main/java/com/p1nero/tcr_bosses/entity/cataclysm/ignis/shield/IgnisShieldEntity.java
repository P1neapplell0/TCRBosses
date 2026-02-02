package com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import com.p1nero.tcr_bosses.effect.TCRBossEffects;
import com.p1nero.tcr_bosses.entity.TCRBossEntities;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.IgnisHumanoid;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class IgnisShieldEntity extends PathfinderMob implements GeoEntity {

    private IgnisHumanoid owner;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public IgnisShieldEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IgnisShieldEntity(IgnisHumanoid owner) {
        this(TCRBossEntities.IGNIS_SHIELD.get(), owner.level());
        this.noPhysics = true;
        this.setNoGravity(true);
        this.owner = owner;
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

    public IgnisHumanoid getOwner() {
        return owner;
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide) {
            if(owner == null || owner.isDeadOrDying() || !owner.hasEffect(TCRBossEffects.SHIELD.get())) {
                if(this.tickCount > 100) {//生出来看看，不急着杀
                    this.discard();
                }
            }
            if(owner != null) {
                this.moveTo(owner.position());
                this.setYRot(owner.getYRot());
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        //TODO
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
