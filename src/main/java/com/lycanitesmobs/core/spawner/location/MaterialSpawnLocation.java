package com.lycanitesmobs.core.spawner.location;

import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class MaterialSpawnLocation extends SpawnLocation {
    /** A list of block materials to either spawn in or not spawn in depending on if it is a blacklist or whitelist. **/
    public List<Material> materials = new ArrayList<>();

    /** Determines if the block materials list is a blacklist or whitelist. **/
    public String listType = "blacklist";
}
