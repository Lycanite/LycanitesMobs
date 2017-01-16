package lycanite.lycanitesmobs.desertmobs.entity;

import lycanite.lycanitesmobs.api.IGroupAlpha;
import lycanite.lycanitesmobs.api.IGroupHunter;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.IGroupPrey;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.ai.*;
import lycanite.lycanitesmobs.core.info.DropRate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityGorgomite extends EntityCreatureBase implements IMob, IGroupPrey {
	private EntityAIAttackRanged rangedAttackAI;
	private int gorgomiteSwarmLimit = 10;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGorgomite(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.defense = 0;
        this.experience = 3;
        this.hasAttackSound = true;
        
        this.setWidth = 1.3F;
        this.setHeight = 0.9F;
        this.setupMob();
        
        this.gorgomiteSwarmLimit = ConfigBase.getConfig(this.group, "general").getInt("Features", "Gorgomite Swarm Limit", this.gorgomiteSwarmLimit, "Limits how many Gorgomites there can be when swarming.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.tasks.addTask(3, new EntityAIAttackMelee(this).setRate(20).setLongMemory(true));
        this.tasks.addTask(6, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.28D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 1D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Blocks.SANDSTONE), 1).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Blocks.STONE), 1).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.FLINT), 0.5F));
        this.drops.add(new DropRate(new ItemStack(Blocks.IRON_ORE), 0.1F));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
		if(!this.worldObj.isRemote && this.hasAttackTarget() && this.ticksExisted % 20 == 0) {
			this.allyUpdate();
		}
        
        super.onLivingUpdate();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.worldObj.isRemote)
			return;
		
		// Spawn Minions:
		if(this.gorgomiteSwarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < this.gorgomiteSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.25F)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	EntityLivingBase minion = new EntityGorgomite(this.worldObj);
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof EntityCreatureBase) {
    		((EntityCreatureBase)minion).setMinion(true);
    		((EntityCreatureBase)minion).setSubspecies(this.getSubspeciesIndex(), true);
    	}
    	this.worldObj.spawnEntityInWorld(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(IGroupAlpha.class))
        	return false;
        if(targetClass.isAssignableFrom(IGroupPredator.class))
            return false;
    	return super.canAttackClass(targetClass);
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
    public boolean isDamageTypeApplicable(String type) {
    	if(type.equals("cactus")) return false;
    	return super.isDamageTypeApplicable(type);
    }
    
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotion() == MobEffects.HUNGER) return false;
        if(potionEffect.getPotion() == MobEffects.WEAKNESS) return false;
        return super.isPotionApplicable(potionEffect);
    }
}
