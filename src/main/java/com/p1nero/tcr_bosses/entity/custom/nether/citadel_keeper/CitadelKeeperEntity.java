package com.p1nero.tcr_bosses.entity.custom.nether.citadel_keeper;

import com.p1nero.tcr_bosses.entity.custom.BaseMobEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class CitadelKeeperEntity extends BaseMobEntity {
    public CitadelKeeperEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(EpicFightAttributes.IMPACT.get(),1.0D)
                .add(EpicFightAttributes.MAX_STAMINA.get(),15.0D)
                .add(EpicFightAttributes.STAMINA_REGEN.get(),1.0D)
                .build();
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable("entity.irons_spellbooks.citadel_keeper");
    }
}
