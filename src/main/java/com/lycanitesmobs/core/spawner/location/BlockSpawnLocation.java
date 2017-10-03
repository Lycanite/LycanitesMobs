package com.lycanitesmobs.core.spawner.location;

import com.lycanitesmobs.core.modelloader.obj.Material;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class BlockSpawnLocation extends SpawnLocation {
    /** A list of blocks to either spawn in or not spawn in depending on if it is a blacklist or whitelist. **/
    public List<Block> blocks = new ArrayList<>();

    /** Determines if the block list is a blacklist or whitelist. **/
    public String listType = "blacklist";
}
