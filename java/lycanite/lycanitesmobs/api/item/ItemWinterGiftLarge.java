package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityItemCustom;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityWendigo;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBehemoth;
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

public class ItemWinterGiftLarge extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemWinterGiftLarge() {
        super();
        this.group = LycanitesMobs.group;
        this.itemName = "wintergiftlarge";
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
         	this.open(itemStack, world, player);
         }

         return itemStack;
     }
    
    
    // ==================================================
  	//                       Open
  	// ==================================================
    public void open(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = StatCollector.translateToLocal("item." + this.itemName + ".bad");
		player.addChatMessage(new ChatComponentText(message));
		if(AssetManager.getSound(this.itemName + "_bad") == null)
			AssetManager.addSound(this.itemName + "_bad", this.group, "item." + this.itemName + ".bad");
		world.playSoundAtEntity(player, AssetManager.getSound(this.itemName + "_bad"), 5.0F, 1.0F);
		
		// Lots of Random Tricks:
		Class[] entityClasses = ObjectLists.getEntites("winter_tricks");
        if(entityClasses == null) return;
		if(entityClasses.length <= 0) return;
        for(int i = 0; i < 15; i++) {
            Class entityClass = entityClasses[player.getRNG().nextInt(entityClasses.length)];
            if(entityClass != null) {
                Entity entity = null;
                try {
                    entity = (Entity) entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (entity != null) {
                    entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);

                    // Themed Names:
                    if (entity instanceof EntityLivingBase) {
                        EntityCreatureBase entityCreature = (EntityCreatureBase) entity;
                        if (entityCreature instanceof EntityWendigo)
                            entityCreature.setCustomNameTag("Gooderness");
                        else if (entityCreature instanceof EntityJabberwock)
                            entityCreature.setCustomNameTag("Rudolph");
                        else if (entityCreature instanceof EntityEnt)
                            entityCreature.setCustomNameTag("Salty Tree");
                        else if (entityCreature instanceof EntityTrent)
                            entityCreature.setCustomNameTag("Salty Tree");
                        else if (entityCreature instanceof EntityPhantom)
                            entityCreature.setCustomNameTag("Satan Claws");
                        else if(entityCreature instanceof EntityBehemoth)
                            entityCreature.setCustomNameTag("Krampus");
                    }

                    world.spawnEntityInWorld(entity);
                }
            }
        }
    }
}
