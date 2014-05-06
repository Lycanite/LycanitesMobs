package lycanite.lycanitesmobs.api.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.info.EntityListCustom;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCustomSpawnEgg extends Item {
	public ILycaniteMod mod;
	public String itemName = "customspawnegg";
	public String texturePath = "customspawn";
    
	// ==================================================
	//                    Constructor
	// ==================================================
    public ItemCustomSpawnEgg() {
        super();
        this.setHasSubtypes(true);
        setCreativeTab(LycanitesMobs.creativeTab);
        setUnlocalizedName("customspawnegg");
    }
    
	// ==================================================
	//                  Get Display Name
	// ==================================================
    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String s1 = ObjectManager.entityLists.get(this.mod.getDomain()).getStringFromID(par1ItemStack.getItemDamage());
        
        if (s1 != null)
            s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        
        return s;
    }
    
    
	// ==================================================
	//                   Get Egg Color
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
    	EntityListCustom.EntityEggInfo entityegginfo = (EntityListCustom.EntityEggInfo)ObjectManager.entityLists.get(this.mod.getDomain()).entityEggs.get(Integer.valueOf(par1ItemStack.getItemDamage()));
        return entityegginfo != null ? (par2 == 0 ? entityegginfo.primaryColor : entityegginfo.secondaryColor) : 16777215;
    }
    
    
	// ==================================================
	//                     Item Use
	// ==================================================
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        
        // Edit Spawner:
        if(block == Blocks.mob_spawner) {
        	TileEntity tileEntity = world.getTileEntity(x, y, z);
        	if(tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
        		TileEntityMobSpawner spawner = (TileEntityMobSpawner)tileEntity;
        		spawner.func_145881_a().setEntityName(ObjectManager.entityLists.get(this.mod.getDomain()).getStringFromID(itemStack.getItemDamage())); //getSpawnerLogic()
        		world.markBlockForUpdate(x, y, z);
        	}
        }
        
        // Spawn Mob:
        else if(!world.isRemote) {
	        x += Facing.offsetsXForSide[side];
	        y += Facing.offsetsYForSide[side];
	        z += Facing.offsetsZForSide[side];
	        double d0 = 0.0D;
	        
	        if(side == 1 && world.getBlock(x, y, z) != null && world.getBlock(x, y, z).getRenderType() == 11)
	            d0 = 0.5D;
	        
	        Entity entity = spawnCreature(world, itemStack.getItemDamage(), (double)x + 0.5D, (double)y + d0, (double)z + 0.5D);
	        
	        if(entity != null) {
	            if(entity instanceof EntityLivingBase && itemStack.hasDisplayName())
	                ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
	
	            if(!player.capabilities.isCreativeMode)
	                --itemStack.stackSize;
	        }
        }

        return true;
    }
    
    
	// ==================================================
	//                   On Right Click
	// ==================================================
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(par2World.isRemote)
            return par1ItemStack;
        else {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

            if(movingobjectposition == null)
                return par1ItemStack;
            else
                if(movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if(!par2World.canMineBlock(par3EntityPlayer, i, j, k))
                        return par1ItemStack;

                    if(!par3EntityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, par1ItemStack))
                        return par1ItemStack;

                    if(par2World.getBlock(i, j, k).getMaterial() == Material.water) {
                        Entity entity = spawnCreature(par2World, par1ItemStack.getItemDamage(), (double)i, (double)j, (double)k);

                        if(entity != null)
                            if(entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName())
                                ((EntityLiving)entity).setCustomNameTag(par1ItemStack.getDisplayName());

                            if(!par3EntityPlayer.capabilities.isCreativeMode)
                                --par1ItemStack.stackSize;
                    }
                }

                return par1ItemStack;
        }
    }
    
    
	// ==================================================
	//                   Spawn Creature
	// ==================================================
    public Entity spawnCreature(World par0World, int par1, double par2, double par4, double par6) {
        if(!ObjectManager.entityLists.get(this.mod.getDomain()).entityEggs.containsKey(Integer.valueOf(par1)))
            return null;
        else {
            Entity entity = null;

            for(int j = 0; j < 1; ++j) {
                entity = ObjectManager.entityLists.get(this.mod.getDomain()).createEntityByID(par1, par0World);

                if(entity != null && entity instanceof EntityLivingBase) {
                    EntityLiving entityliving = (EntityLiving)entity;
                    entity.setLocationAndAngles(par2, par4, par6, MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    entityliving.onSpawnWithEgg((IEntityLivingData)null);
                    par0World.spawnEntityInWorld(entity);
                    entityliving.playLivingSound();
                }
            }

            return entity;
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    // ========== Get Icon ==========
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
        return par2 > 0 ? AssetManager.getIcon(this.itemName + "_overlay") : AssetManager.getIcon(this.itemName);
    }
    
    // ========== Register Icon ==========
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
    	AssetManager.addIcon(this.itemName, this.mod.getDomain(), texturePath, iconRegister);
    	AssetManager.addIcon(this.itemName + "_overlay", this.mod.getDomain(), texturePath + "_overlay", iconRegister);
    }
    
    
	// ==================================================
	//                   Get Sub Items
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List subItems) {
    	if(this.mod == null || !ObjectManager.entityLists.containsKey(this.mod.getDomain()))
    		return;
    	
    	HashMap entityEggs = ObjectManager.entityLists.get(this.mod.getDomain()).entityEggs;
    	if(entityEggs.size() <= 0)
    		return;
    	
        Iterator iterator = entityEggs.values().iterator();
        while(iterator.hasNext()) {
        	EntityListCustom.EntityEggInfo entityegginfo = (EntityListCustom.EntityEggInfo)iterator.next();
            subItems.add(new ItemStack(this, 1, entityegginfo.spawnedID));
        }
    }
}