package com.lycanitesmobs.infernomobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class EntityKhalk extends EntityCreatureTameable implements IMob, IGroupFire, IGroupHeavy {

    public boolean khalkLavaDeath = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityKhalk(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 5;
        this.experience = 10;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;

        this.khalkLavaDeath = ConfigBase.getConfig(this.group, "general").getBool("Features", "Khalk Lava Death", this.khalkLavaDeath, "Set to false to disable Khalks from turning into a pile of lava on death.");

        this.setWidth = 4.5F;
        this.setHeight = 3.5F;
        this.solidCollision = true;
        this.setupMob();

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setRate(20));
        this.tasks.addTask(3, new EntityAIStayByWater(this).setSpeed(1.25D));
        this.tasks.addTask(4, this.aiSit);
        this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupIce.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupWater.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySnowman.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(IGroupPlant.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 40D);
		baseAttributes.put("movementSpeed", 0.20D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.COAL), 1.0F).setMaxAmount(32));
        this.drops.add(new DropRate(new ItemStack(Items.MAGMA_CREAM), 0.75F).setMaxAmount(5));
        this.drops.add(new DropRate(new ItemStack(Items.BLAZE_POWDER), 0.5F).setMaxAmount(8));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Lunging:
        if(this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.1D, this.getAttackTarget());
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
	// ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Damage Effect:
    	target.setFire(this.getEffectDuration(5) / 20);
        return true;
    }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
		if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.khalkLavaDeath && !this.isTamed()) {
			int lavaWidth = (int)Math.floor(this.width) - 1;
			int lavaHeight = (int)Math.floor(this.height) - 1;
			for(int x = (int)this.posX - lavaWidth; x <= (int)this.posX + lavaWidth; x++) {
				for(int y = (int)this.posY; y <= (int)this.posY + lavaHeight; y++) {
					for(int z = (int)this.posZ - lavaWidth; z <= (int)this.posZ + lavaWidth; z++) {
						Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
						if(block == Blocks.AIR) {
							IBlockState blockState = Blocks.FLOWING_LAVA.getStateFromMeta(11);
							if(x == (int)this.posX && y == (int)this.posY && z == (int)this.posZ)
								blockState = Blocks.FLOWING_LAVA.getStateFromMeta(12);
							this.getEntityWorld().setBlockState(new BlockPos(x, y, z), blockState, 3);
						}
					}
				}
			}
		}
        super.onDeath(damageSource);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("Penetration") != null)
            if(potionEffect.getPotion() == ObjectManager.getPotionEffect("Penetration")) return false;
        super.isPotionApplicable(potionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
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
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityKhalk(this.getEntityWorld());
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
