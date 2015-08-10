package lycanite.lycanitesmobs.demonmobs.entity;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupDemon;
import lycanite.lycanitesmobs.api.IGroupFire;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.entity.ai.*;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityRahovart extends EntityCreatureBase implements IMob, IBossDisplayData, IGroupDemon {

    public List<EntityPlayer> playerTargets = new ArrayList<EntityPlayer>();
    public List<EntityBelph> belphMinions = new ArrayList<EntityBelph>(); // Phase 0
    public List<EntityBehemoth> behemothMinions = new ArrayList<EntityBehemoth>(); // Phase 1
    public int hellfireEnergy = 0;
    public int hellfireAttackProgress = 0;


    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRahovart(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.defense = 2;
        this.experience = 10;
        this.hasAttackSound = false;
        
        this.setWidth = 10F;
        this.setHeight = 50F;
        this.setupMob();

        // Boss:
        this.boss = true;
        this.forceBossHealthBar = true;
        this.damageMax = 40;
        
        // AI Tasks:
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this).setSpeed(1.0D).setRate(60).setRange(16.0F).setMinChaseDistance(8.0F).setChaseTime(-1));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityBelph.class));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 10000D);
		baseAttributes.put("movementSpeed", 0.32D);
		baseAttributes.put("knockbackResistance", 1D);
		baseAttributes.put("followRange", 56D);
		baseAttributes.put("attackDamage", 18D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(Items.blaze_powder), 0.5F).setMinAmount(20).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(Items.blaze_rod), 0.5F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.diamond), 0.75F).setMinAmount(10).setMaxAmount(20));
        this.drops.add(new DropRate(new ItemStack(Items.nether_star), 0.5F).setMinAmount(5).setMaxAmount(8));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("doomfirecharge")), 0.5F).setMinAmount(20).setMaxAmount(100));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("hellfirecharge")), 0.5F).setMinAmount(10).setMaxAmount(50));
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("demonicsoulstone")), 1F).setMinAmount(5).setMaxAmount(5));
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        this.updateHellfireCharge();
        if(this.ticksExisted % 20 == 0) {
            if(this.hellfireEnergy < 100)
                this.hellfireEnergy++;
            else
                this.hellfireEnergy = 0;
            this.hellfireEnergy = Math.max(0, Math.min(100, this.hellfireEnergy));
        }

        // Random Projectiles:
        if(this.ticksExisted % 40 == 0) {
            EntityProjectileBase projectile = new EntityHellfireball(this.worldObj, this);
            projectile.setProjectileScale(8f);
            projectile.setThrowableHeading((this.getRNG().nextFloat()) - 0.5F, this.getRNG().nextFloat(), (this.getRNG().nextFloat()) - 0.5F, 1.2F, 6.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.worldObj.spawnEntityInWorld(projectile);
        }

        // Hellfire Trail:
        if(!this.worldObj.isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
            int trailHeight = 5;
            int trailWidth = 1;
            if(this.getSubspeciesIndex() >= 3)
                trailWidth = 10;
            for(int y = 0; y < trailHeight; y++) {
                Block block = this.worldObj.getBlock((int)this.posX, (int)this.posY + y, (int)this.posZ);
                if(block == Blocks.air || block == Blocks.fire || block == Blocks.snow_layer || block == Blocks.tallgrass || block == ObjectManager.getBlock("frostfire")) {
                    if(trailWidth == 1)
                        this.worldObj.setBlock((int) this.posX, (int) this.posY + y, (int) this.posZ, ObjectManager.getBlock("hellfire"));
                    else
                        for(int x = -(trailWidth / 2); x < (trailWidth / 2) + 1; x++) {
                            for(int z = -(trailWidth / 2); z < (trailWidth / 2) + 1; z++) {
                                this.worldObj.setBlock((int) this.posX + x, (int) this.posY + y, (int) this.posZ + z, ObjectManager.getBlock("hellfire"));
                            }
                        }
                }
            }
        }
    }


    // ==================================================
    //                  Battle Phases
    // ==================================================
    @Override
    public void updateBattlePhase() {
        double healthNormal = this.getHealth() / this.getMaxHealth();
        if(healthNormal <= 20) {
            this.battlePhase = 2;
            return;
        }
        if(healthNormal <= 60) {
            this.battlePhase = 1;
            return;
        }
        this.battlePhase = 0;
    }


    // ==================================================
    //                     Hellfire
    // ==================================================
    public List<EntityHellfireWall> hellfireWalls = new ArrayList<EntityHellfireWall>();
    public void updateHellfireCharge() {
        int hellfireChargeCount = Math.round((float)this.hellfireEnergy / 20);
        while(this.hellfireWalls.size() < hellfireChargeCount) {
            EntityHellfireWall hellfireWall = new EntityHellfireWall(this.worldObj, this);
            this.hellfireWalls.add(hellfireWall);
            this.worldObj.spawnEntityInWorld(hellfireWall);
        }
        while(this.hellfireWalls.size() > hellfireChargeCount) {
            this.hellfireWalls.get(this.hellfireWalls.size() - 1).projectileLife = 0;
            this.hellfireWalls.remove(this.hellfireWalls.size() - 1);
        }
        int i = 0;
        for(EntityHellfireWall hellfireWall : this.hellfireWalls) {
            hellfireWall.setPosition(this.posX, this.posY + 5 + (10 * i), this.posZ);
            hellfireWall.projectileLife = 5;
            i++;
        }
    }
	
	
	// ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityBelph.class))
    		return false;
        return super.canAttackClass(targetClass);
    }

    public boolean canAttackEntity(EntityLivingBase targetEntity) {
        if(targetEntity instanceof IGroupDemon || targetEntity instanceof IGroupFire) {
            if(targetEntity instanceof EntityCreatureTameable)
                return ((EntityCreatureTameable)targetEntity).getOwner() instanceof EntityPlayer;
            else
                return false;
        }
        return super.canAttackEntity(targetEntity);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void rangedAttack(Entity target, float range) {
    	// Type:
    	EntityHellfireball projectile = new EntityHellfireball(this.worldObj, this);
        projectile.setProjectileScale(8f);
    	
    	// Y Offset:
    	projectile.posY -= this.height / 2;
    	
    	// Accuracy:
    	float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
    	
    	// Set Velocities:
        double d0 = target.posX - this.posX + accuracy;
        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
        double d2 = target.posZ - this.posZ + accuracy;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        float velocity = 1.2F;
        projectile.setThrowableHeading(d0, d1 + (double)f1, d2, velocity, 6.0F);
        
        // Launch:
        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(projectile);
        super.rangedAttack(target, range);
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
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       NBT
    // ==================================================
    // ========== Read ===========
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
        if(nbtTagCompound.hasKey("HellfireEnergy")) {
            this.hellfireEnergy = nbtTagCompound.getInteger("HellfireEnergy");
        }
        if(nbtTagCompound.hasKey("HellfireAttackProgress")) {
            this.hellfireAttackProgress = nbtTagCompound.getInteger("HellfireAttackProgress");
        }
        if(nbtTagCompound.hasKey("BelphIDs")) {
            NBTTagList belphIDs = nbtTagCompound.getTagList("BelphIDs", 10);
            for(int i = 0; i < belphIDs.tagCount(); i++) {
                NBTTagCompound belphID = belphIDs.getCompoundTagAt(i);
                if(belphID.hasKey("ID")) {
                    Entity entity = this.worldObj.getEntityByID(belphID.getInteger("ID"));
                    if(entity != null && entity instanceof EntityBelph)
                        this.belphMinions.add((EntityBelph)entity);
                }
            }
        }
        if(nbtTagCompound.hasKey("BehemothIDs")) {
            NBTTagList behemothIDs = nbtTagCompound.getTagList("BehemothIDs", 10);
            for(int i = 0; i < behemothIDs.tagCount(); i++) {
                NBTTagCompound behemothID = behemothIDs.getCompoundTagAt(i);
                if(behemothID.hasKey("ID")) {
                    Entity entity = this.worldObj.getEntityByID(behemothID.getInteger("ID"));
                    if(entity != null && entity instanceof EntityBehemoth)
                        this.behemothMinions.add((EntityBehemoth)entity);
                }
            }
        }
    }

    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setInteger("HellfireEnergy", this.hellfireEnergy);
        nbtTagCompound.setInteger("HellfireAttackProgress", this.hellfireAttackProgress);
        if(this.battlePhase == 0) {
            NBTTagList belphIDs = new NBTTagList();
            for(EntityBelph entityBelph : this.belphMinions) {
                NBTTagCompound belphID = new NBTTagCompound();
                belphID.setInteger("ID", entityBelph.getEntityId());
                belphIDs.appendTag(belphID);
            }
            nbtTagCompound.setTag("BelphIDs", belphIDs);
        }
        if(this.battlePhase == 1) {
            NBTTagList behemothIDs = new NBTTagList();
            for(EntityBehemoth entityBehemoth : this.behemothMinions) {
                NBTTagCompound behemothID = new NBTTagCompound();
                behemothID.setInteger("ID", entityBehemoth.getEntityId());
                behemothIDs.appendTag(behemothID);
            }
            nbtTagCompound.setTag("BehemothIDs", behemothIDs);
        }
    }
}
