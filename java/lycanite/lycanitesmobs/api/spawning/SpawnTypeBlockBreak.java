package lycanite.lycanitesmobs.api.spawning;


import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class SpawnTypeBlockBreak extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeBlockBreak(String typeName) {
        super(typeName);
        CustomSpawner.instance.blockSpawnTypes.add(this);
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
    public boolean validBlockHarvest(Block block, World world, int x, int y, int z, Entity entity) { return false; }


    // ==================================================
    //                      Rare Block
    // ==================================================
    public boolean isRareBlock(Block block) {
        return false;
    }
}
