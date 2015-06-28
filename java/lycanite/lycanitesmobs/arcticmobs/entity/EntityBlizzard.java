package lycanite.lycanitesmobs.arcticmobs.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityBlizzard extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityBlizzard(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityBlizzard(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityBlizzard(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.setSize(0.3125F, 0.3125F);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "blizzard";
    	this.group = ArcticMobs.group;
    	this.setBaseDamage(1);
    	this.setProjectileScale(0.5F);
        this.knockbackChance = 0D;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, this.getEffectDuration(5), 0));
    	return true;
    }

    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(int x, int y, int z) {
        if(this.worldObj.getBlock(x, y, z) == Blocks.snow)
            return true;
        if(this.worldObj.getBlock(x, y, z) == Blocks.fire)
            return true;
        if(this.worldObj.getBlock(x, y, z) == Blocks.tallgrass)
            return true;
        if(ObjectManager.getBlock("PoisonCloud") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("PoisonCloud"))
            return true;
        if(ObjectManager.getBlock("FrostCloud") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("FrostCloud"))
            return true;
        if(ObjectManager.getBlock("Frostweb") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Frostweb"))
            return true;
        if(ObjectManager.getBlock("QuickWeb") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("QuickWeb"))
            return true;
        if(ObjectManager.getBlock("Hellfire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Hellfire"))
            return true;
        if(ObjectManager.getBlock("Frostfire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Frostfire"))
            return true;
        if(ObjectManager.getBlock("Icefire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Icefire"))
            return true;
        if(ObjectManager.getBlock("Scorchfire") != null && this.worldObj.getBlock(x, y, z) == ObjectManager.getBlock("Scorchfire"))
            return true;
        return super.canDestroyBlock(x, y, z);
    }

    //========== Place Block ==========
    @Override
    public void placeBlock(World world, int x, int y, int z) {
        String blockName = "icefire";
        if(this.getThrower() != null && this.getThrower() instanceof EntitySerpix) {
            EntitySerpix entitySerpix = (EntitySerpix)this.getThrower();
            if(!entitySerpix.isTamed())
                blockName = "frostfire";
        }
        world.setBlock(x, y, z, ObjectManager.getBlock(blockName));
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.worldObj.spawnParticle("snowshovel", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }
}
