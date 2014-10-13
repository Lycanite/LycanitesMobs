package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.IGrowable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CustomSpawner {
    public static CustomSpawner instance;

    // ==================================================
    //                     Constructor
    // ==================================================
	public CustomSpawner() {
		instance = this;
	}
	
	
	// ==================================================
	//                 Entity Update Event
	// ==================================================
    public List<SpawnTypeBase> updateSpawnTypes = new ArrayList<SpawnTypeBase>();
	public Map<EntityPlayer, Long> entityUpdateTicks = new HashMap<EntityPlayer, Long>();
	
    public List<SpawnTypeBase> shadowSpawnTypes = new ArrayList<SpawnTypeBase>();
	public Map<EntityPlayer, ChunkCoordinates> entityLightCoords = new HashMap<EntityPlayer, ChunkCoordinates>();
	public Map<EntityPlayer, int[][]> entityLightLevel = new HashMap<EntityPlayer, int[][]>();
	
	/** This uses the player update events to spawn mobs around each player randomly over time. **/
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null || !(entity instanceof EntityPlayer) || entity.worldObj == null || entity.worldObj.isRemote || event.isCanceled())
			return;
		
		// ========== Spawn Near Players ==========
		EntityPlayer player = (EntityPlayer)entity;
		World world = player.worldObj;
		int x = (int)player.posX;
		int y = (int)player.posY;
		int z = (int)player.posZ;
		
		if(!entityUpdateTicks.containsKey(player))
			entityUpdateTicks.put(player, (long)0);
		long entityUpdateTick = entityUpdateTicks.get(player);
		
		// Custom Mob Spawning:
		int tickOffset = 0;
		for(SpawnTypeBase spawnType : this.updateSpawnTypes) {
			spawnType.spawnMobs(entityUpdateTick - tickOffset, world, x, y, z);
			tickOffset += 100;
		}
		
		// ========== Spawn On Sudden Light to Dark ==========
		if(/*!player.capabilities.isCreativeMode &&*/ entityUpdateTick % 4 == 0) {
			ChunkCoordinates coordsPrev = null;
			int checkRange = 6;
			if(this.entityLightCoords.containsKey(player))
				coordsPrev = this.entityLightCoords.get(player);
			if(!this.entityLightLevel.containsKey(player))
				this.entityLightLevel.put(player, new int[3][3]);
			
			boolean mobSpawned = false;
			for(int xSection = -1; xSection <= 1; xSection++) {
				for(int zSection = -1; zSection <= 1; zSection++) {
					
					// Check Light Level Change:
					if(!mobSpawned && coordsPrev != null) {
						int xOffset = coordsPrev.posX + (checkRange * xSection);
						int zOffset = coordsPrev.posZ + (checkRange * zSection);
						
						int lightLevelPrev = entityLightLevel.get(player)[xSection + 1][zSection + 1];
						//boolean solidBlocks = lightLevelPrev < 0 || !world.isAirBlock(xOffset, coordsPrev.posY, zOffset);
						boolean solidBlocks = lightLevelPrev < 0 || !world.isSideSolid(xOffset, coordsPrev.posY, zOffset, ForgeDirection.DOWN, true);
						if(!solidBlocks && lightLevelPrev >= 10 && world.getBlockLightValue(xOffset, coordsPrev.posY, zOffset) <= 5) {
							for(SpawnTypeBase spawnType : this.shadowSpawnTypes) {
								spawnType.spawnMobs(entityUpdateTick, world, xOffset, coordsPrev.posY, zOffset);
							}
						}
					}
					
					// Set Next Coord and Light Level:
					ChunkCoordinates coordsCurrent = player.getPlayerCoordinates();
					int lightLevelCurrent = world.getBlockLightValue(coordsCurrent.posX + (checkRange * xSection), coordsCurrent.posY, coordsCurrent.posZ + (checkRange * zSection));
					if(world.isSideSolid(coordsCurrent.posX + (checkRange * xSection), coordsCurrent.posY, coordsCurrent.posZ + (checkRange * zSection), ForgeDirection.DOWN, true))
					//if(!world.isAirBlock(coordsCurrent.posX + (checkRange * xSection), coordsCurrent.posY, coordsCurrent.posZ + (checkRange * zSection)))
							lightLevelCurrent = -10;
					this.entityLightLevel.get(player)[xSection + 1][zSection + 1] = lightLevelCurrent;
				}
			}
			
			this.entityLightCoords.put(player, player.getPlayerCoordinates());
		}
		
		entityUpdateTicks.put(player, entityUpdateTick + 1);
	}

	
	// ==================================================
	//                 Block Break Event
	// ==================================================
    public List<SpawnTypeBase> oreBreakSpawnTypes = new ArrayList<SpawnTypeBase>();
    public List<SpawnTypeBase> cropBreakSpawnTypes = new ArrayList<SpawnTypeBase>();
	/** This uses the block break events to spawn mobs around blocks when they are destroyed. **/
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		EntityPlayer player = event.getPlayer(); // Only fire when broken by a player.
		if(player == null || event.block == null || event.world == null || event.world.isRemote || event.isCanceled())
			return;
		if(player.capabilities.isCreativeMode) // No Spawning for Creative Players
			return;
		
		// Spawn On Block Break:
		World world = event.world;
		int x = (int)event.x;
		int y = (int)event.y;
		int z = (int)event.z + 1;

		// Ore Blocks:
		String blockName = event.block.getUnlocalizedName();
		String[] blockNameParts = blockName.split("\\.");
		boolean isOre = false;
		for(String blockNamePart : blockNameParts) {
			if(blockNamePart.length() >= 3 && blockNamePart.substring(0, 3).equalsIgnoreCase("ore")) {
				isOre = true;
				break;
			}
		}
		if(isOre) {
			for(SpawnTypeBase spawnType : this.oreBreakSpawnTypes) {
				spawnType.spawnMobs(0, world, x, y, z);
			}
		}
		
		// Crop Blocks:
		if(event.block instanceof IGrowable) {
			for(SpawnTypeBase spawnType : this.cropBreakSpawnTypes) {
				spawnType.spawnMobs(0, world, x, y, z);
			}
		}
	}

	
	// ==================================================
	//               Lightning Strike Event
	// ==================================================
    public List<SpawnTypeStorm> lightningStrikeTypes = new ArrayList<SpawnTypeStorm>();
	/** This uses the lightning strike event to spawn mobs. **/
	public void onLightningStrike() {
		//TODO Lightning strike detection.
	}
}
