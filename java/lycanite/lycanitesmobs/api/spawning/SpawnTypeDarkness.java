package lycanite.lycanitesmobs.api.spawning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class SpawnTypeDarkness extends SpawnTypeBase {
	public boolean displayChatWarnings = true;
    /** The highest light level this spawner will work in. 5 Is above ground in the overworld at night time. 0 is pitch black. **/
    public int lightLevelMax = 5;
    public int checkRate = 5 * 20;
    public double lowChance = 0.125F;
    public double medChance = 0.25F;
    public double hiChance = 0.5F;
	public Map<EntityPlayer, Byte> darknessLevels = new HashMap<EntityPlayer, Byte>();

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeDarkness(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    @Override
    public void loadFromConfig() {
        super.loadFromConfig();
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");
        this.displayChatWarnings = config.getBool("Spawner Features", "Darkness Spawn Chat Warnings", this.displayChatWarnings, "Set to false to prevent the darkness warning messages from showing.");
        this.lightLevelMax = config.getInt("Spawner Features", "Darkness Spawn Highest Light Level", this.lightLevelMax, "The highest light level the Darkness spawn type will work in. 5 Is above ground in the overworld at night time. 0 is pitch black.");
        this.checkRate = config.getInt("Spawner Features", "Darkness Spawn Check Rate", this.checkRate, "The rate in ticks (20 ticks = 1 second) that the light level is checked, a higher rate will make things spawn much faster from the darkness.");
        this.lowChance = config.getDouble("Spawner Features", "Darkness Spawn Low Chance", this.lowChance, "The chance from 0.0-1.0 that a monster will spawn when in most dark light levels.");
        this.medChance = config.getDouble("Spawner Features", "Darkness Spawn Medium Chance", this.medChance, "The chance from 0.0-1.0 that a monster will spawn when in light level 1 (almost the darkest).");
        this.hiChance = config.getDouble("Spawner Features", "Darkness Spawn High Chance", this.hiChance, "The chance from 0.0-1.0 that a monster will spawn when in light level 0 (the darkest).");
    }
	
	
	// ==================================================
	//                    Spawn Mobs
	// ==================================================
    /**
     * Tells this spawn type to try and spawn mobs at or near the provided coordinates.
     * Usually custom spawn types shouldn't need to override this method and should instead override methods called by this method.
     * This method is usually called by the Custom Spawner class where this spawn type is added to its Spawn Type lists.
     * @param tick Used by spawn types that attempt spawn on a regular basis. Use 0 for event based spawning.
     * @param world The world to spawn in.
     * @param x X position.
     * @param y Y position.
     * @param z Z position.
     * @param player The player or null if there is no player.
     */
    @Override
    public boolean spawnMobs(long tick, World world, int x, int y, int z, EntityPlayer player) {
    	boolean spawned = false;

        ChunkCoordinates playerCoords = player.getPlayerCoordinates();
        Block block = world.getBlock(playerCoords.posX, playerCoords.posY, playerCoords.posZ);
        boolean isValidBlock = block != null;
        if(isValidBlock)
            isValidBlock = !block.isNormalCube();
        if(isValidBlock)
            isValidBlock = block.getMaterial() != Material.water;

        if(!player.capabilities.isCreativeMode && isValidBlock && tick % this.checkRate == 0 && this.enabled && this.hasSpawns()) {
			int lightLevel = world.getBlockLightValue(playerCoords.posX, playerCoords.posY, playerCoords.posZ);
			byte darknessLevel = 0;
			if(this.darknessLevels.containsKey(player))
				darknessLevel = (byte)Math.max(0, Math.min(2, this.darknessLevels.get(player)));
            LycanitesMobs.printDebug("CustomSpawner", "Darkness Level Read: " + darknessLevel);

			if(lightLevel <= this.lightLevelMax) {
				double chance = this.lowChance;
				if(lightLevel <= 0)
					chance = this.hiChance;
				else if(lightLevel == 1)
					chance = this.medChance;
				float roll = player.getRNG().nextFloat();
				ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		    	if(worldExt != null) {
		    		if("shadowgames".equalsIgnoreCase(worldExt.getMobEventType()))
		    			roll /= 2;
		    	}
				
				if(chance > roll) {
					darknessLevel++;
					if(darknessLevel == 1 && this.displayChatWarnings) {
						String message = StatCollector.translateToLocal("spawner.darkness.level1");
						player.addChatMessage(new ChatComponentText(message));
					}
					else if(darknessLevel == 2 && this.displayChatWarnings) {
						String message = StatCollector.translateToLocal("spawner.darkness.level2");
						player.addChatMessage(new ChatComponentText(message));
					}
					else if(darknessLevel == 3) {
						if(this.displayChatWarnings) {
							String message = StatCollector.translateToLocal("spawner.darkness.level3");
							player.addChatMessage(new ChatComponentText(message));
						}
						spawned = super.spawnMobs(tick, world, playerCoords.posX, playerCoords.posY, playerCoords.posZ, player);
						darknessLevel = 0;
					}
					else
						darknessLevel = 0;
				}
			}
			
			// Light
			else if(darknessLevel > 0) {
				if(darknessLevel == 2 && this.displayChatWarnings) {
					String message = StatCollector.translateToLocal("spawner.darkness.level1.back");
					player.addChatMessage(new ChatComponentText(message));
				}
				darknessLevel--;
			}
			
			this.darknessLevels.put(player, darknessLevel);
            LycanitesMobs.printDebug("CustomSpawner", "Darkness Level Write: " + darknessLevel);
		}
		return spawned;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z, boolean rare) {
        return true;
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    @Override
    public List<int[]> orderCoords(List<int[]> coords, int x, int y, int z) {
        return this.orderCoordsCloseToFar(coords, x, y, z);
    }
}
