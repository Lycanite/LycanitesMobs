package lycanite.lycanitesmobs.demonmobs.entity;

import java.util.HashMap;

import lycanite.lycanitesmobs.DropRate;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAttackMelee;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAttack;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetRevenge;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityNetherSoul extends EntityCreatureBase implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityNetherSoul(World par1World) {
        super(par1World);
        
        // Setup:
        this.entityName = "NetherSoul";
        this.mod = DemonMobs.instance;
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.experience = 5;
        this.hasAttackSound = true;
        
        this.eggName = "DemonEgg";
        
        this.setWidth = 0.6F;
        this.setHeight = 0.8F;
        this.setupMob();
        
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setSpeed(2.0D).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D).setPauseRate(0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(0, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        
        // Drops:
        this.drops.add(new DropRate(Item.bone.itemID, 1).setMinAmount(2).setMaxAmount(2));
        this.drops.add(new DropRate(Item.gunpowder.itemID, 0.5F).setMinAmount(1).setMaxAmount(2));
        this.drops.add(new DropRate(Item.blazePowder.itemID, 0.25F).setMinAmount(1).setMaxAmount(2));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 5D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 40D);
		baseAttributes.put("attackDamage", 4.0D);
        super.applyEntityAttributes(baseAttributes);
    }
    
    
    // ==================================================
   	//                     Updates
   	// ==================================================
    // ========== Living ==========
    @Override
    public void onLivingUpdate() {
        
        // Fire Sound:
        if(this.rand.nextInt(24) == 0)
            this.worldObj.playSoundEffect(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F);
        
        // Particles:
        if(this.worldObj.isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.worldObj.spawnParticle("largesmoke", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	            this.worldObj.spawnParticle("flame", this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
        
        super.onLivingUpdate();
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean canFly() { return true; }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
		if(!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
			this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 1, true);
        super.onDeath(par1DamageSource);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
        if(par1PotionEffect.getPotionID() == Potion.wither.id) return false;
        super.isPotionApplicable(par1PotionEffect);
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
