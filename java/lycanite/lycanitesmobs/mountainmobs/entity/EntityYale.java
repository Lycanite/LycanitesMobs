package lycanite.lycanitesmobs.mountainmobs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.IGroupPredator;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIEatBlock;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollowParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAILookIdle;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIMate;
import lycanite.lycanitesmobs.api.entity.ai.EntityAISwimming;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetAvoid;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITargetParent;
import lycanite.lycanitesmobs.api.entity.ai.EntityAITempt;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWander;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIWatchClosest;
import lycanite.lycanitesmobs.api.info.DropRate;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class EntityYale extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IShearable {
	
	public DropRate woolDrop;
	
	/**
	 * Simulates a crafting instance between two dyes and uses the result dye as a mixed color, used for babies with different colored parents.
	 */
	private final InventoryCrafting colorMixer = new InventoryCrafting(new Container() {
        private static final String __OBFID = "CL_00001649";
        public boolean canInteractWith(EntityPlayer par1EntityPlayer)
        {
            return false;
        }
    }, 2, 1);
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityYale(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.experience = 3;
        this.spawnsOnlyInLight = true;
        this.hasAttackSound = false;
        
        this.eggName = "MountainEgg";
        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        
        this.setWidth = 0.8F;
        this.setHeight = 1.6F;
        this.fleeHealthPercent = 1.0F;
        this.isHostileByDefault = false;
        this.setupMob();
    	
        // AI Tasks:
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(2, new EntityAIMate(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItemList("vegetables"));
        this.tasks.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(6, new EntityAIEatBlock(this).setBlocks(Blocks.grass).setReplaceBlock(Blocks.dirt));
        this.tasks.addTask(7, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        
        // Add Dyes to the Color Mixer:
        this.colorMixer.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
        this.colorMixer.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
    }
	
	// ========== Init ==========
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(WATCHER_ID.LAST.id, (byte)1);
    }
    
    // ========== Stats ==========
	@Override
	protected void applyEntityAttributes() {
		HashMap<String, Double> baseAttributes = new HashMap<String, Double>();
		baseAttributes.put("maxHealth", 20D);
		baseAttributes.put("movementSpeed", 0.26D);
		baseAttributes.put("knockbackResistance", 0.25D);
		baseAttributes.put("followRange", 16D);
		baseAttributes.put("attackDamage", 4D);
        super.applyEntityAttributes(baseAttributes);
    }
	
	// ========== Default Drops ==========
	@Override
	public void loadItemDrops() {
        this.drops.add(new DropRate(new ItemStack(ObjectManager.getItem("YaleMeatRaw")), 1).setMinAmount(1).setMaxAmount(3));
		this.woolDrop = new DropRate(new ItemStack(Blocks.wool), 1).setMinAmount(1).setMaxAmount(3);
        this.drops.add(this.woolDrop);
	}
	
	
    // ==================================================
    //                      Spawn
    // ==================================================
	// ========== On Spawn ==========
	@Override
	public void onSpawn() {
		if(!this.isChild())
			this.setColor(this.getRandomFurColor(this.getRNG()));
	}
	
	
    // ==================================================
    //                      Abilities
    // ==================================================
	// ========== IShearable ==========
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return this.hasFur() && !this.isChild();
	}
	
	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		this.setFur(false);
		this.playSound("mob.sheep.shear", 1.0F, 1.0F);
		ArrayList<ItemStack> dropStacks = new ArrayList<ItemStack>();
		
		int quantity = this.woolDrop.getQuantity(this.getRNG(), fortune);
		ItemStack dropStack = this.woolDrop.getItemStack(this, quantity);
		this.dropItem(dropStack);
		dropStacks.add(dropStack);
		
		return dropStacks;
	}
	
	// ========== Fur ==========
	public boolean hasFur() {
		if(this.dataWatcher == null) return true;
		return this.dataWatcher.getWatchableObjectByte(WATCHER_ID.LAST.id) > 0;
	}

	public void setFur(boolean fur) {
		if(!this.worldObj.isRemote)
			this.dataWatcher.updateObject(WATCHER_ID.LAST.id, (byte)(fur ? 1 : 0));
	}
	
	@Override
	public void onEat() {
		if(!this.worldObj.isRemote)
			this.setFur(true);
	}
	
	@Override
	public boolean canBeColored(EntityPlayer player) {
		return true;
	}
	
	@Override
	public void setColor(int color) {
		this.woolDrop.setDrop(new ItemStack(Blocks.wool, 1, color));
		super.setColor(color);
	}
	
	public int getRandomFurColor(Random random) {
        int i = random.nextInt(100);
        return i < 5 ? 15 : (i < 10 ? 7 : (i < 15 ? 8 : (i < 18 ? 12 : (random.nextInt(500) == 0 ? 6 : 0))));
    }
	
	public int getMixedFurColor(EntityCreatureBase entityA, EntityCreatureBase entityB) {
		int i = entityA.getColor();
        int j = entityB.getColor();
        this.colorMixer.getStackInSlot(0).setItemDamage(i);
        this.colorMixer.getStackInSlot(1).setItemDamage(j);
        ItemStack itemstack = CraftingManager.getInstance().findMatchingRecipe(this.colorMixer, this.worldObj);
        int k;
        if(itemstack != null && itemstack.getItem() == Items.dye)
            k = itemstack.getItemDamage();
        else
            k = this.worldObj.rand.nextBoolean() ? i : j;
        return k;
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// ========== Pathing Weight ==========
	@Override
	public float getBlockPathWeight(int par1, int par2, int par3) {
		if(this.worldObj.getBlock(par1, par2 - 1, par3) != Blocks.air) {
			Block block = this.worldObj.getBlock(par1, par2 - 1, par3);
			if(block.getMaterial() == Material.grass)
				return 10F;
			if(block.getMaterial() == Material.ground)
				return 7F;
		}
        return super.getBlockPathWeight(par1, par2, par3);
    }
    
	// ========== Can leash ==========
    @Override
    public boolean canLeash(EntityPlayer player) {
	    return true;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isPotionApplicable(PotionEffect potionEffect) {
        if(potionEffect.getPotionID() == Potion.digSlowdown.id) return false;
        if(ObjectManager.getPotionEffect("Weight") != null)
        	if(potionEffect.getPotionID() == ObjectManager.getPotionEffect("Weight").id) return false;
        return super.isPotionApplicable(potionEffect);
    }
    
    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    
    // ==================================================
   	//                      Drops
   	// ==================================================
    // ========== Drop Items ==========
    /** Cycles through all of this entity's DropRates and drops random loot, usually called on death. If this mob is a minion, this method is cancelled. **/
    @Override
    protected void dropFewItems(boolean playerKill, int lootLevel) {
    	if(!this.hasFur())
    		this.woolDrop.setMinAmount(0).setMaxAmount(0);
    	super.dropFewItems(playerKill, lootLevel);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partner) {
		EntityCreatureAgeable baby = new EntityYale(this.worldObj);
		int color = this.getMixedFurColor(this, partner);
		baby.setColor(15 - color);
		return baby;
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("vegetables", testStack);
    }
    
	
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	super.readEntityFromNBT(nbtTagCompound);
    	if(nbtTagCompound.hasKey("HasFur")) {
    		this.setFur(nbtTagCompound.getBoolean("HasFur"));
    	}
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	super.writeEntityToNBT(nbtTagCompound);
    	nbtTagCompound.setBoolean("HasFur", this.hasFur());
    }
}
