package net.shelmarow.tcr_bosses.entity.cataclysm.remnant;

import com.github.L_Ender.cataclysm.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.shelmarow.combat_evolution.ai.CEBossEntity;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class AncientRemnant extends CEBossEntity {

    public AncientRemnant(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ANCIENT_SPEAR.get()));
    }

    public static AttributeSupplier createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(EpicFightAttributes.IMPACT.get(),2.0D)
                .add(EpicFightAttributes.STUN_ARMOR.get(),20.0D)
                .add(EpicFightAttributes.MAX_STAMINA.get(),30.0D)
                .add(EpicFightAttributes.STAMINA_REGEN.get(),1.0D)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }
}
