package net.shelmarow.tcr_bosses.event;

import com.brass_amber.ba_bt.init.BTEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.tcr_bosses.TCRBosses;
import net.shelmarow.tcr_bosses.entity.battle_tower.core_golem.CoreGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.land_golem.LandGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.nether_golem.NetherGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.ocean_golem.OceanGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;

@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {

    @SubscribeEvent
    public static void entityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        LandGolemPatch.initAttributes(event);
        OceanGolemPatch.initAttributes(event);
        NetherGolemPatch.initAttributes(event);
        CoreGolemPatch.initAttributes(event);
        SkyGolemPatch.initAttributes(event);
        EndGolemPatch.initAttributes(event);
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
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));

        event.addPatchedEntityRenderer(BTEntityType.END_GOLEM.get(),
                entityType -> new PHumanoidRenderer<>(Meshes.BIPED_OLD_TEX, event.getContext(), entityType)
                        .initLayerLast(event.getContext(), entityType));
    }
}
