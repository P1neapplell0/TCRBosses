package com.p1nero.tcr_bosses.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.p1nero.tcr_bosses.TCRBossesMod;

public class TCRBossEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TCRBossesMod.MOD_ID);
    public static final RegistryObject<MobEffect> SHIELD = REGISTRY.register("shield", () -> new SimpleEffect(MobEffectCategory.BENEFICIAL, 0x6A5ACD));
}
