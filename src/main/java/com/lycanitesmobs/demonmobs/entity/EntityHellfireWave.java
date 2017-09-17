package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellfireWave extends EntityProjectileBase {

	// Properties:
    public EntityHellfireWall[][] hellfireWalls;
    protected int hellfireWidth = 5;
    protected int hellfireHeight = 5;
    protected int hellfireSize = 10;
    public int time = 0;
    public int timeMax = 10 * 20;
    public float angle = 90;
    public double rotation = 0;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireWave(World world) {
        super(world);
    }

    public EntityHellfireWave(World world, EntityLivingBase shooterEntity) {
        super(world, shooterEntity);
    }

    public EntityHellfireWave(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellfirewave";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(0);
    	this.setProjectileScale(0F);
        this.setSize(2F, 2F);
        this.movement = false;
        this.pierce = true;
        this.pierceBlocks = true;
        this.projectileLife = 5 * 20;
        this.animationFrameMax = 59;
        this.noClip = true;
        this.waterProof = true;
        this.lavaProof = true;
    }

    @Override
    public boolean isBurning() { return false; }


    // ==================================================
    //                      Update
    // ==================================================
    @Override
    public void onUpdate() {
        if(this.getEntityWorld().isRemote)
            return;

        // Time Update:
        if(this.time++ >= this.timeMax)
            this.setDead();

        // Populate:
        if(this.hellfireWalls == null) {
            hellfireWalls = new EntityHellfireWall[this.hellfireHeight][this.hellfireWidth];
            for(int row = 0; row < this.hellfireHeight; row++) {
                for(int col = 0; col < this.hellfireWidth; col++) {
                    if(this.getThrower() != null)
                        hellfireWalls[row][col] = new EntityHellfireWavePart(this.getEntityWorld(), this.getThrower());
                    else
                        hellfireWalls[row][col] = new EntityHellfireWavePart(this.getEntityWorld(), this.posX, this.posY + 5 + (this.hellfireSize * row), this.posZ);
                    hellfireWalls[row][col].posY = this.posY + (this.hellfireSize * row);
                    this.getEntityWorld().spawnEntity(hellfireWalls[row][col]);
                    hellfireWalls[row][col].setProjectileScale(this.hellfireSize * 2);
                }
            }
        }

        // Move:
        for(int row = 0; row < this.hellfireHeight; row++) {
            for(int col = 0; col < this.hellfireWidth; col++) {
                double rotationRadians = Math.toRadians(((((float)col / this.hellfireWidth) * this.angle) - (this.angle / 2) + this.rotation) % 360);
                double x = (((float)this.time / this.timeMax) * 200) * Math.cos(rotationRadians) - Math.sin(rotationRadians);
                double z = (((float)this.time / this.timeMax) * 200) * Math.sin(rotationRadians) + Math.cos(rotationRadians);
                hellfireWalls[row][col].posX = this.posX + x;
                hellfireWalls[row][col].posY = this.posY + (this.hellfireSize * row);
                hellfireWalls[row][col].posZ = this.posZ + z;
                hellfireWalls[row][col].projectileLife = 2 * 20;
                if(this.isDead)
                    hellfireWalls[row][col].setDead();
            }
        }
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(10) / 20);
    	return true;
    }

    //========== Do Damage Check ==========
    public boolean canDamage(EntityLivingBase targetEntity) {
        EntityLivingBase owner = this.getThrower();
        if(owner == null) {
            if(targetEntity instanceof EntityRahovart)
                return false;
            if(targetEntity instanceof IGroupDemon)
                return false;
        }
        return super.canDamage(targetEntity);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("hellfirewave");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public ResourceLocation getTexture() {
        if(AssetManager.getTexture("hellfirewall") == null)
            AssetManager.addTexture("hellfirewall", this.group, "textures/items/hellfirewall" + ".png");
        return AssetManager.getTexture("hellfirewall");
    }
}
