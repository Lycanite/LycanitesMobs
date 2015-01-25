package lycanite.lycanitesmobs.shadowmobs.entity;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupShadow;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.HashMap;

public class EntityGeist extends EntityCreatureAgeable implements IMob, IGroupShadow {

    public boolean geistShadowfireDeath = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeist(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 0;
        this.experience = 5;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;

        this.geistShadowfireDeath = ConfigBase.getConfig(this.group, "general").getBool("Features", "Geist Shadowfire Death", this.geistShadowfireDeath, "Set to false to disable Geists from bursting into Shadowfire oh death.");

        this.setWidth = 0.6F;
        this.setHeight = 2.4F;
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
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class).setCheckSight(false));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.3D);
		baseAttributes.put("knockbackResistance", 0.0D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 2D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.rotten_flesh), 1).setMaxAmount(3));
        this.drops.add(new DropRate(new ItemStack(Items.ender_pearl), 0.25F).setMaxAmount(2));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("geistliver")), 0.25F).setMaxAmount(1));
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== On Kill ==========
    @Override
    public void onKillEntity(EntityLivingBase par1EntityLivingBase) {
        super.onKillEntity(par1EntityLivingBase);

        if(this.worldObj.difficultySetting.getDifficultyId() >= 2 && par1EntityLivingBase instanceof EntityVillager) {
            if (this.worldObj.difficultySetting.getDifficultyId() == 2 && this.rand.nextBoolean()) return;

            EntityZombie entityzombie = new EntityZombie(this.worldObj);
            entityzombie.copyLocationAndAnglesFrom(par1EntityLivingBase);
            this.worldObj.removeEntity(par1EntityLivingBase);
            entityzombie.onSpawnWithEgg((IEntityLivingData)null);
            entityzombie.setVillager(true);

            if(par1EntityLivingBase.isChild())
                entityzombie.setChild(true);

            this.worldObj.spawnEntityInWorld(entityzombie);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        }
    }


    // ==================================================
    //                      Death
    // ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
        if(!this.worldObj.isRemote && this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing") && this.geistShadowfireDeath) {
            int shadowfireWidth = (int)Math.floor(this.width) + 1;
            int shadowfireHeight = (int)Math.floor(this.height) + 1;
            for(int x = (int)this.posX - shadowfireWidth; x <= (int)this.posX + shadowfireWidth; x++) {
                for(int y = (int)this.posY - shadowfireHeight; y <= (int)this.posY + shadowfireHeight; y++) {
                    for(int z = (int)this.posZ - shadowfireWidth; z <= (int)this.posZ + shadowfireWidth; z++) {
                        Block block = this.worldObj.getBlock(x, y, z);
                        if(block != Blocks.air) {
                            Block upperBlock = this.worldObj.getBlock(x, y + 1, z);
                            if(upperBlock == Blocks.air) {
                                this.worldObj.setBlock(x, y + 1, z, ObjectManager.getBlock("shadowfire"), 0, 3);
                            }
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
        if(potionEffect.getPotionID() == Potion.blindness.id) return false;
        if(ObjectManager.getPotionEffect("Fear") != null)
            if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Fear").id) return false;
        super.isPotionApplicable(potionEffect);
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
		return new EntityGeist(this.worldObj);
	}
}
