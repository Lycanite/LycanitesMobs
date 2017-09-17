package com.lycanitesmobs.core.spawning;


import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

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
    public boolean spawnMobs(long tick, World world, BlockPos pos, EntityPlayer player, Block block) {
        int rank = 0;
        if(this.isRareBlock(block)) {
            rank = 1;
        }
        LycanitesMobs.printDebug("CustomSpawnerBlockBreak", this.typeName + ": A valid block was broken/harvested for this spawner." + (rank > 0 ? " (Rare)" : ""));
        return super.spawnMobs(tick, world, pos, player, rank);
    }


    // ==================================================
    //                     Block Break
    // ==================================================
    public boolean validBlockBreak(Block block, World world, BlockPos pos, Entity entity) {
        return false;
    }


    // ==================================================
    //                     Block Harvest
    // ==================================================
    public boolean validBlockHarvest(Block block, World world, BlockPos pos, Entity entity) {
        if(this.playerOnly) {
            if(entity instanceof EntityPlayer) {
                if(entity instanceof FakePlayer)
                    return false;
                return true;
            }
            return false;
        }
        return true;
    }


    // ==================================================
    //                      Rare Block
    // ==================================================
    public boolean isRareBlock(Block block) {
        return false;
    }
}
