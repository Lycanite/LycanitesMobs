package lycanite.lycanitesmobs;

import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Utilities {
	
	// ==================================================
  	//                      Raytrace
  	// ==================================================
	// ========== Raytrace All ==========
    public static MovingObjectPosition raytrace(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
		Vec3 startVec = Vec3.createVectorHelper(x, y, z);
		Vec3 lookVec = Vec3.createVectorHelper(tx - x, ty - y, tz - z);
		Vec3 endVec = Vec3.createVectorHelper(tx, ty, tz);
		float minX = (float)(x < tx ? x : tx);
		float minY = (float)(y < ty ? y : ty);
		float minZ = (float)(z < tz ? z : tz);
		float maxX = (float)(x > tx ? x : tx);
		float maxY = (float)(y > ty ? y : ty);
		float maxZ = (float)(z > tz ? z : tz);

		// Get Block Collision:
		MovingObjectPosition collision = world.rayTraceBlocks(startVec, endVec, false);
		startVec = Vec3.createVectorHelper(x, y, z);
		endVec = Vec3.createVectorHelper(tx, ty, tz);
		float maxDistance = (float)endVec.distanceTo(startVec);
		if(collision != null)
			maxDistance = (float)collision.hitVec.distanceTo(startVec);

		// Get Entity Collision:
		if(excluded != null) {
			AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
			List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(null, bb);
			Entity closestHitEntity = null;
			float closestHit = Float.POSITIVE_INFINITY;
			float currentHit = 0.0f;
			AxisAlignedBB entityBb;
			MovingObjectPosition intercept;
			for(Entity ent : allEntities) {
				if(ent.canBeCollidedWith() && !excluded.contains(ent)) {
					float entBorder = ent.getCollisionBorderSize();
					entityBb = ent.boundingBox;
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
				collision = new MovingObjectPosition(closestHitEntity);
		}
		
		return collision;
    }

    public static MovingObjectPosition raytraceEntities(World world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
		Vec3 startVec = Vec3.createVectorHelper(x, y, z);
		Vec3 lookVec = Vec3.createVectorHelper(tx - x, ty - y, tz - z);
		Vec3 endVec = Vec3.createVectorHelper(tx, ty, tz);
		float minX = (float)(x < tx ? x : tx);
		float minY = (float)(y < ty ? y : ty);
		float minZ = (float)(z < tz ? z : tz);
		float maxX = (float)(x > tx ? x : tx);
		float maxY = (float)(y > ty ? y : ty);
		float maxZ = (float)(z > tz ? z : tz);

		// Get Entities and Raytrace Blocks:
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
		List<Entity> allEntities = world.getEntitiesWithinAABBExcludingEntity(
				null, bb);
		MovingObjectPosition collision = world.rayTraceBlocks(startVec, endVec, false);

		// Get Entity Collision:
		Entity closestHitEntity = null;
		float closestHit = Float.POSITIVE_INFINITY;
		float currentHit = 0.0f;
		AxisAlignedBB entityBb;
		MovingObjectPosition intercept;
		for(Entity ent : allEntities) {
			if(ent.canBeCollidedWith() && !excluded.contains(ent)) {
				float entBorder = ent.getCollisionBorderSize();
				entityBb = ent.boundingBox;
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
			collision = new MovingObjectPosition(closestHitEntity);
		return collision;
    }
}
