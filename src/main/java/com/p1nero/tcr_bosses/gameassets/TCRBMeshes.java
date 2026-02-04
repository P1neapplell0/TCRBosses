package com.p1nero.tcr_bosses.gameassets;

import com.p1nero.tcr_bosses.TCRBossesMod;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.client.mesh.HumanoidMesh;

public class TCRBMeshes {

    public static Meshes.MeshAccessor<SkinnedMesh> M_WING_R =
            Meshes.MeshAccessor.create(TCRBossesMod.MOD_ID, "entity/maledictus_wing_r",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

    public static Meshes.MeshAccessor<SkinnedMesh> M_WING_L =
            Meshes.MeshAccessor.create(TCRBossesMod.MOD_ID, "entity/maledictus_wing_l",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

    public static Meshes.MeshAccessor<HumanoidMesh> MALEDICTUS_HUMANOID =
            Meshes.MeshAccessor.create(TCRBossesMod.MOD_ID, "entity/maledictus_mesh",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(HumanoidMesh::new));

    public static Meshes.MeshAccessor<SkinnedMesh> WING_R =
            Meshes.MeshAccessor.create(TCRBossesMod.MOD_ID, "entity/wing_r",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

    public static Meshes.MeshAccessor<SkinnedMesh> WING_L =
            Meshes.MeshAccessor.create(TCRBossesMod.MOD_ID, "entity/wing_l",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

}
