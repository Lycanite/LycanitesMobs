package lycanite.lycanitesmobs.swampmobs.mobevent;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventWingedVenom extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventWingedVenom(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    public void onStart(World world) {
        super.onStart(world);
		world.getWorldInfo().setRaining(false);
		world.getWorldInfo().setThundering(false);
    }


    // ==================================================
    //                      Finish
    // ==================================================
    public void onFinish() {
        super.onFinish();
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);

        List<EntityProjectileRapidFire> projectiles = new ArrayList<EntityProjectileRapidFire>();
        int rapidTime = 20;

        EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 1.0D, 0, (double)entity.posZ + 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 2.0D, 0, 0, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 1.0D, 0, (double)entity.posZ - 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading(0, 0, (double)entity.posZ - 2.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 1.0D, 0, (double)entity.posZ - 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 2.0D, 0, 0, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 1.0D, 0, (double)entity.posZ + 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        for(EntityProjectileRapidFire projectile : projectiles) {
            projectile.setProjectileScale(1f);

            // Y Offset:
            projectile.posY -= entity.height / 4;

            // Launch:
            entity.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
            entity.worldObj.spawnEntityInWorld(projectile);
        }
	}
}
