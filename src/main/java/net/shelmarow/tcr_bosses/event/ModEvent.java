package net.shelmarow.tcr_bosses.event;

import com.brass_amber.ba_bt.init.BTEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shelmarow.tcr_bosses.TCRBosses;
import net.shelmarow.tcr_bosses.entity.battle_tower.end_golem.EndGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.land_golem.LandGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.core_golem.CoreGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.nether_golem.NetherGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.ocean_golem.OceanGolemPatch;
import net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem.PSkyGolemRenderer;
import net.shelmarow.tcr_bosses.entity.battle_tower.sky_golem.SkyGolemPatch;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;

@Mod.EventBusSubscriber(modid = TCRBosses.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {


}
