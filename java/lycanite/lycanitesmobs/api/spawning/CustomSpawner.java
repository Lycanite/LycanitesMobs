package lycanite.lycanitesmobs.api.spawning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
    public List<SpawnType> updateSpawnTypes;
	public Map<EntityPlayer,Long> entityUpdateTicks = new HashMap<EntityPlayer, Long>();
	
	/** This uses the player update events to spawn mobs around each player randomly over time. **/
	@SubscribeEvent
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
			
			if(!entityUpdateTicks.containsKey(player))
				entityUpdateTicks.put(player, (long)0);
			long entityUpdateTick = entityUpdateTicks.get(player);
			
			// Custom Mob Spawning:
			int tickOffset = 0;
			for(SpawnType spawnType : this.updateSpawnTypes) {
				spawnType.onUpdate(entityUpdateTick - tickOffset, "area", world, x, y, z);
				tickOffset += 100;
			}
			
			entityUpdateTicks.put(player, entityUpdateTick + 1);
		}
	}

	
	// ==================================================
	//                 Block Break Event
	// ==================================================
	/** This uses the block break events to spawn mobs around blocks when they are destroyed. **/
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		EntityPlayer player = event.getPlayer(); // Only fire when broken by a player.
		if(player == null || event.block == null || event.world == null || event.isCanceled())
			return;
		if(player.capabilities.isCreativeMode) // No Spawning for Creative Players
			return;
		
		// Only Ore Blocks:
		String blockName = event.block.getUnlocalizedName();
		String[] blockNameParts = blockName.split("\\.");
		boolean isOre = false;
		for(String blockNamePart : blockNameParts) {
			if(blockNamePart.length() >= 3 && blockNamePart.substring(0, 3).equalsIgnoreCase("ore")) {
				isOre = true;
				break;
			}
		}
		if(!isOre) {
			return;
		}
		
		if(event.world != null && !event.world.isRemote) {
			World world = event.world;
			int x = (int)event.x;
			int y = (int)event.y;
			int z = (int)event.z;
			
			// Custom Mob Spawning:
			SpawnType.getSpawnType("ROCK").onUpdate(0, "blockbreak", world, x, y, z);
		}
	}
}
