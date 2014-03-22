package lycanite.lycanitesmobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public class EventListener {
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public EventListener() {}
	
	// ==================================================
	//                    Entity Update
	// ==================================================
	public long entityUpdateTick = 0;
	@ForgeSubscribe
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null)
			return;
		
		// ========== Player Server Side Update Events ==========
		if(entity instanceof EntityPlayer && entity.worldObj != null && !entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer)entity;
			
			// Custom Mob Spawning:
			if(LycanitesMobs.config.getFeatureBool("FireSpawnEnabled") && (this.entityUpdateTick % LycanitesMobs.config.getFeatureInt("FireSpawnTick")) == 0 && player.worldObj.rand.nextDouble() >= LycanitesMobs.config.getFeatureDouble("FireSpawnChance")) {
				World world = player.worldObj;
				int x = (int)player.posX;
				int y = (int)player.posY;
				int z = (int)player.posZ;
				int range = LycanitesMobs.config.getFeatureInt("FireSpawnRange");
				
				// Fire Spawning - Search for Fire Blocks:
				int blockLimit = LycanitesMobs.config.getFeatureInt("FireSpawnBlockLimit");
				List<int[]> fireCoords = this.searchForBlocks(world, x, y, z, range, Block.fire.blockID);
				if(fireCoords.size() > blockLimit)
					fireCoords = fireCoords.subList(0, blockLimit);
				
				// Fire Spawning - Choose Mobs:
				String spawnName = null;
				if(fireCoords.size() > 0) {
					int totalWeights = 0;
					
					// If there is only one possible mob, just use that:
					if(ObjectManager.fireSpawns.size() == 1) {
						for(String possibleSpawn : ObjectManager.fireSpawns.keySet()) {
							if(ObjectManager.getMob(possibleSpawn) != null)
								spawnName = possibleSpawn;
						}
					}
					
					// Otherwise use the weights and decide randomly:
					else {
						for(String possibleSpawn : ObjectManager.fireSpawns.keySet()) {
							if(ObjectManager.getMob(possibleSpawn) != null)
								totalWeights += ObjectManager.fireSpawns.get(possibleSpawn);
						}
						if(totalWeights > 0) {
							int randomWeight = player.worldObj.rand.nextInt(totalWeights);
							for(String possibleSpawn : ObjectManager.fireSpawns.keySet()) {
								if(ObjectManager.fireSpawns.get(possibleSpawn) > randomWeight)
									break;
								spawnName = possibleSpawn;
							}
						}
					}
				}
				
				// Fire Spawning - Spawn Chosen Mobs:
				int spawnLimit = LycanitesMobs.config.getFeatureInt("FireSpawnMobLimit");
				int mobsSpawned = 0;
				if(spawnName != null) {
					for(int[] fireCoord : fireCoords) {
						EntityLiving entityliving = null;
						try {
							entityliving = (EntityLiving)ObjectManager.getMob(spawnName).getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
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
	
	
    // ==================================================
    //                 Attack Target Event
    // ==================================================
	@ForgeSubscribe
	public void onAttackTarget(LivingSetAttackTargetEvent event) {
		if(event.isCancelable() && event.isCanceled())
	      return;
		
		// Better Invisibility:
		if(event.entityLiving != null) {
			if(event.entityLiving.isPotionActive(Potion.nightVision))
				return;
			if(event.target != null) {
				if(event.target.isInvisible())
					if(event.isCancelable())
						event.setCanceled(true);
			}
		}
	}
}
