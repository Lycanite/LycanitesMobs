package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.io.FilenameUtils;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class Utilities {
    
	// ==================================================
  	//                    Dungeon Loot
  	// ==================================================
	public static void addDungeonLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
        ConfigBase config = LycanitesMobs.config;
        config.setCategoryComment("Dungeon Loot Enabled", "Here you can enable/disable dungeon loot for various types of dungeons.");
        if(config.getBool("Dungeon Loot Enabled", "Dungeons", true, "These are most dungeons from underground mob spawner dungeons to pyramids, mineshafts and jungle temples.")) {
            /*ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));*/
        }
	}

	public static void addStrongholdLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
        ConfigBase config = LycanitesMobs.config;
        if(config.getBool("Dungeon Loot Enabled", "Strongholds", true, "Stronghold dungeons including corridors, libraries and other parts.")) {
            /*ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CORRIDOR, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_LIBRARY, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
            ChestGenHooks.addItem(ChestGenHooks.STRONGHOLD_CROSSING, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));*/
        }
	}

	public static void addVillageLoot(ItemStack itemStack, int minAmount, int maxAmount, int weight) {
        ConfigBase config = LycanitesMobs.config;
        if(config.getBool("Dungeon Loot Enabled", "Blacksmiths", true, "These are the chests found in village blacksmiths homes.")) {
            //ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(itemStack, minAmount, maxAmount, weight));
        }
	}
	
	
	// ==================================================
  	//                      Raytrace
  	// ==================================================
	// ========== Raytrace All ==========
    public static RayTraceResult raytrace(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
		Vec3d startVec = new Vec3d(x, y, z);
        Vec3d lookVec = new Vec3d(tx - x, ty - y, tz - z);
        Vec3d endVec = new Vec3d(tx, ty, tz);
		float minX = (float)(x < tx ? x : tx);
		float minY = (float)(y < ty ? y : ty);
		float minZ = (float)(z < tz ? z : tz);
		float maxX = (float)(x > tx ? x : tx);
		float maxY = (float)(y > ty ? y : ty);
		float maxZ = (float)(z > tz ? z : tz);

		// Get Block Collision:
        RayTraceResult collision = world.rayTraceBlocks(startVec, endVec, false);
		startVec = new Vec3d(x, y, z);
		endVec = new Vec3d(tx, ty, tz);
		float maxDistance = (float)endVec.distanceTo(startVec);
		if(collision != null)
			maxDistance = (float)collision.hitVec.distanceTo(startVec);

		// Get Entity Collision:
		if(excluded != null) {
			AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
			List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);
			Entity closestHitEntity = null;
			float closestHit = Float.POSITIVE_INFINITY;
			float currentHit;
			AxisAlignedBB entityBb;
            RayTraceResult intercept;
			for(Entity ent : allEntities) {
				if(ent.canBeCollidedWith() && !excluded.contains(ent)) {
					float entBorder = ent.getCollisionBorderSize();
					entityBb = ent.getEntityBoundingBox();
					if(entityBb != null) {
						entityBb = entityBb.expand(entBorder, entBorder, entBorder);
						intercept = entityBb.calculateIntercept(startVec, endVec);
						if(intercept != null) {
							currentHit = (float) intercept.hitVec.distanceTo(startVec);
							if(currentHit < closestHit || currentHit == 0) {
								closestHit = currentHit;
								closestHitEntity = ent;
							}
						}
					}
				}
			}
			if(closestHitEntity != null)
				collision = new RayTraceResult(closestHitEntity);
		}
		
		return collision;
    }

    public static RayTraceResult raytraceEntities(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
        Vec3d startVec = new Vec3d(x, y, z);
        Vec3d lookVec = new Vec3d(tx - x, ty - y, tz - z);
        Vec3d endVec = new Vec3d(tx, ty, tz);
		float minX = (float)(x < tx ? x : tx);
		float minY = (float)(y < ty ? y : ty);
		float minZ = (float)(z < tz ? z : tz);
		float maxX = (float)(x > tx ? x : tx);
		float maxY = (float)(y > ty ? y : ty);
		float maxZ = (float)(z > tz ? z : tz);

		// Get Entities and Raytrace Blocks:
		AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
		List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(
				null, bb);
        RayTraceResult collision = world.rayTraceBlocks(startVec, endVec, false);

		// Get Entity Collision:
		Entity closestHitEntity = null;
		float closestHit = Float.POSITIVE_INFINITY;
		float currentHit = 0.0f;
		AxisAlignedBB entityBb;
        RayTraceResult intercept;
		for(Entity ent : allEntities) {
			if(ent.canBeCollidedWith() && !excluded.contains(ent)) {
				float entBorder = ent.getCollisionBorderSize();
				entityBb = ent.getEntityBoundingBox();
				if(entityBb != null) {
					entityBb = entityBb.expand(entBorder, entBorder, entBorder);
					intercept = entityBb.calculateIntercept(startVec, endVec);
					if(intercept != null) {
						currentHit = (float) intercept.hitVec.distanceTo(startVec);
						if(currentHit < closestHit || currentHit == 0) {
							closestHit = currentHit;
							closestHitEntity = ent;
						}
					}
				}
			}
		}
		if(closestHitEntity != null)
			collision = new RayTraceResult(closestHitEntity);
		return collision;
    }
	
	
	// ==================================================
  	//                      Seasonal
  	// ==================================================
    public static boolean isHalloween() {
    	Calendar calendar = Calendar.getInstance();
		if(		(calendar.get(Calendar.DAY_OF_MONTH) >= 25 && calendar.get(Calendar.MONTH) == calendar.OCTOBER)
			||	(calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.MONTH) == calendar.NOVEMBER)
		)
			return true;
		return false;
    }

    public static boolean isYuletide() {
    	Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) == calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) > 9 && calendar.get(Calendar.DAY_OF_MONTH) < 26;
    }

    public static boolean isYuletideDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) == 25;
    }

    public static boolean isNewYear() {
    	Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) == calendar.JANUARY && calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }


	// ==================================================
	//                   File Loading
	// ==================================================

	/**
	 * Returns A Path instance for the provided asset path of the jar file that the provided class is in.
	 * @param clazz The class to base the jar file off of.
	 * @param assetDomain The mod domain name.
	 * @param assetPath The path inside of the mod's assets directory. Ex: "textures/blocks"
	 * @return
	 */
	public static Path getAssetPath(Class clazz, String assetDomain, String assetPath) {
		Path path = null;
		String assetDir = "/assets/" + assetDomain + (!"".equals(assetPath) ? "/" + assetPath : "");
		try {
			URL url = clazz.getResource("/assets/" + assetDomain + "/" + ".root");
			URI uri = url.toURI();
			if ("file".equals(uri.getScheme())) {
				path = Paths.get(clazz.getResource(assetDir).toURI());
			}
			else {
				if (!"jar".equals(uri.getScheme())) {
					LycanitesMobs.printWarning("", "Unsupported file scheme: " + uri.getScheme());
					return null;
				}
				FileSystem filesystem;
				try {
					filesystem = FileSystems.getFileSystem(uri);
				}
				catch (Exception e) {
					filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
				}
				path = filesystem.getPath(assetDir);
			}
		}
		catch (Exception e) {
			LycanitesMobs.printWarning("", "No data found in: " + assetDir);
			//e.printStackTrace();
		}

		return path;
	}

	/**
	 * Returns a list of ResourceLocations for every file in the provided Path instance.
	 * @param path The directory Path instance to read from.
	 * @param assetDomain The mod domain name.
	 * @param fileType The file extension to use. Ex: "png"
	 * @return
	 */
	public static List<ResourceLocation> getPathResourceLocations(Path path, String assetDomain, String fileType) {
		List<ResourceLocation> resourceLocations = new ArrayList<>();
		try {
			Iterator<Path> iterator = Files.walk(path).iterator();
			while(iterator.hasNext()) {
				Path filePath = iterator.next();
				if (fileType == null || fileType.equals(FilenameUtils.getExtension(filePath.toString()))) {
					Path relativePath = path.relativize(filePath);
					String resourceLocationPath = FilenameUtils.removeExtension(relativePath.toString()).replaceAll("\\\\", "/");
					ResourceLocation resourceLocation = new ResourceLocation(assetDomain, resourceLocationPath);
					resourceLocations.add(resourceLocation);
				}
			}
		}
		catch (Exception e) {
			LycanitesMobs.printWarning("", "There was a problem getting ResourceLocations for: " + path + ", " + fileType + ", " + " \n" + e.toString());
		}

		return resourceLocations;
	}
}
