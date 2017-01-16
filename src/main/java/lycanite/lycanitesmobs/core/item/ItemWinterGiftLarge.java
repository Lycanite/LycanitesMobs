package lycanite.lycanitesmobs.core.item;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
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
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
         if(!player.capabilities.isCreativeMode) {
             --itemStack.stackSize;
         }
         
         if(!world.isRemote) {
         	this.open(itemStack, world, player);
         }

        return new ActionResult(EnumActionResult.SUCCESS, itemStack);
     }
    
    
    // ==================================================
  	//                       Open
  	// ==================================================
    public void open(ItemStack itemStack, World world, EntityPlayer player) {
    	String message = I18n.translateToLocal("item." + this.itemName + ".bad");
		player.addChatMessage(new TextComponentString(message));
		if(AssetManager.getSound(this.itemName + "_bad") == null)
			AssetManager.addSound(this.itemName + "_bad", this.group, "item." + this.itemName + ".bad");
        this.playSound(world, player.getPosition(), AssetManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);
		
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
    }
}
