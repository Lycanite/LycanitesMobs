package com.lycanitesmobs.infernomobs.mobevent;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.infernomobs.entity.EntityMagma;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MobEventEruption extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventEruption(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world, int rank) {
        super.onStart(world, rank);
        if(canAffectWeather) {
            world.getWorldInfo().setRaining(false);
            world.getWorldInfo().setThundering(false);
        }
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity, int rank) {
		super.onSpawn(entity, rank);

        List<EntityProjectileBase> projectiles = new ArrayList<EntityProjectileBase>();
        int rapidTime = 20;

        EntityProjectileBase projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX + 3.0D, 0, entity.posZ + 3.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX + 3.0D, 0, entity.posZ, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX, 0, entity.posZ + 3.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX - 3.0D, 0, entity.posZ - 3.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX - 3.0D, 0, entity.posZ, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX, 0, entity.posZ - 3.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX + 3.0D, 0, entity.posZ - 3.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityMagma(entity.worldObj, entity);
        projectileEntry.setThrowableHeading(entity.posX - 3.0D, 0, entity.posZ + 3.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        for(EntityProjectileBase projectile : projectiles) {
            projectile.setProjectileScale(1f);

            // Y Offset:
            projectile.posY -= entity.height / 4;

            // Launch:
            entity.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (entity.getRNG().nextFloat() * 0.4F + 0.8F));
            entity.worldObj.spawnEntityInWorld(projectile);
        }
	}
}
