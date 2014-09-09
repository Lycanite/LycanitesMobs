package lycanite.lycanitesmobs.api.mobevent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileRapidFire;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class MobEventCinderfall extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventCinderfall(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    public void onStart(World world) {
        super.onStart(world);
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

                EntityProjectileRapidFire projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 0.5D, 0, (double)entity.posZ + 0.5D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 1.0D, 0, 0, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading((double)entity.posX + 0.5D, 0, (double)entity.posZ - 0.5D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading(0, 0, (double)entity.posZ - 1.0D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 0.5D, 0, (double)entity.posZ - 0.5D, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 1.0D, 0, 0, 1.1F, 6.0F);
        projectiles.add(projectileEntry);

        projectileEntry = new EntityProjectileRapidFire(EntityEmber.class, entity.worldObj, entity, 15, 3);
        projectileEntry.setThrowableHeading((double)entity.posX - 0.5D, 0, (double)entity.posZ + 0.5D, 1.1F, 6.0F);
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
