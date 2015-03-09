package lycanite.lycanitesmobs.demonmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityTrite extends EntityCreatureBase implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTrite(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 0;
        this.experience = 2;
        this.hasAttackSound = true;
        
        this.setWidth = 0.9F;
        this.setHeight = 0.6F;
        this.setupMob();
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setRate(10));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityChicken.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(3, 1));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(3, 1));
        }
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.36D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.string), 1).setMinAmount(1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.spider_eye), 0.5F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.fermented_spider_eye), 0.25F));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Leap:
        if(this.hasAttackTarget() && this.onGround && !this.worldObj.isRemote && this.rand.nextInt(10) == 0)
        	this.leap(6.0F, 0.6D, this.getAttackTarget());
    }
    
    
    // ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityAstaroth.class))
    		return false;
        return super.canAttackClass(targetClass);
    }
    
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Wither:
        if(target instanceof EntityLivingBase) {
        	((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.wither.id, this.getEffectDuration(5), 0));
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    @Override
    public boolean canClimb() { return true; }
    
    
    // ==================================================
  	//                     Immunities
  	// ==================================================
    // ========== Damage ==========
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.wither.id) return false;
        return super.isPotionApplicable(par1PotionEffect);
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    // ========== Environmental ==========
    @Override
    public boolean webproof() { return true; }
}
