package lycanite.lycanitesmobs.api.spawning;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CustomSpawner {
    // ==================================================
    //                     Constructor
    // ==================================================
	public CustomSpawner() {}
	
	
	// ==================================================
	//                 Entity Update Event
	// ==================================================
	public long entityUpdateTick = 0;
	/** This uses the player update event to spawn mobs around each player randomly over time. **/
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
			
			// Custom Mob Spawning:
			int tickOffset = 0;
			for(SpawnType spawnType : SpawnType.spawnTypes) {
				spawnType.spawnMobs(entityUpdateTick - tickOffset, world, x, y, z);
				tickOffset += 100;
			}
		}
		
		this.entityUpdateTick++;
	}
}
