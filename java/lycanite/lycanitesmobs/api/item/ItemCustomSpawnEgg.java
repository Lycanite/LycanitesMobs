package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.EntityListCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemCustomSpawnEgg extends ItemBase {
	public GroupInfo group = LycanitesMobs.group;
	public String itemName = "customspawnegg";
	public String texturePath = "customspawn";
    
	// ==================================================
	//                    Constructor
	// ==================================================
    public ItemCustomSpawnEgg() {
        super();
        this.setHasSubtypes(true);
        setCreativeTab(LycanitesMobs.creaturesTab);
        setUnlocalizedName("customspawnegg");
    }
    
	// ==================================================
	//                  Get Display Name
	// ==================================================
    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        String s = ("" + I18n.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String s1 = this.getEntityIdFromItem(itemStack);
        if (s1 != null) {
            s = s + " " + I18n.translateToLocal("entity." + s1 + ".name");
        }

        return s;
    }
    
    
    // ==================================================
	//                      Info
	// ==================================================
    @Override
    public String getDescription(ItemStack itemStack, EntityPlayer entityPlayer, List textList, boolean par4) {
    	String entityID = this.getEntityIdFromItem(itemStack);
        Class entityClass = ObjectManager.entityLists.get(this.group.filename).getClassFromID(entityID);
        MobInfo mobInfo = MobInfo.mobClassToInfo.get(entityClass);
        if(mobInfo == null) {
            LycanitesMobs.printWarning("Mob Spawn Egg", "Unable to get a MobInfo entry for id: " + entityID + " class: " + entityClass);
            return "Unable to get a MobInfo entry for id: " + entityID + " class: " + entityClass;
        }
    	return mobInfo.getDescription();
    }
    
    
	// ==================================================
	//                     Item Use
	// ==================================================
    @Override
    public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        if (!player.canPlayerEdit(pos.offset(facing), facing, itemStack)) {
            return EnumActionResult.FAIL;
        }
        
        // Edit Spawner:
        if(block == Blocks.MOB_SPAWNER) {
            TileEntity tileEntity = world.getTileEntity(pos);
            MobSpawnerBaseLogic mobspawnerbaselogic = ((TileEntityMobSpawner)tileEntity).getSpawnerBaseLogic();
            mobspawnerbaselogic.setEntityName(this.getEntityIdFromItem(itemStack));
            tileEntity.markDirty();
            world.notifyBlockUpdate(pos, blockState, blockState, 3);
            if (!player.capabilities.isCreativeMode) {
                --itemStack.stackSize;
            }

            return EnumActionResult.SUCCESS;
        }
        
        // Spawn Mob:
        else if(!world.isRemote) {
            pos = pos.offset(facing);
            double d0 = 0.0D;

            if (facing == EnumFacing.UP && blockState.getBlock() instanceof BlockFence) {
                d0 = 0.5D;
            }
	        
	        Entity entity = spawnCreature(world, this.getEntityIdFromItem(itemStack), (double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D);
	        
	        if(entity != null) {
	            if(entity instanceof EntityLivingBase && itemStack.hasDisplayName())
	                ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());

                applyItemEntityDataToEntity(world, player, itemStack, entity);
	
	            if(!player.capabilities.isCreativeMode)
	                --itemStack.stackSize;
	        }
        }

        return EnumActionResult.SUCCESS;
    }
    
    
	// ==================================================
	//                   On Right Click
	// ==================================================
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if(world.isRemote)
            return new ActionResult(EnumActionResult.PASS, itemStack);
        else {
            RayTraceResult rayTraceResult = this.rayTrace(world, player, true);

            if(rayTraceResult == null)
                return new ActionResult(EnumActionResult.PASS, itemStack);
            else
                if(rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = rayTraceResult.getBlockPos();

                    if(!world.canMineBlockBody(player, pos))
                        return new ActionResult(EnumActionResult.FAIL, itemStack);

                    if(!player.canPlayerEdit(pos, rayTraceResult.sideHit, itemStack))
                        return new ActionResult(EnumActionResult.PASS, itemStack);

                    if(world.getBlockState(pos).getMaterial() == Material.WATER) {
                        Entity entity = spawnCreature(world, this.getEntityIdFromItem(itemStack), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());

                        if(entity != null)
                            if(entity instanceof EntityLivingBase && itemStack.hasDisplayName())
                                ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());

                            if(!player.capabilities.isCreativeMode)
                                --itemStack.stackSize;
                    }
                }

                return new ActionResult(EnumActionResult.SUCCESS, itemStack);
        }
    }
    
    
	// ==================================================
	//                   Spawn Creature
	// ==================================================
    public Entity spawnCreature(World world, String entityID, double x, double y, double z) {
        if(!ObjectManager.entityLists.get(this.group.filename).entityEggs.containsKey(entityID))
            return null;
        else {
            Entity entity = null;

            for(int j = 0; j < 1; ++j) {
                entity = ObjectManager.entityLists.get(this.group.filename).createEntityByID(entityID, world);

                if(entity != null && entity instanceof EntityLivingBase) {
                    EntityLiving entityliving = (EntityLiving)entity;
                    entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(world.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entityliving)), (IEntityLivingData)null);
                    world.spawnEntityInWorld(entity);
                    entityliving.playLivingSound();
                }
            }

            return entity;
        }
    }
    
    
	// ==================================================
	//                      Visuals
	// ==================================================
    // ========== Use Colors ==========
    @Override
    public boolean useItemColors() {
        return true;
    }

    // ========== Get Color from ItemStack ==========
    @Override
    public int getColorFromItemstack(ItemStack itemStack, int tintIndex) {
        EntityListCustom.EntityEggInfo entityEggInfo = ObjectManager.entityLists.get(this.group.filename).entityEggs.get(this.getEntityIdFromItem(itemStack));
        return entityEggInfo != null ? (tintIndex == 0 ? entityEggInfo.primaryColor : entityEggInfo.secondaryColor) : 16777215;
    }
    
    
	// ==================================================
	//                   Get Sub Items
	// ==================================================
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List subItems) {
    	if(this.group == null || !ObjectManager.entityLists.containsKey(this.group.filename))
    		return;

        for(EntityListCustom.EntityEggInfo entityEggInfo : ObjectManager.entityLists.get(this.group.filename).entityEggs.values()) {
            ItemStack itemstack = new ItemStack(item, 1);
            this.applyEntityIdToItemStack(itemstack, entityEggInfo.spawnedID);
            subItems.add(itemstack);
        }
    }


    // ==================================================
    //              Apply Entity ID To Stack
    // ==================================================
    // @SideOnly(Side.CLIENT)
     public static void applyEntityIdToItemStack(ItemStack stack, String entityId) {
        NBTTagCompound nbttagcompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        nbttagcompound1.setString("id", entityId);
        nbttagcompound.setTag("EntityTag", nbttagcompound1);
        stack.setTagCompound(nbttagcompound);
    }


    // ==================================================
    //              Get Entity ID From Item
    // ==================================================
    public static String getEntityIdFromItem(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound == null || !nbttagcompound.hasKey("EntityTag", 10)) {
            return null;
        }

        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("EntityTag");
        return !nbttagcompound1.hasKey("id", 8) ? null : nbttagcompound1.getString("id");
    }


    // ==================================================
    //         Apply Item Entity Data To Entity
    // ==================================================
    public static void applyItemEntityDataToEntity(World entityWorld, @Nullable EntityPlayer player, ItemStack stack, @Nullable Entity targetEntity) {
        MinecraftServer minecraftserver = entityWorld.getMinecraftServer();
        if (minecraftserver != null && targetEntity != null) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null && nbttagcompound.hasKey("EntityTag", 10))
            {
                if (!entityWorld.isRemote && targetEntity.ignoreItemEntityData() && (player == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile())))
                {
                    return;
                }

                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                targetEntity.writeToNBT(nbttagcompound1);
                UUID uuid = targetEntity.getUniqueID();
                nbttagcompound1.merge(nbttagcompound.getCompoundTag("EntityTag"));
                targetEntity.setUniqueId(uuid);
                targetEntity.readFromNBT(nbttagcompound1);
            }
        }
    }
}