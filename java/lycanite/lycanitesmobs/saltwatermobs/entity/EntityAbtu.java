package lycanite.lycanitesmobs.saltwatermobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIStayByWater;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetOwnerThreats;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAbtu extends EntityCreatureTameable implements IMob, IGroupPredator {
	
	EntityAIWander wanderAI = new EntityAIWander(this);
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAbtu(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 4;
        this.spawnsInDarkness = true;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;
        
        this.eggName = "SaltwaterEgg";
        this.babySpawnChance = 0.9D;
        this.canGrow = true;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.4F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setAvoidsWater(false);
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false).setRate(10));
        this.tasks.addTask(6, wanderAI.setPauseRate(0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class).setPackHuntingScale(1, 3));
        }
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 7D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.fish, 1, 1), 0.5F).setMinAmount(1).setMaxAmount(2).setBurningDrop(new ItemStack(Items.cooked_fished, 1, 1)));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
		// Summon Allies:
        if(this.hasAttackTarget() && this.ticksExisted % 20 == 0) {
			this.allyUpdate();
		}

        // Random Leaping:
        if(!this.worldObj.isRemote) {
            if(this.hasAttackTarget() && this.isChild()) {
                if(this.rand.nextInt(10) == 0)
                    this.leap(4.0F, 0.6D, this.getAttackTarget());
            }
        }
		
        super.onLivingUpdate();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.worldObj.isRemote)
			return;
		
		// Spawn Minions:
		int swarmLimit = ConfigBase.getConfig(this.group, "general").getInt("Features", "Abtu Swarm Limit", 20, "Limits how many Abtu there can be when swarming.");
		if(swarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < swarmLimit) {
			float random = this.rand.nextFloat();
			float spawnChance = 0.1F;
			if(!this.isChild())
				spawnChance = 0.5F;
			if(random <= spawnChance)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	EntityCreatureAgeable minion = new EntityAbtu(this.worldObj);
    	minion.setGrowingAge(minion.growthTime);
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	minion.setMinion(true);
    	this.worldObj.spawnEntityInWorld(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		int waterWeight = 10;
		
        if(this.worldObj.getBlock(par1, par2, par3) == Blocks.water)
        	return (super.getBlockPathWeight(par1, par2, par3) + 1) * (waterWeight + 1);
		if(this.worldObj.getBlock(par1, par2, par3) == Blocks.flowing_water)
			return (super.getBlockPathWeight(par1, par2, par3) + 1) * waterWeight;
        if(this.worldObj.isRaining() && this.worldObj.canBlockSeeTheSky(par1, par2, par3))
        	return (super.getBlockPathWeight(par1, par2, par3) + 1) * (waterWeight + 1);
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(par1, par2, par3);
        if(this.waterContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(par1, par2, par3);
    }
	
	// Swimming:
	@Override
	public boolean canSwim() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}
    
    
    // ==================================================
    //                       Death
    // ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
    	allyUpdate();
        super.onDeath(par1DamageSource);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
    	if(ObjectManager.getPotionEffect("Weight") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        if(potionEffect.getPotionID() == Potion.blindness.id) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }
}
