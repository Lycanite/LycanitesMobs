package lycanite.lycanitesmobs.api.spawning;


import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SpawnTypeBlockBreak extends SpawnTypeBase {
    public boolean playerOnly = false;

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeBlockBreak(String typeName) {
        super(typeName);
        CustomSpawner.instance.blockSpawnTypes.add(this);
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    @Override
    public void loadFromConfig() {
        super.loadFromConfig();
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");

        this.playerOnly = config.getBool("Spawner Features", this.getCfgName("Player Only"), this.playerOnly, "If true, this spawn type will only react to blocks broken by actual player (for example this will stop BuildCraft Quarries from spawning Geonach).");
    }


    // ==================================================
    //                    Spawn Mobs
    // ==================================================
    public boolean spawnMobs(long tick, World world, int x, int y, int z, EntityPlayer player, Block block) {
        boolean rare = this.isRareBlock(block);
        LycanitesMobs.printDebug("CustomSpawnerBlockBreak", this.typeName + ": A valid block was broken/harvested for this spawner." + (rare ? " (Rare)" : ""));
        return super.spawnMobs(tick, world, x, y, z, player, rare);
    }


    // ==================================================
    //                     Block Break
    // ==================================================
    public boolean validBlockBreak(Block block, World world, int x, int y, int z, Entity entity) {
        return false;
    }


    // ==================================================
    //                     Block Harvest
    // ==================================================
    public boolean validBlockHarvest(Block block, World world, int x, int y, int z, Entity entity) {
        if(this.playerOnly && !(entity instanceof EntityPlayer))
            return false;
        return true;
    }


    // ==================================================
    //                      Rare Block
    // ==================================================
    public boolean isRareBlock(Block block) {
        return false;
    }
}
