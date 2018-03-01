package com.lycanitesmobs.infernomobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupIce;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.api.IGroupWater;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLobber extends EntityCreatureBase implements IMob, IGroupFire {

	EntityAIWander wanderAI;
    public boolean lobberMelting = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLobber(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;

        this.lobberMelting = ConfigBase.getConfig(this.creatureInfo.group, "general").getBool("Features", "Rare Lobber Melting", this.lobberMelting, "Set to false to disable Umber Lobbers melting certain blocks.");
        this.setupMob();

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(1, new EntityAIAttackRanged(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
        this.tasks.addTask(2, new EntityAIStayByWater(this).setSpeed(1.25D));
        this.wanderAI = new EntityAIWander(this);
        this.tasks.addTask(6, wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupIce.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupWater.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntitySnowman.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPlant.class));
    }

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 3, height * 3);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 3;
        }
        return this.sizeScale;
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Wander Pause Rates:
        if(!this.getEntityWorld().isRemote) {
            if (this.lavaContact())
                this.wanderAI.setPauseRate(120);
            else
                this.wanderAI.setPauseRate(0);
        }
        
        // Trail:
        if(!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
        	int trailHeight = 1;
            int trailWidth = 1;
            if(this.getSubspeciesIndex() >= 3)
                trailWidth = 3;
        	for(int y = 0; y < trailHeight; y++) {
        		Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
        		if(block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW_LAYER || block == Blocks.TALLGRASS || block == ObjectManager.getBlock("frostfire") || block == ObjectManager.getBlock("icefire")) {
                    if(trailWidth == 1)
                        this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), Blocks.FIRE.getDefaultState());
                    else
                        for(int x = -(trailWidth / 2); x < (trailWidth / 2) + 1; x++) {
                            for(int z = -(trailWidth / 2); z < (trailWidth / 2) + 1; z++) {
                                this.getEntityWorld().setBlockState(this.getPosition().add(x, y, z), Blocks.FIRE.getDefaultState());
                            }
                        }
                }
        	}
		}

        // Rare Subspecies Powers:
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() >= 3 && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.lobberMelting && this.ticksExisted % 10 == 0) {

            // Melt Blocks:
            int range = 2;
            for(int w = -((int)Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++)
                for(int d = -((int)Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++)
                    for(int h = 0; h <= Math.ceil(this.height); h++) {
                        Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
                        if(block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE || block == Blocks.DIRT || block == Blocks.PLANKS || block == Blocks.GRAVEL || block == Blocks.SAND) {
							IBlockState blockState = Blocks.FLOWING_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
                            if(block == Blocks.OBSIDIAN)
                                blockState = Blocks.LAVA.getDefaultState();
                            this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
                        }
                    }

            // Random Projectiles:
            if(this.ticksExisted % 40 == 0) {
                EntityProjectileBase projectile = new EntityMagma(this.getEntityWorld(), this);
                projectile.setProjectileScale(2f);
                projectile.shoot((2 * this.getRNG().nextFloat()) - 1, this.getRNG().nextFloat(), (2 * this.getRNG().nextFloat()) - 1, 1.2F, 6.0F);
                this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                this.getEntityWorld().spawnEntity(projectile);
            }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote) {
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
	        if(this.ticksExisted % 10 == 0)
		        for(int i = 0; i < 2; ++i) {
		            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
		        }
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.lavaContact())
    		return 2.0F;
    	return 1.0F;
    }
    
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		int waterWeight = 10;
		BlockPos pos = new BlockPos(x, y, z);
        if(this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
        	return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
		if(this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.FLOWING_LAVA)
			return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(x, y, z);
        if(this.lavaContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(x, y, z);
    }
	
	// Pushed By Water:
	@Override
	public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(IGroupFire.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityMagma.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    // ========== Is Aggressive ==========
    @Override
    public boolean isAggressive() {
    	if(this.getAir() <= -100)
    		return false;
    	return super.isAggressive();
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return this.getSubspeciesIndex() < 3; }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                       Drops
   	// ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(EntityItemCustom entityitem) {
    	entityitem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
