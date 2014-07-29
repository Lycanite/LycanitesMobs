package lycanite.lycanitesmobs.api.spawning;

import lycanite.lycanitesmobs.OldConfig;

public class BlockSpawner extends SpawnType {

    // ==================================================
    //                     Constructor
    // ==================================================
    public BlockSpawner(String typeName, OldConfig config) {
        super(typeName, config);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }
}
