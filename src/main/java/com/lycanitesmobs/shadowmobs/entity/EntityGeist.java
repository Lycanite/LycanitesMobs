package com.lycanitesmobs.shadowmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityGeist extends EntityCreatureAgeable implements IMob, IGroupShadow {

    public boolean geistShadowfireDeath = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeist(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;

        this.geistShadowfireDeath = ConfigBase.getConfig(this.creatureInfo.group, "general").getBool("Features", "Geist Shadowfire Death", this.geistShadowfireDeath, "Set to false to disable Geists from bursting into Shadowfire oh death.");
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
            pathNavigateGround.setAvoidSun(true);
        }
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
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== On Kill ==========
    @Override
    public void onKillEntity(EntityLivingBase entityLivingBase) {
        super.onKillEntity(entityLivingBase);

        if(this.getEntityWorld().getDifficulty().getDifficultyId() >= 2 && entityLivingBase instanceof EntityVillager) {
            if (this.getEntityWorld().getDifficulty().getDifficultyId() == 2 && this.rand.nextBoolean()) return;

            EntityVillager entityvillager = (EntityVillager)entityLivingBase;
            EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.getEntityWorld());
            entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
            this.getEntityWorld().removeEntity(entityvillager);
            entityzombievillager.onInitialSpawn(this.getEntityWorld().getDifficultyForLocation(new BlockPos(entityzombievillager)), new EntityCreatureBase.GroupData(false));
            entityzombievillager.setProfession(entityvillager.getProfession());
            entityzombievillager.setChild(entityvillager.isChild());
            entityzombievillager.setNoAI(entityvillager.isAIDisabled());

            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomNameTag(entityvillager.getCustomNameTag());
                entityzombievillager.setAlwaysRenderNameTag(entityvillager.getAlwaysRenderNameTag());
            }

            this.getEntityWorld().spawnEntity(entityzombievillager);
            this.getEntityWorld().playEvent(null, 1016, entityzombievillager.getPosition(), 0);
        }
    }


    // ==================================================
    //                      Death
    // ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
        try {
            if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.geistShadowfireDeath) {
                int shadowfireWidth = (int)Math.floor(this.width) + 1;
                int shadowfireHeight = (int)Math.floor(this.height) + 1;
                for(int x = (int)this.posX - shadowfireWidth; x <= (int)this.posX + shadowfireWidth; x++) {
                    for(int y = (int)this.posY - shadowfireHeight; y <= (int)this.posY + shadowfireHeight; y++) {
                        for(int z = (int)this.posZ - shadowfireWidth; z <= (int)this.posZ + shadowfireWidth; z++) {
                            Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                            if(block != Blocks.AIR) {
                                BlockPos placePos = new BlockPos(x, y + 1, z);
                                Block upperBlock = this.getEntityWorld().getBlockState(placePos).getBlock();
                                if(upperBlock == Blocks.AIR) {
                                    this.getEntityWorld().setBlockState(placePos, ObjectManager.getBlock("shadowfire").getDefaultState(), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e) {}
        super.onDeath(damageSource);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean daylightBurns() { return !this.isChild(); }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityGeist(this.getEntityWorld());
	}
}
