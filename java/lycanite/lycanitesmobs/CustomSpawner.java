package lycanite.lycanitesmobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.api.SpawnInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class CustomSpawner {
	
	public static Map<String, List<SpawnInfo>> blockSpawns = new HashMap<String, List<SpawnInfo>>();
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public CustomSpawner() {}
	
	// ==================================================
	//                      Add Spawn
	// ==================================================
	public static void addSpawn(SpawnInfo spawnInfo) {
		if(!blockSpawns.containsKey(spawnInfo.spawnType))
			blockSpawns.put(spawnInfo.spawnType.toUpperCase(), new ArrayList<SpawnInfo>());
		blockSpawns.get(spawnInfo.spawnType).add(spawnInfo);
	}
	
	
	// ==================================================
	//                 Entity Update Event
	// ==================================================
	public long entityUpdateTick = 0;
	/** This uses the player update event to spawn mobs around each player randomly over time. **/
	@ForgeSubscribe
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null)
			return;
		
		// ========== Player Server Side Update Events ==========
		if(entity instanceof EntityPlayer && entity.worldObj != null && !entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer)entity;
			World world = player.worldObj;
			int x = (int)player.posX;
			int y = (int)player.posY;
			int z = (int)player.posZ;
			
			// Fire Mob Spawning:
			List<SpawnInfo> fireSpawns = null;
			if(blockSpawns.containsKey("FIRE"))
				fireSpawns = blockSpawns.get("FIRE");
			if(fireSpawns != null && LycanitesMobs.config.getFeatureBool("FireSpawnEnabled") && (
					this.entityUpdateTick % LycanitesMobs.config.getFeatureInt("FireSpawnTick")) == 0
					&& player.worldObj.rand.nextDouble() >= LycanitesMobs.config.getFeatureDouble("FireSpawnChance")
					) {
				int range = LycanitesMobs.config.getFeatureInt("FireSpawnRange");
				
				// Fire Spawning - Search for Fire Blocks:
				int blockLimit = LycanitesMobs.config.getFeatureInt("FireSpawnBlockLimit");
				List<int[]> fireCoords = this.searchForBlocks(world, x, y, z, range, Block.fire.blockID);
				if(fireCoords.size() > blockLimit)
					fireCoords = fireCoords.subList(0, blockLimit);
				
				// Fire Spawning - Choose Mobs:
				SpawnInfo spawnInfo = null;
				if(fireCoords.size() > 0) {
					
					// Use spawn weights and decide randomly between valid spawns:
					List<SpawnInfo> possibleSpawns = new ArrayList<SpawnInfo>();
					int totalWeights = 0;
					for(SpawnInfo possibleSpawn : fireSpawns) {
						if(fireCoords.size() >= possibleSpawn.spawnBlockCost) {
							possibleSpawns.add(possibleSpawn);
							totalWeights += possibleSpawn.spawnWeight;
						}
					}
					if(totalWeights > 0) {
						int randomWeight = player.worldObj.rand.nextInt(totalWeights);
						for(SpawnInfo possibleSpawn : possibleSpawns) {
							if(possibleSpawn.spawnWeight > randomWeight)
								break;
							spawnInfo = possibleSpawn;
						}
					}
				}
				
				// Fire Spawning - Spawn Chosen Mobs:
				int spawnLimit = LycanitesMobs.config.getFeatureInt("FireSpawnMobLimit");
				int mobsSpawned = 0;
				if(spawnInfo != null) {
					for(int[] fireCoord : fireCoords) {
						EntityLiving entityliving = null;
						try {
							entityliving = (EntityLiving)spawnInfo.mobInfo.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
						} catch (Exception e) { e.printStackTrace(); }
						if(entityliving != null) {
							entityliving.setLocationAndAngles((double)fireCoord[0], (double)fireCoord[1], (double)fireCoord[2], world.rand.nextFloat() * 360.0F, 0.0F);
							Result canSpawn = ForgeEventFactory.canEntitySpawn(entityliving, world, (float)fireCoord[0], (float)fireCoord[1], (float)fireCoord[2]);
							if(canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityliving.getCanSpawnHere())) {
								world.spawnEntityInWorld(entityliving);
	                            if(!ForgeEventFactory.doSpecialSpawn(entityliving, world, (float)fireCoord[0], (float)fireCoord[1], (float)fireCoord[2]))
	                                entityliving.onSpawnWithEgg(null);
	                            mobsSpawned++;
							}
						}
						if(mobsSpawned >= spawnLimit)
							break;
					}
				}
			}
		}
		
		this.entityUpdateTick++;
	}
	
	// ========== Search for Blocks ==========
	/** Searches for block coords within the defined range of the given x, y, z coordinates.
	 * This will search from the closest blocks to the farthest blocks last.
	 * world - World object to search.
	 * x, y, z - Coordinates to search from.
	 * range - How far to search from the given coordinates.
	 * blockID - The ID of the blocks to search for. An array can be taken for multiple block types.
	**/
	public List<int[]> searchForBlocks(World world, int x, int y, int z, int range, int... blockID) {
		List<int[]> blockCoords = new ArrayList<int[]>();
		for(int i = x - range; i <= x + range; i++) {
			for(int j = y - range; j <= y + range; j++) {
				for(int k = z - range; k <= z + range; k++) {
					for(int validID : blockID) {
						if(world.getBlockId(i, j, k) == validID) {
							blockCoords.add(new int[] {i, j, k});
							break;
						}
					}
				}
			}
		}
		Collections.sort(blockCoords, new Comparator<int[]>() {
			@Override
			public int compare(int[] currentCoord, int[] previousCoord) {
				int deltaX = previousCoord[0] - currentCoord[0];
				int deltaY = previousCoord[1] - currentCoord[1];
				int deltaZ = previousCoord[2] - currentCoord[2];
				return Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
			}
		});
		return blockCoords;
	}
}
