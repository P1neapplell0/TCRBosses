package net.shelmarow.tcr_bosses.gameassets;

import net.shelmarow.tcr_bosses.TCRBosses;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.client.mesh.HumanoidMesh;

public class TCRMeshes {

    public static Meshes.MeshAccessor<SkinnedMesh> WING =
            Meshes.MeshAccessor.create(TCRBosses.MOD_ID, "entity/wing",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

    public static Meshes.MeshAccessor<SkinnedMesh> WING_R =
            Meshes.MeshAccessor.create(TCRBosses.MOD_ID, "entity/wing_r",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

    public static Meshes.MeshAccessor<SkinnedMesh> WING_L =
            Meshes.MeshAccessor.create(TCRBosses.MOD_ID, "entity/wing_l",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

    public static Meshes.MeshAccessor<HumanoidMesh> YAN_MO =
            Meshes.MeshAccessor.create(TCRBosses.MOD_ID, "entity/yanmo",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));

}
