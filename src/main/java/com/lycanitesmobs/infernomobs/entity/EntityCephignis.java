package com.lycanitesmobs.infernomobs.entity;

import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.info.MobDrop;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
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

public class EntityCephignis extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IGroupFire {

	EntityAIWander wanderAI;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCephignis(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.defense = 0;
        this.experience = 5;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;

        this.setWidth = 1.8F;
        this.setHeight = 0.8F;
        this.fleeHealthPercent = 1.0F;
        this.isHostileByDefault = false;
        this.setupMob();

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(2, new EntityAITempt(this).setItemList("Fuel"));
        this.tasks.addTask(3, new EntityAIStayByWater(this));
        this.tasks.addTask(4, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(5, new EntityAIMate(this));
        this.tasks.addTask(6, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.wanderAI = new EntityAIWander(this);
        this.tasks.addTask(7, this.wanderAI);
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10D);
		baseAttributes.put("movementSpeed", 0.24D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 32D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new MobDrop(new ItemStack(ObjectManager.getItem("CephignisMeatCooked")), 1).setBurningDrop(new ItemStack(ObjectManager.getItem("CephignisMeatCooked"))).setMinAmount(2).setMaxAmount(5));
        this.drops.add(new MobDrop(new ItemStack(Items.MAGMA_CREAM, 1, 0), 0.25F).setMinAmount(1).setMaxAmount(2));
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
            if (this.isInWater())
                this.wanderAI.setPauseRate(20);
            else
                this.wanderAI.setPauseRate(0);
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// Pathing Weight:
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        if(this.getEntityWorld().getBlockState(pos).getBlock() == ObjectManager.getBlock("purelava"))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 2);
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

    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(EntityPlayer player) { return true; }

    // ========== Can Be Tempted ==========
    @Override
    public boolean canBeTempted() {
        if(this.getAir() <= -100)
            return false;
        else return super.canBeTempted();
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type) {
        if(type.equals("inWall")) return false;
            return super.isDamageTypeApplicable(type);
    }

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
        return false;
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
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityCephignis(this.getEntityWorld());
    }

    // ========== Breeding Item ==========
    @Override
    public boolean isBreedingItem(ItemStack testStack) {
        if(this.getAir() <= -100)
            return false;
        return ObjectLists.inItemList("Fuel", testStack);
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
