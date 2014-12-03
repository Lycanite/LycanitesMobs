package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityWendigo;
import lycanite.lycanitesmobs.forestmobs.entity.EntityEnt;
import lycanite.lycanitesmobs.forestmobs.entity.EntityTrent;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityJabberwock;
import lycanite.lycanitesmobs.shadowmobs.entity.EntityPhantom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
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
		
		// Three Random Gifts:
		for(int i = 0; i < 3; i++) {
			ItemStack[] dropStacks = ObjectLists.getItems("winter_gifts");
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
		Class[] entityClasses = ObjectLists.getEntites("winter_tricks");
		if(entityClasses != null && entityClasses.length <= 0) return;
		Class entityClass = entityClasses[player.getRNG().nextInt(entityClasses.length)];
		if(entityClass != null) {
			Entity entity = null;
            try {
                entity = (Entity)entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
            } catch (Exception e) { e.printStackTrace(); }
            if(entity != null) {
	            entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

                // Themed Names:
                if(entity instanceof EntityLivingBase) {
                    EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
                    if(entityCreature instanceof EntityWendigo)
                        entityCreature.setCustomNameTag("Gooderness");
                    else if(entityCreature instanceof EntityJabberwock)
                        entityCreature.setCustomNameTag("Rudolph");
                    else if(entityCreature instanceof EntityEnt)
                        entityCreature.setCustomNameTag("Salty Tree");
                    else if(entityCreature instanceof EntityTrent)
                        entityCreature.setCustomNameTag("Salty Tree");
                    else if(entityCreature instanceof EntityPhantom)
                        entityCreature.setCustomNameTag("Satan Claws");
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
    }
}
