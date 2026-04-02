package com.p1nero.tcr_bosses.entity;

import com.ao.tcrmeshes.TCRMeshes;
import com.brass_amber.ba_bt.init.BTEntityType;
import com.p1nero.tcr_bosses.entity.cataclysm.ender_gardian.EnderGuardianHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.harbinger.HarbingerHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield.IgnisShieldEntityRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.leviathan.LeviathanHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.maledictus.PMaledictusHumanoidGolemRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.netherite.PNetheriteHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.custom.aether.gilded_hunter.GildedHunterEntity;
import com.p1nero.tcr_bosses.entity.custom.aether.valkyrie.ValkyrieQueenEntity;
import com.p1nero.tcr_bosses.entity.custom.aether.valkyrie.ValkyrieQueenRenderer;
import com.p1nero.tcr_bosses.entity.custom.nether.WitherSkeletonTextureRenderer;
import com.p1nero.tcr_bosses.entity.custom.nether.citadel_keeper.CitadelKeeperEntity;
import com.p1nero.tcr_bosses.entity.custom.nether.citadel_keeper.CitadelKeeperRenderer;
import com.p1nero.tcr_bosses.entity.custom.nether.evening_ghost.EveningGhostEntity;
import com.p1nero.tcr_bosses.entity.custom.nether.golden_executor.GoldenExecutorEntity;
import com.p1nero.tcr_bosses.gameassets.TCRBArmatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.p1nero.tcr_bosses.TCRBossesMod;
import com.p1nero.tcr_bosses.entity.battle_tower.core_golem.CoreGolemPatch;
import com.p1nero.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import com.p1nero.tcr_bosses.entity.battle_tower.land_golem.LandGolemPatch;
import com.p1nero.tcr_bosses.entity.battle_tower.nether_golem.NetherGolemPatch;
import com.p1nero.tcr_bosses.entity.battle_tower.ocean_golem.OceanGolemPatch;
import com.p1nero.tcr_bosses.entity.battle_tower.sky_golem.PSkyGolemRenderer;
import com.p1nero.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import com.p1nero.tcr_bosses.entity.cataclysm.ender_gardian.EnderGuardianHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.harbinger.HarbingerHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.IgnisHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.shield.IgnisShieldEntity;
import com.p1nero.tcr_bosses.entity.cataclysm.leviathan.LeviathanHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.maledictus.MaledictusHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.ignis.IgnisHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.maledictus.MaledictusHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.netherite.NetheriteHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.ancient_remnant.AncientRemnantHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.ancient_remnant.AncientRemnantRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.netherite.NetheriteHumanoidRenderer;
import com.p1nero.tcr_bosses.entity.cataclysm.scylla.ScyllaHumanoid;
import com.p1nero.tcr_bosses.entity.cataclysm.scylla.ScyllaHumanoidRenderer;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(modid = TCRBossesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCRBossEntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TCRBossesMod.MOD_ID);

    public static final RegistryObject<EntityType<MaledictusHumanoid>> MALEDICTUS_HUMANOID = register("maledictus_humanoid",
            EntityType.Builder.of(MaledictusHumanoid::new, MobCategory.MONSTER).sized(0.6f * 1.4F, 1.8f * 1.4f));

    public static final RegistryObject<EntityType<IgnisHumanoid>> IGNIS_HUMANOID = register("ignis_humanoid",
            EntityType.Builder.of(IgnisHumanoid::new, MobCategory.MONSTER).sized(0.6f * 1.6F, 1.8f * 1.6f));

    public static final RegistryObject<EntityType<IgnisShieldEntity>> IGNIS_SHIELD = register("ignis_shield",
            EntityType.Builder.<IgnisShieldEntity>of(IgnisShieldEntity::new, MobCategory.MONSTER).sized(1, 1));

    public static final RegistryObject<EntityType<NetheriteHumanoid>> NETHERITE_HUMANOID = register("netherite_humanoid",
            EntityType.Builder.of(NetheriteHumanoid::new, MobCategory.MONSTER).sized(0.6f * 2, 1.8f * 2));

    public static final RegistryObject<EntityType<ScyllaHumanoid>> SCYLLA_HUMANOID = register("scylla_humanoid",
            EntityType.Builder.of(ScyllaHumanoid::new, MobCategory.MONSTER).sized(0.6f * 1.2F, 1.8f * 1.2F));

    public static final RegistryObject<EntityType<EnderGuardianHumanoid>> ENDER_GUARDIAN_HUMANOID = register("ender_guardian_humanoid",
            EntityType.Builder.of(EnderGuardianHumanoid::new, MobCategory.MONSTER).sized(0.6f * 1.6F, 1.8f * 1.6F));

    public static final RegistryObject<EntityType<HarbingerHumanoid>> HARBINGER_HUMANOID = register("harbinger_humanoid",
            EntityType.Builder.of(HarbingerHumanoid::new, MobCategory.MONSTER).sized(0.6f * 1.35F, 1.8f * 1.35F));

    public static final RegistryObject<EntityType<LeviathanHumanoid>> LEVIATHAN_HUMANOID = register("leviathan_humanoid",
            EntityType.Builder.of(LeviathanHumanoid::new, MobCategory.MONSTER).sized(0.6f * 2.0F, 1.8f * 2.0F));

    public static final RegistryObject<EntityType<AncientRemnantHumanoid>> ANCIENT_REMNANT_HUMANOID = register("ancient_remnant_humanoid",
            EntityType.Builder.of(AncientRemnantHumanoid::new, MobCategory.MONSTER).sized(0.6F * 2.0F, 1.8f * 2.0F));

    public static final RegistryObject<EntityType<CitadelKeeperEntity>> CITADEL_KEEPER = register("citadel_keeper",
            EntityType.Builder.of(CitadelKeeperEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8f));

    public static final RegistryObject<EntityType<GildedHunterEntity>> GILDED_HUNTER = register("gilded_hunter",
            EntityType.Builder.of(GildedHunterEntity::new, MobCategory.MONSTER).sized(0.6F * 1.5F, 1.8f * 1.5F));

    public static final RegistryObject<EntityType<ValkyrieQueenEntity>> VALKYRIE = register("valkyrie",
            EntityType.Builder.of(ValkyrieQueenEntity::new, MobCategory.MONSTER).sized(0.6F * 1.5F, 1.8f * 1.5F));

    public static final RegistryObject<EntityType<EveningGhostEntity>> EVENING_GHOST = register("evening_ghost",
            EntityType.Builder.of(EveningGhostEntity::new, MobCategory.MONSTER).sized(0.6F * 1.5F, 1.8f * 1.5F));

    public static final RegistryObject<EntityType<GoldenExecutorEntity>> GOLDEN_EXECUTOR = register("golden_executor",
            EntityType.Builder.of(GoldenExecutorEntity::new, MobCategory.MONSTER).sized(0.6F * 1.5F, 1.8f * 1.5F));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(ResourceLocation.fromNamespaceAndPath(TCRBossesMod.MOD_ID, name).toString()));
    }


    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(MALEDICTUS_HUMANOID.get(), MaledictusHumanoid.createAttributes());
        event.put(IGNIS_HUMANOID.get(), IgnisHumanoid.createAttributes());
        event.put(IGNIS_SHIELD.get(), IgnisShieldEntity.createAttributes());
        event.put(NETHERITE_HUMANOID.get(), NetheriteHumanoid.createAttributes());
        event.put(SCYLLA_HUMANOID.get(), ScyllaHumanoid.createAttributes());
        event.put(ANCIENT_REMNANT_HUMANOID.get(), AncientRemnantHumanoid.createAttributes());
        event.put(ENDER_GUARDIAN_HUMANOID.get(), EnderGuardianHumanoid.createAttributes());
        event.put(HARBINGER_HUMANOID.get(), HarbingerHumanoid.createAttributes());
        event.put(LEVIATHAN_HUMANOID.get(), LeviathanHumanoid.createAttributes());

        event.put(CITADEL_KEEPER.get(), CitadelKeeperEntity.createAttributes());

        event.put(GILDED_HUNTER.get(), GildedHunterEntity.createAttributes());
        event.put(VALKYRIE.get(), ValkyrieQueenEntity.createAttributes());
        event.put(EVENING_GHOST.get(), EveningGhostEntity.createAttributes());
        event.put(GOLDEN_EXECUTOR.get(), GoldenExecutorEntity.createAttributes());
    }

    @SubscribeEvent
    public static void entityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        LandGolemPatch.initAttributes(event);
        OceanGolemPatch.initAttributes(event);
        CoreGolemPatch.initAttributes(event);
        NetherGolemPatch.initAttributes(event);
        SkyGolemPatch.initAttributes(event);
        EndGolemPatch.initAttributes(event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void rendererRegister(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MALEDICTUS_HUMANOID.get(), MaledictusHumanoidRenderer::new);
        event.registerEntityRenderer(IGNIS_HUMANOID.get(), IgnisHumanoidRenderer::new);
        event.registerEntityRenderer(IGNIS_SHIELD.get(), IgnisShieldEntityRenderer::new);
        event.registerEntityRenderer(NETHERITE_HUMANOID.get(), NetheriteHumanoidRenderer::new);
        event.registerEntityRenderer(SCYLLA_HUMANOID.get(), ScyllaHumanoidRenderer::new);
        event.registerEntityRenderer(ANCIENT_REMNANT_HUMANOID.get(), AncientRemnantRenderer::new);
        event.registerEntityRenderer(ENDER_GUARDIAN_HUMANOID.get(), EnderGuardianHumanoidRenderer::new);
        event.registerEntityRenderer(HARBINGER_HUMANOID.get(), HarbingerHumanoidRenderer::new);
        event.registerEntityRenderer(LEVIATHAN_HUMANOID.get(), LeviathanHumanoidRenderer::new);

        event.registerEntityRenderer(CITADEL_KEEPER.get(), CitadelKeeperRenderer::new);
        event.registerEntityRenderer(GILDED_HUNTER.get(), WitherSkeletonTextureRenderer::new);
        event.registerEntityRenderer(VALKYRIE.get(), ValkyrieQueenRenderer::new);
        event.registerEntityRenderer(EVENING_GHOST.get(), WitherSkeletonTextureRenderer::new);
        event.registerEntityRenderer(GOLDEN_EXECUTOR.get(), WitherSkeletonTextureRenderer::new);
    }

    public static void registerArmatures() {
        Armatures.registerEntityTypeArmature(BTEntityType.LAND_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.OCEAN_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.NETHER_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.CORE_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.SKY_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.END_GOLEM.get(), Armatures.BIPED);

        Armatures.registerEntityTypeArmature(ANCIENT_REMNANT_HUMANOID.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(ENDER_GUARDIAN_HUMANOID.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(HARBINGER_HUMANOID.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(IGNIS_HUMANOID.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(LEVIATHAN_HUMANOID.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(MALEDICTUS_HUMANOID.get(), TCRBArmatures.MALEDICTUS_HUMANOID);
        Armatures.registerEntityTypeArmature(NETHERITE_HUMANOID.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(SCYLLA_HUMANOID.get(), Armatures.BIPED);
    }

    @SubscribeEvent
    public static void setPatch(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(BTEntityType.LAND_GOLEM.get(), (entity) -> LandGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.OCEAN_GOLEM.get(), (entity) -> OceanGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.NETHER_GOLEM.get(), (entity) -> NetherGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.CORE_GOLEM.get(), (entity) -> CoreGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.SKY_GOLEM.get(), (entity) -> SkyGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.END_GOLEM.get(), (entity) -> EndGolemPatch::new);
    }


    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPatchedRenderer(PatchedRenderersEvent.Add event) {
        event.addPatchedEntityRenderer(BTEntityType.LAND_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.OCEAN_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.NETHER_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.CORE_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.SKY_GOLEM.get(),
                entityType -> new PSkyGolemRenderer(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.END_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(ANCIENT_REMNANT_HUMANOID.get(),
                entityType -> new PHumanoidRenderer<>(TCRMeshes.ANCIENT_REMAIN_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(ENDER_GUARDIAN_HUMANOID.get(),
                entityType -> new PHumanoidRenderer<>(TCRMeshes.ENDER_GUARDIAN_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(HARBINGER_HUMANOID.get(),
                entityType -> new PHumanoidRenderer<>(TCRMeshes.HARBINGER_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(IGNIS_HUMANOID.get(),
                entityType -> new PHumanoidRenderer<>(TCRMeshes.IGNIS_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(LEVIATHAN_HUMANOID.get(),
                entityType -> new PHumanoidRenderer<>(TCRMeshes.LEVIATHAN_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(MALEDICTUS_HUMANOID.get(),
                entityType -> new PMaledictusHumanoidGolemRenderer(TCRMeshes.MALEDICTUS_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(NETHERITE_HUMANOID.get(),
                entityType -> new PNetheriteHumanoidRenderer(TCRMeshes.NETHERITE_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
        event.addPatchedEntityRenderer(SCYLLA_HUMANOID.get(),
                entityType -> new PHumanoidRenderer<>(TCRMeshes.SCYLLA_HUMANOID, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
    }

}
