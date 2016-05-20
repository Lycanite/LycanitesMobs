package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemWinterGift extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWinterGift() {
        super();
        this.group = LycanitesMobs.group;
        this.itemName = "wintergift";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
    @Override
     public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
        if(!player.capabilities.isCreativeMode) {
         --itemStack.stackSize;
        }

        if(!world.isRemote) {
        if(player.getRNG().nextBoolean())
            this.openGood(itemStack, world, player);
        else
            this.openBad(itemStack, world, player);
        }

        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Good
  	// ==================================================
    public void openGood(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = I18n.translateToLocal("item." + this.itemName + ".good");
		player.addChatMessage(new TextComponentString(message));
		if(AssetManager.getSound(this.itemName + "_good") == null)
			AssetManager.addSound(this.itemName + "_good", this.group, "item." + this.itemName + ".good");
		world.playSound(player, player.getPosition(), AssetManager.getSound(this.itemName + "_good"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// Three Random Gifts:
		for(int i = 0; i < 3; i++) {
			ItemStack[] dropStacks = ObjectLists.getItems("winter_gifts");
			if(dropStacks == null || dropStacks.length <= 0) return;
			ItemStack dropStack = dropStacks[player.getRNG().nextInt(dropStacks.length)];
			if(dropStack != null && dropStack.getItem() != null) {
				dropStack.stackSize = 1 + player.getRNG().nextInt(4);
				EntityItemCustom entityItem = new EntityItemCustom(world, player.posX, player.posY, player.posZ, dropStack);
				entityItem.setPickupDelay(10);
				world.spawnEntityInWorld(entityItem);
			}
		}
    }
    
    
    // ==================================================
  	//                       Bad
  	// ==================================================
    public void openBad(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = I18n.translateToLocal("item." + this.itemName + ".bad");
		player.addChatMessage(new TextComponentString(message));
		if(AssetManager.getSound(this.itemName + "_bad") == null)
			AssetManager.addSound(this.itemName + "_bad", this.group, "item." + this.itemName + ".bad");
        world.playSound(player, player.getPosition(), AssetManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
		// One Random Trick:
		Class[] entityClasses = ObjectLists.getEntites("winter_tricks");
        if(entityClasses == null) return;
        if(entityClasses.length <= 0) return;
		Class entityClass = entityClasses[player.getRNG().nextInt(entityClasses.length)];
		if(entityClass != null) {
			Entity entity = null;
            try {
                entity = (Entity)entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
            } catch (Exception e) { e.printStackTrace(); }
            if(entity != null) {
	            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

                // Themed Names:
                if (entity instanceof EntityLivingBase) {
                    EntityCreatureBase entityCreature = (EntityCreatureBase) entity;
                    if (entityCreature.mobInfo.getRegistryName().equals("wendigo"))
                        entityCreature.setCustomNameTag("Gooderness");
                    else if (entityCreature.mobInfo.getRegistryName().equals("jabberwock"))
                        entityCreature.setCustomNameTag("Rudolph");
                    else if (entityCreature.mobInfo.getRegistryName().equals("ent"))
                        entityCreature.setCustomNameTag("Salty Tree");
                    else if (entityCreature.mobInfo.getRegistryName().equals("trent"))
                        entityCreature.setCustomNameTag("Salty Tree");
                    else if (entityCreature.mobInfo.getRegistryName().equals("phantom"))
                        entityCreature.setCustomNameTag("Satan Claws");
                    else if(entityCreature.mobInfo.getRegistryName().equals("behemoth"))
                        entityCreature.setCustomNameTag("Krampus");
                }

	            world.spawnEntityInWorld(entity);
            }
		}
    }


    // ==================================================
    //                       Lists
    // ==================================================
    public static void createObjectLists() {
        // Halloween Treats:
        ObjectLists.addItem("winter_gifts", Items.diamond);
        ObjectLists.addItem("winter_gifts", Items.gold_ingot);
        ObjectLists.addItem("winter_gifts", Items.emerald);
        ObjectLists.addItem("winter_gifts", Blocks.iron_block);
        ObjectLists.addItem("winter_gifts", Items.ender_pearl);
        ObjectLists.addItem("winter_gifts", Items.blaze_rod);
        ObjectLists.addItem("winter_gifts", Items.glowstone_dust);
        ObjectLists.addItem("winter_gifts", Items.coal);
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("mosspie"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("ambercake"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("peakskebab"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("bulwarkburger"));
        ObjectLists.addItem("winter_gifts", ObjectManager.getItem("palesoup"));
        ObjectLists.addFromConfig("winter_gifts");

        // Halloween Mobs:
        ObjectLists.addEntity("winter_tricks", "wendigo");
        ObjectLists.addEntity("winter_tricks", "jabberwock");
        ObjectLists.addEntity("winter_tricks", "ent");
        ObjectLists.addEntity("winter_tricks", "trent");
        ObjectLists.addEntity("winter_tricks", "phantom");
        ObjectLists.addEntity("winter_tricks", "behemoth");
    }
}
