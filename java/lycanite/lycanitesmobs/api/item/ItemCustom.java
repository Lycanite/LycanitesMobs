package lycanite.lycanitesmobs.api.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemCustom extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemCustom(String itemName, GroupInfo group) {
        super();
        this.itemName = itemName;
        this.group = group;
        this.setCreativeTab(LycanitesMobs.itemsTab);
        this.setup();
    }
}
