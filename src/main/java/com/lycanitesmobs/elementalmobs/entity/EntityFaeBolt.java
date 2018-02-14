package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFaeBolt extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityFaeBolt(World world) {
        super(world);
    }

    public EntityFaeBolt(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityFaeBolt(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "faebolt";
    	this.group = ElementalMobs.instance.group;
    	this.setBaseDamage(0);
    	this.setProjectileScale(4F);
    	this.waterProof = true;
    }


	// ==================================================
	//                   Movement
	// ==================================================
	// ========== Gravity ==========
	@Override
	protected float getGravityVelocity() {
		return 0.001F;
	}
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	Potion lifeleak = ObjectManager.getPotionEffect("lifeleak");
    	if(lifeleak != null)
            entityLiving.addPotionEffect(new PotionEffect(lifeleak, this.getEffectDuration(3), 0));
        return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
			this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
					this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
					this.posY + this.rand.nextDouble() * (double) this.height,
					this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
					0.0D, 0.0D, 0.0D,
					Blocks.TALLGRASS.getStateId(Blocks.YELLOW_FLOWER.getDefaultState()));
			this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
					this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
					this.posY + this.rand.nextDouble() * (double) this.height,
					this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
					0.0D, 0.0D, 0.0D,
					Blocks.TALLGRASS.getStateId(Blocks.RED_FLOWER.getDefaultState()));
    	}
    }


	// ==================================================
	//                      Damage
	// ==================================================
	@Override
	public float getDamage(Entity entity) {
		return 0;
	}


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public String getTextureName() {
        return this.entityName.toLowerCase() + "charge";
    }
}
