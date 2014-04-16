package lycanite.lycanitesmobs.swampmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIBreakDoor;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMoveVillage;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityGhoulZombie extends EntityCreatureAgeable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGhoulZombie(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "GhoulZombie";
        this.mod = SwampMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 0;
        this.experience = 5;
        this.spawnsInDarkness = true;
        this.hasAttackSound = false;
        this.spreadFire = true;
        
        this.eggName = "SwampEgg";
        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.6F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setTargetClass(EntityPlayer.class).setLongMemory(false));
        this.tasks.addTask(4, new EntityAIAttackMelee(this));
        this.tasks.addTask(6, new EntityAIMoveVillage(this));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setSightCheck(false));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 15D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(Item.rottenFlesh.itemID, 1).setMaxAmount(3));
        this.drops.add(new DropRate(Item.coal.itemID, 0.25F).setMaxAmount(2));
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean meleeAttack(Entity target, double damageScale) {
    	if(!super.meleeAttack(target, damageScale))
    		return false;
    	
    	// Wither:
        if(target instanceof EntityLivingBase) {
            byte effectSeconds = 5;
            if(this.worldObj.difficultySetting > 1)
                if (this.worldObj.difficultySetting == 2)
                	effectSeconds = 7;
                else if (this.worldObj.difficultySetting == 3)
                	effectSeconds = 10;
            if(effectSeconds > 0)
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(Potion.poison.id, effectSeconds * 20, 0));
        }
        
        return true;
    }
    
    // ========== On Kill ==========
    @Override
    public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
        super.onKillEntity(par1EntityLivingBase);

        if(this.worldObj.difficultySetting >= 2 && par1EntityLivingBase instanceof EntityVillager) {
            if (this.worldObj.difficultySetting == 2 && this.rand.nextBoolean()) return;

            EntityZombie entityzombie = new EntityZombie(this.worldObj);
            entityzombie.copyLocationAndAnglesFrom(par1EntityLivingBase);
            this.worldObj.removeEntity(par1EntityLivingBase);
            entityzombie.onSpawnWithEgg((EntityLivingData)null);
            entityzombie.setVillager(true);

            if(par1EntityLivingBase.isChild())
                entityzombie.setChild(true);

            this.worldObj.spawnEntityInWorld(entityzombie);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        }
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.poison.id) return false;
        if(par1PotionEffect.getPotionID() == Potion.blindness.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    @Override
    public boolean daylightBurns() { return !this.isChild(); }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityGhoulZombie(this.worldObj);
	}
}
