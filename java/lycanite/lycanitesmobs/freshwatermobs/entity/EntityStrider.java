package lycanite.lycanitesmobs.freshwatermobs.entity;

import lycanite.lycanitesmobs.ExtendedEntity;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityStrider extends EntityCreatureTameable implements IMob {

    protected EntityAIWander wanderAI = new EntityAIWander(this);
    protected EntityAIAttackMelee attackAI;

    protected int pickupCooldown = 100;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityStrider(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 7;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;

        this.setWidth = 4.9F;
        this.setHeight = 10.9F;
        this.setupMob();

        this.stepHeight = 2.0F;
        
        // AI Tasks:
        this.getNavigator().setCanSwim(true);
        this.getNavigator().setAvoidsWater(false);
        this.tasks.addTask(0, new EntityAISwimming(this).setSink(true));
        this.tasks.addTask(2, this.aiSit);
        this.attackAI = new EntityAIAttackMelee(this).setLongMemory(false);
        this.tasks.addTask(3, this.attackAI);
        this.tasks.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(4).setLostDistance(32));
        this.tasks.addTask(5, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("stridertreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(6, new EntityAIStayByWater(this).setSpeed(1.25D));
        this.tasks.addTask(7, wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(3, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class).setCheckSight(false));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setCheckSight(false));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 100D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 1.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.fish), 0.5F).setBurningDrop(new ItemStack(Items.cooked_fished)).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(Items.fish, 1, 3), 0.5F).setBurningDrop(new ItemStack(Items.cooked_fished, 1, 3)).setMaxAmount(2));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if(!this.worldObj.isRemote) {
            // Wander Pause Rates:
            if(this.isInWater())
                this.wanderAI.setPauseRate(120);
            else
                this.wanderAI.setPauseRate(0);

            // Entity Pickup Update:
            this.attackAI.setEnabled(!this.hasPickupEntity());
            if(this.hasPickupEntity()) {
                ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                if(extendedEntity != null)
                    extendedEntity.setPickedUpByEntity(this);
                if(this.ticksExisted % 20 == 0 && this.getRNG().nextBoolean()) {
                    this.attackEntityAsMob(this.getPickupEntity(), 0.5F);
                    if(this.getPickupEntity() instanceof EntityLivingBase) {
                        if(ObjectManager.getPotionEffect("penetration") != null && ObjectManager.getPotionEffect("penetration").id < Potion.potionTypes.length)
                            ((EntityLivingBase)this.getPickupEntity()).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("penetration").id, this.getEffectDuration(5), 1));
                    }
                }
            }
            else if(this.pickupCooldown > 0) {
                this.pickupCooldown--;
            }
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
	@Override
    public float getAISpeedModifier() {
    	if(this.isInWater()) // Checks specifically just for water.
    		return 4.0F;
    	else if(this.waterContact()) // Checks for water, rain, etc.
    		return 1.5F;
    	return 1.0F;
    }
    
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

        // Effect:
        if(target instanceof EntityLivingBase) {
            if(ObjectManager.getPotionEffect("penetration") != null && ObjectManager.getPotionEffect("penetration").id < Potion.potionTypes.length)
                ((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("penetration").id, this.getEffectDuration(5), 1));
        }

        // Pickup:
        if(this.canPickupEntity(target)) {
            this.pickupEntity(target);
            this.pickupCooldown = 100;
        }

        return true;
    }


    // ==================================================
    //                   Taking Damage
    // ==================================================
    // ========== On Damage ==========
    /** Called when this mob has received damage. Here a random blocking chance is applied. **/
    @Override
    public void onDamage(DamageSource damageSrc, float damage) {
        if(this.hasPickupEntity() && this.getRNG().nextFloat() <= 0.25F)
            this.dropPickupEntity();
        super.onDamage(damageSrc, damage);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(ObjectManager.getPotionEffect("Penetration") != null)
            if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Penetration").id) return false;
        if(ObjectManager.getPotionEffect("Paralysis") != null)
            if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Paralysis").id) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public double[] getPickupOffset(Entity entity) {
        return new double[]{0, 5.5D, 0};
    }

    // ========== Pickup ==========
    public boolean canPickupEntity(Entity entity) {
        if(this.pickupCooldown > 0)
            return false;
        return super.canPickupEntity(entity);
    }

    public void dropPickupEntity() {
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
        if(extendedEntity != null)
            extendedEntity.setPickedUpByEntity(null);
        this.pickupEntity = null;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemstack) {
        return itemstack.getItem() == ObjectManager.getItem("stridertreat");
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
