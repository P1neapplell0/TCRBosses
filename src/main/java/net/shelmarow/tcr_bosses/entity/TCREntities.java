package net.shelmarow.tcr_bosses.entity;

import com.brass_amber.ba_bt.init.BTEntityType;
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
import net.shelmarow.tcr_bosses.TCRBosses;
import net.shelmarow.tcr_bosses.entity.battle_tower.core_golem.CoreGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.land_golem.LandGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.nether_golem.NetherGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.ocean_golem.OceanGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem.PSkyGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import net.shelmarow.tcr_bosses.entity.cataclysm.cloia.Cloia;
import net.shelmarow.tcr_bosses.entity.cataclysm.ignis.Ignis;
import net.shelmarow.tcr_bosses.entity.cataclysm.nethermal.Nethermal;
import net.shelmarow.tcr_bosses.entity.cataclysm.remnant.AncientRemnant;
import net.shelmarow.tcr_bosses.entity.cataclysm.remnant.AncientRemnantPatch;
import net.shelmarow.tcr_bosses.entity.cataclysm.remnant.AncientRemnantRenderer;
import net.shelmarow.tcr_bosses.entity.cataclysm.scylla.Scylla;
import net.shelmarow.tcr_bosses.entity.render.TCRHumanoidRenderer;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.gameasset.Armatures;

@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TCREntities {

    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TCRBosses.MOD_ID);

    public static final RegistryObject<EntityType<Cloia>> CLOIA = register("cloia",
            EntityType.Builder.of(Cloia::new, MobCategory.MONSTER).sized(0.6f * 1.4F, 1.8f * 1.4f));

    public static final RegistryObject<EntityType<Ignis>> IGNIS = register("ignis",
            EntityType.Builder.of(Ignis::new, MobCategory.MONSTER).sized(0.6f * 1.6F, 1.8f * 1.6f));

    public static final RegistryObject<EntityType<Nethermal>> NETHERMEL = register("nethermel",
            EntityType.Builder.of(Nethermal::new, MobCategory.MONSTER).sized(0.6f * 2, 1.8f * 2));

    public static final RegistryObject<EntityType<Scylla>> SCYLLA = register("scylla",
            EntityType.Builder.of(Scylla::new, MobCategory.MONSTER).sized(0.6f * 1.2F, 1.8f * 1.2F));

    public static final RegistryObject<EntityType<AncientRemnant>> ANCIENT_REMNANT = register("ancient_remnant",
            EntityType.Builder.of(AncientRemnant::new, MobCategory.MONSTER).sized(1.2f, 2.6f));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.Builder<T> entityTypeBuilder) {
        return REGISTRY.register(name, () -> entityTypeBuilder.build(ResourceLocation.fromNamespaceAndPath(TCRBosses.MOD_ID, name).toString()));
    }


    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(CLOIA.get(), Cloia.createAttributes());
        event.put(IGNIS.get(), Ignis.createAttributes());
        event.put(NETHERMEL.get(), Nethermal.createAttributes());
        event.put(SCYLLA.get(), Scylla.createAttributes());
        event.put(ANCIENT_REMNANT.get(), AncientRemnant.createAttributes());
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
        event.registerEntityRenderer(CLOIA.get(), TCRHumanoidRenderer::new);
        event.registerEntityRenderer(IGNIS.get(), TCRHumanoidRenderer::new);
        event.registerEntityRenderer(NETHERMEL.get(), TCRHumanoidRenderer::new);
        event.registerEntityRenderer(SCYLLA.get(), TCRHumanoidRenderer::new);
        event.registerEntityRenderer(ANCIENT_REMNANT.get(), AncientRemnantRenderer::new);
    }


    public static void registerArmatures() {
        Armatures.registerEntityTypeArmature(BTEntityType.LAND_GOLEM.get(),Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.OCEAN_GOLEM.get(),Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.NETHER_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.CORE_GOLEM.get(), Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.SKY_GOLEM.get(),Armatures.BIPED);
        Armatures.registerEntityTypeArmature(BTEntityType.END_GOLEM.get(),Armatures.BIPED);

        Armatures.registerEntityTypeArmature(ANCIENT_REMNANT.get(), Armatures.BIPED);
    }

    @SubscribeEvent
    public static void setPatch(EntityPatchRegistryEvent event) {
        event.getTypeEntry().put(BTEntityType.LAND_GOLEM.get(), (entity) -> LandGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.OCEAN_GOLEM.get(), (entity) -> OceanGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.NETHER_GOLEM.get(), (entity) -> NetherGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.CORE_GOLEM.get(), (entity) -> CoreGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.SKY_GOLEM.get(), (entity) -> SkyGolemPatch::new);
        event.getTypeEntry().put(BTEntityType.END_GOLEM.get(), (entity) -> EndGolemPatch::new);

        event.getTypeEntry().put(ANCIENT_REMNANT.get(), (entity)-> AncientRemnantPatch::new);
    }



    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPatchedRenderer(PatchedRenderersEvent.Add event){
        event.addPatchedEntityRenderer(BTEntityType.LAND_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX,event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.OCEAN_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX,event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.NETHER_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX,event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.CORE_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX,event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.SKY_GOLEM.get(),
                entityType -> new PSkyGolemRenderer(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.END_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(ANCIENT_REMNANT.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX,event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
    }

}
