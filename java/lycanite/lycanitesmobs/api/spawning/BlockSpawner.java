package lycanite.lycanitesmobs.api.spawning;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.Config;
import net.minecraft.world.World;

public class BlockSpawner extends SpawnType {

    // ==================================================
    //                     Constructor
    // ==================================================
    public BlockSpawner(String typeName, Config config) {
        super(typeName, config);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }
}
