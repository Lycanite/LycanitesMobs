package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.ExtendedWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
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

    public List<SpawnTypeBase> darknessSpawnTypes = new ArrayList<SpawnTypeBase>();
	public Map<EntityPlayer, Byte> darknessLevels = new HashMap<EntityPlayer, Byte>();
	
    /*public List<SpawnTypeBase> shadowSpawnTypes = new ArrayList<SpawnTypeBase>();
	public Map<EntityPlayer, ChunkCoordinates> entityLightCoords = new HashMap<EntityPlayer, ChunkCoordinates>();
	public Map<EntityPlayer, int[][]> entityLightLevel = new HashMap<EntityPlayer, int[][]>();*/
	
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
			tickOffset += 105;
		}
		
		// ========== Spawn When In The Dark ==========
		if(!player.capabilities.isCreativeMode && entityUpdateTick % (5 * 20) == 0) {
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
					if(darknessLevel == 1) {
						String message = StatCollector.translateToLocal("spawner.darkness.level1");
						player.addChatMessage(new ChatComponentText(message));
					}
					else if(darknessLevel == 2) {
						String message = StatCollector.translateToLocal("spawner.darkness.level2");
						player.addChatMessage(new ChatComponentText(message));
					}
					else if(darknessLevel == 3) {
						String message = StatCollector.translateToLocal("spawner.darkness.level3");
						player.addChatMessage(new ChatComponentText(message));
						for(SpawnTypeBase spawnType : this.darknessSpawnTypes) {
							spawnType.spawnMobs(entityUpdateTick, world, playerCoords.posX, playerCoords.posY, playerCoords.posZ);
						}
						darknessLevel = 0;
					}
					else
						darknessLevel = 0;
				}
			}
			
			// Light
			else if(darknessLevel > 0) {
				if(darknessLevel == 2) {
					String message = StatCollector.translateToLocal("spawner.darkness.level1.back");
					player.addChatMessage(new ChatComponentText(message));
				}
				darknessLevel--;
			}
			
			this.darknessLevels.put(player, darknessLevel);
		}
		
		/*/ ========== Spawn On Sudden Light to Dark ==========
		if(!player.capabilities.isCreativeMode && entityUpdateTick % 4 == 0) {
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
						boolean solidBlocks = lightLevelPrev < 0 || world.isSideSolid(xOffset, coordsPrev.posY, zOffset, ForgeDirection.DOWN, true);
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
		}*/
		
		entityUpdateTicks.put(player, entityUpdateTick + 1);
	}
	
	
	// ==================================================
	//                 Entity Death Event
	// ==================================================
    public List<SpawnTypeDeath> deathSpawnTypes = new ArrayList<SpawnTypeDeath>();
	
	/** This uses the entity death events to spawn mobs when other mobs/players die. **/
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null || entity.worldObj == null || entity.worldObj.isRemote || event.isCanceled())
			return;
		
		// ========== Get Killer ==========
		Entity killerEntity = event.source.getSourceOfDamage();
		if(!(killerEntity instanceof EntityLivingBase))
			return;
		EntityLivingBase killer = (EntityLivingBase)killerEntity;
		if(!(killer instanceof EntityPlayer) && !(entity instanceof EntityPlayer))
			return;
		
		// ========== Get Coords ==========
		World world = entity.worldObj;
		int x = (int)entity.posX;
		int y = (int)entity.posY;
		int z = (int)entity.posZ;
		
		// ========== Pass To Spawners ==========
		for(SpawnTypeDeath spawnType : this.deathSpawnTypes) {
			if(spawnType.isValidKill(entity, killer))
				spawnType.spawnMobs(0, world, x, y, z);
		}
	}

	
	// ==================================================
	//                 Harvest Drops Event
	// ==================================================
    public List<SpawnTypeBase> oreBreakSpawnTypes = new ArrayList<SpawnTypeBase>();
    public List<SpawnTypeBase> cropBreakSpawnTypes = new ArrayList<SpawnTypeBase>();
    public List<SpawnTypeBase> treeBreakSpawnTypes = new ArrayList<SpawnTypeBase>();
	/** This uses the block break events to spawn mobs around blocks when they are destroyed. **/
	@SubscribeEvent
	public void onHarvestDrops(HarvestDropsEvent event) {
		EntityPlayer player = event.harvester;
		if(event.block == null || event.world == null || event.world.isRemote || event.isCanceled())
			return;
		if(player != null && player.capabilities.isCreativeMode) // No Spawning for Creative Players
			return;
		
		// Spawn On Block Harvest:
		World world = event.world;
		int x = (int)event.x;
		int y = (int)event.y;
		int z = (int)event.z + 1;
		
		String blockName = event.block.getUnlocalizedName();
		String[] blockNameParts = blockName.split("\\.");
		
		// Ore Blocks:
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
		if(event.block instanceof IPlantable) {
			for(SpawnTypeBase spawnType : this.cropBreakSpawnTypes) {
				spawnType.spawnMobs(0, world, x, y, z);
			}
		}
		
		// Tree Blocks:
		boolean isLog = false;
		for(String blockNamePart : blockNameParts) {
			if(blockNamePart.length() >= 3 && blockNamePart.substring(0, 3).equalsIgnoreCase("log")) {
				isLog = true;
				break;
			}
		}
		if(isLog) {
			for(int searchY = y + 1; searchY <= Math.min(world.getHeight(), y + 32); searchY++) {
				Block searchBlock = world.getBlock(x, searchY, z);
				if(searchBlock != event.block) {
					if(searchBlock instanceof BlockLeaves) {
						for(SpawnTypeBase spawnType : this.treeBreakSpawnTypes) {
							spawnType.spawnMobs(0, world, x, y, z);
						}
					}
					if(!world.isAirBlock(x, searchY, z))
						break;
				}
			}
		}
	}

	
	// ==================================================
	//                Player Use Bed Event
	// ==================================================
    public List<SpawnTypeSleep> sleepSpawnTypes = new ArrayList<SpawnTypeSleep>();
	/** This uses the player sleep in bed event to spawn mobs. **/
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.entityPlayer; // Only fire when used by a player.
		if(player == null || event.isCanceled())
			return;
		
		// Get Coords:
		World world = player.worldObj;
		int x = (int)event.x;
		int y = (int)event.y;
		int z = (int)event.z + 1;
		
		if(world == null || world.isRemote || world.provider.isDaytime())
			return;
		
		// Run Spawners:
		boolean interrupted = false;
		for(SpawnTypeBase spawnType : this.sleepSpawnTypes) {
			if(spawnType.spawnMobs(0, world, x, y, z))
				interrupted = true;
		}
		
		// Possible Interrupt:
		if(interrupted)
			event.result = EnumStatus.NOT_SAFE;
	}

	
	// ==================================================
	//              Lightning Strike Event
	// ==================================================
    public List<SpawnTypeStorm> lightningStrikeTypes = new ArrayList<SpawnTypeStorm>();
	/** This uses the lightning strike event to spawn mobs. **/
	public void onLightningStrike() {
		//TODO Lightning strike detection.
	}
}
