package net.shelmarow.tcr_bosses.gameassets;

import net.shelmarow.tcr_bosses.TCRBosses;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;

public class TCRMeshes {

    public static Meshes.MeshAccessor<SkinnedMesh> WING =
            Meshes.MeshAccessor.create(TCRBosses.MOD_ID, "entity/wing",(jsonModelLoader) -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));

}
