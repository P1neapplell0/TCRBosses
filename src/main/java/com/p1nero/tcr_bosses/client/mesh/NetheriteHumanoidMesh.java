package com.p1nero.tcr_bosses.client.mesh;

import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.VertexBuilder;
import yesman.epicfight.client.mesh.HumanoidMesh;

import java.util.List;
import java.util.Map;

public class NetheriteHumanoidMesh extends HumanoidMesh {
    public final SkinnedMesh.SkinnedMeshPart mask;

    public NetheriteHumanoidMesh(Map<String, Number[]> arrayMap, Map<MeshPartDefinition, List<VertexBuilder>> parts, SkinnedMesh parent, RenderProperties properties) {
        super(arrayMap, parts, parent, properties);
        this.mask = this.getOrLogException(this.parts, "mask");
    }

}
