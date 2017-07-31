package lycanite.lycanitesmobs.swampmobs.mobevent;

import lycanite.lycanitesmobs.core.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import lycanite.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    public void onStart(World world) {
        super.onStart(world);
        if(canAffectWeather) {
            world.getWorldInfo().setRaining(false);
            world.getWorldInfo().setThundering(false);
        }
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity) {
		super.onSpawn(entity);

        List<EntityProjectileRapidFire> projectiles = new ArrayList<EntityProjectileRapidFire>();
        int rapidTime = 20;

        EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 1.0D, 0, (double)entity.posZ + 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 2.0D, 0, 0, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 1.0D, 0, (double)entity.posZ - 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading(0, 0, (double)entity.posZ - 2.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 1.0D, 0, (double)entity.posZ - 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 2.0D, 0, 0, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityVenomShot.class, entity.getEntityWorld(), entity, rapidTime, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 1.0D, 0, (double)entity.posZ + 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        for(EntityProjectileRapidFire projectile : projectiles) {
            projectile.setProjectileScale(1f);

            // Y Offset:
            projectile.posY -= entity.height / 4;

            // Launch:
            entity.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
            entity.getEntityWorld().spawnEntity(projectile);
        }
	}
}
