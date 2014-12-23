package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemHalloweenTreat extends ItemBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemHalloweenTreat() {
        super();
        this.group = LycanitesMobs.group;
        this.itemName = "halloweentreat";
        this.setup();
    }
    
    
    // ==================================================
 	//                    Item Use
 	// ==================================================
     public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
         if(!player.capabilities.isCreativeMode) {
             --itemStack.stackSize;
         }
         
         if(!world.isRemote) {
         	if(player.getRNG().nextBoolean())
         		this.openGood(itemStack, world, player);
         	else
         		this.openBad(itemStack, world, player);
         }

         return itemStack;
     }
    
    
    // ==================================================
  	//                       Good
  	// ==================================================
    public void openGood(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = StatCollector.translateToLocal("item." + this.itemName + ".good");
		player.addChatMessage(new ChatComponentText(message));
		if(AssetManager.getSound(this.itemName + "_good") == null)
			AssetManager.addSound(this.itemName + "_good", this.group, "item." + this.itemName + ".good");
		world.playSoundAtEntity(player, AssetManager.getSound(this.itemName + "_good"), 5.0F, 1.0F);
		
		// Three Random Treats:
		for(int i = 0; i < 3; i++) {
			ItemStack[] dropStacks = ObjectLists.getItems("halloween_treats");
			if(dropStacks == null || dropStacks.length <= 0) return;
			ItemStack dropStack = dropStacks[player.getRNG().nextInt(dropStacks.length)];
			if(dropStack != null && dropStack.getItem() != null) {
				dropStack.stackSize = 1 + player.getRNG().nextInt(4);
				EntityItemCustom entityItem = new EntityItemCustom(world, player.posX, player.posY, player.posZ, dropStack);
				entityItem.delayBeforeCanPickup = 10;
				world.spawnEntityInWorld(entityItem);
			}
		}
    }
    
    
    // ==================================================
  	//                       Bad
  	// ==================================================
    public void openBad(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = StatCollector.translateToLocal("item." + this.itemName + ".bad");
		player.addChatMessage(new ChatComponentText(message));
		if(AssetManager.getSound(this.itemName + "_bad") == null)
			AssetManager.addSound(this.itemName + "_bad", this.group, "item." + this.itemName + ".bad");
		world.playSoundAtEntity(player, AssetManager.getSound(this.itemName + "_bad"), 5.0F, 1.0F);
		
		// One Random Trick:
		Class[] entityClasses = ObjectLists.getEntites("halloween_tricks");
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
	            world.spawnEntityInWorld(entity);
            }
		}
    }


    // ==================================================
    //                       Lists
    // ==================================================
    public static void createObjectLists() {
        // Halloween Treats:
        ObjectLists.addItem("halloween_treats", Items.diamond);
        ObjectLists.addItem("halloween_treats", Items.gold_ingot);
        ObjectLists.addItem("halloween_treats", Items.emerald);
        ObjectLists.addItem("halloween_treats", Blocks.iron_block);
        ObjectLists.addItem("halloween_treats", Items.ender_pearl);
        ObjectLists.addItem("halloween_treats", Items.blaze_rod);
        ObjectLists.addItem("halloween_treats", Items.glowstone_dust);
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("mosspie"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("bulwarkburger"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("paleosalad"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("searingtaco"));
        ObjectLists.addItem("halloween_treats", ObjectManager.getItem("devillasagna"));
        ObjectLists.addFromConfig("halloween_treats");

        // Halloween Mobs:
        ObjectLists.addEntity("halloween_tricks", "ghoulzombie");
        ObjectLists.addEntity("halloween_tricks", "cryptzombie");
        ObjectLists.addEntity("halloween_tricks", "belph");
        ObjectLists.addEntity("halloween_tricks", "behemoth");
        ObjectLists.addEntity("halloween_tricks", "ent");
        ObjectLists.addEntity("halloween_tricks", "trent");
        ObjectLists.addEntity("halloween_tricks", "nethersoul");
        ObjectLists.addEntity("halloween_tricks", "cacodemon");
        ObjectLists.addEntity("halloween_tricks", "grue");
        ObjectLists.addEntity("halloween_tricks", "phantom");
        ObjectLists.addEntity("halloween_tricks", "epion");
    }
}
