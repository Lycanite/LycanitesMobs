package com.lycanitesmobs.saltwatermobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.DropRate;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityRoa extends EntityCreatureRideable implements IMob, IGroupPredator {

	EntityAIWander wanderAI;
    protected int whirlpoolRange = 6;
    protected int whirlpoolEnergy = 0;
    protected int whirlpoolEnergyMax = 5 * 20;
    protected boolean whirlpoolRecharging = true;
    protected int mountedWhirlpool = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRoa(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 1;
        this.experience = 4;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;
        
        this.setWidth = 2.3F;
        this.setHeight = 1.8F;
        this.setupMob();

        // Stats:
        this.stepHeight = 1.0F;

        this.whirlpoolRange = ConfigBase.getConfig(this.group, "general").getInt("Features", "Roa Whirlpool Range", this.whirlpoolRange, "The range (in blocks) of the Roa's whirlpool pull effect, set to 0 to disable, note that the Roa is nearly 2 blocks in size itself which the range must cover.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(3, new EntityAITempt(this).setItem(new ItemStack(ObjectManager.getItem("roatreat"))).setTemptDistanceMin(4.0D));
        this.tasks.addTask(4, new EntityAIAttackMelee(this).setLongMemory(false));
        this.wanderAI = new EntityAIWander(this);
        this.tasks.addTask(6, wanderAI.setPauseRate(60));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(MobInfo.predatorsAttackAnimals) {
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
            this.targetTasks.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntitySquid.class));
        }
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.FISH, 1, 1), 0.5F).setMinAmount(1).setMaxAmount(2).setBurningDrop(new ItemStack(Items.COOKED_FISH, 1, 1)));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Whirlpool:
        if(!this.getEntityWorld().isRemote) {
            if(this.whirlpoolRecharging) {
                if(++this.whirlpoolEnergy >= this.whirlpoolEnergyMax)
                    this.whirlpoolRecharging = false;
            }
            this.whirlpoolEnergy = Math.min(this.whirlpoolEnergy, this.whirlpoolEnergyMax);
            if(this.canWhirlpool()) {
                for (EntityLivingBase entity : this.getNearbyEntities(EntityLivingBase.class, null, this.whirlpoolRange)) {
                    if (entity == this || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity.isPotionActive(ObjectManager.getPotionEffect("weight")))
                        continue;
                    EntityPlayerMP player = null;
                    if (entity instanceof EntityPlayerMP) {
                        player = (EntityPlayerMP) entity;
                        if (player.capabilities.isCreativeMode)
                            continue;
                    }
                    double xDist = this.posX - entity.posX;
                    double zDist = this.posZ - entity.posZ;
                    double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
                    double factor = 0.25D;
                    entity.addVelocity(
                            xDist / xzDist * factor + entity.motionX * factor,
                            0,
                            zDist / xzDist * factor + entity.motionZ * factor
                    );
                    if (player != null)
                        player.connection.sendPacket(new SPacketEntityVelocity(entity));
                }
                if(--this.whirlpoolEnergy <= 0)
                    this.whirlpoolRecharging = true;
            }
        }

        if(this.mountedWhirlpool > 0)
            this.mountedWhirlpool--;
    }

    @Override
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, (5 * 20) + 5, 1));
        if(rider.isPotionActive(MobEffects.BLINDNESS))
            rider.removePotionEffect(MobEffects.BLINDNESS);
        if(rider.isPotionActive(ObjectManager.getPotionEffect("weight")))
            rider.removePotionEffect(ObjectManager.getPotionEffect("weight"));
    }

    // ========== Extra Animations ==========
    /** An additional animation boolean that is passed to all clients through the animation mask. **/
    public boolean extraAnimation01() {
        if(this.getEntityWorld().isRemote) {
            return super.extraAnimation01();
        }
        return this.canWhirlpool();
    }

    // ========== Whirlpool ==========
    public boolean canWhirlpool() {
        if(this.getEntityWorld().isRemote) {
            return this.extraAnimation01();
        }

        // Out of Water:
        if(!this.isInWater()) {
            return false;
        }

        // Mounted:
        if(this.getControllingPassenger() != null && this.mountedWhirlpool > 0) {
            return true;
        }

        // Attack Target:
        return !this.whirlpoolRecharging && this.hasAttackTarget() && this.getDistanceToEntity(this.getAttackTarget()) <= (this.whirlpoolRange * 3);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(block == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }
	
	// Swimming:
	@Override
	public boolean isStrongSwimmer() {
		return true;
	}
	
	// Walking:
	@Override
	public boolean canWalk() {
		return false;
	}

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.25D;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
    	if(ObjectManager.getPotionEffect("weight") != null)
        	if(potionEffect.getPotion() == ObjectManager.getPotionEffect("weight")) return false;
        if(potionEffect.getPotion() == MobEffects.BLINDNESS) return false;
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


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.getStamina() < this.getStaminaCost()) {
            return;
        }

        this.applyStaminaCost();
        this.mountedWhirlpool = 20;
    }

    @Override
    public float getStaminaCost() {
        return 1;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 0;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 2.0F;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    // Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 5 * 20, 1));
        }
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }


    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityRoa(this.getEntityWorld());
    }


    // ==================================================
    //                       Taming
    // ==================================================
    @Override
    public boolean isTamingItem(ItemStack itemStack) {
        if(itemStack == null)
            return false;
        return itemStack.getItem() == ObjectManager.getItem("roatreat");
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack) || ObjectLists.inItemList("cookedfish", testStack);
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return false; }
}
