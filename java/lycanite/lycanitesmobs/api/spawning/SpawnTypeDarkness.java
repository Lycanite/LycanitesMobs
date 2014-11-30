package lycanite.lycanitesmobs.api.spawning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class SpawnTypeDarkness extends SpawnTypeBase {
	public boolean displayChatWarnings = true;
	public Map<EntityPlayer, Byte> darknessLevels = new HashMap<EntityPlayer, Byte>();

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeDarkness(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
        this.displayChatWarnings = ConfigBase.getConfig(LycanitesMobs.group, "spawning").getBool("Spawner Features", "Darkness Spawn Chat Warnings", this.displayChatWarnings, "Set to false to prevent the darkness warning messages from showing.");
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
		if(!player.capabilities.isCreativeMode && tick % (5 * 20) == 0 && this.enabled && this.hasSpawns()) {
			ChunkCoordinates playerCoords = player.getPlayerCoordinates();
			int lightLevel = world.getBlockLightValue(playerCoords.posX, playerCoords.posY, playerCoords.posZ);
			byte darknessLevel = 0;
			if(this.darknessLevels.containsKey(player))
				darknessLevel = this.darknessLevels.get(player);
			
			// Dark:
			if(lightLevel <= 5) {
				float chance = 0.125F;
				if(lightLevel <= 0)
					chance = 0.5F;
				else if(lightLevel == 1)
					chance = 0.25F;
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
		}
		return spawned;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z) {
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
