package com.lycanitesmobs;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

public class CommonProxy {
	
	// ========== Register Event Handlers ==========
    public void registerEvents() {
	}
	
	
	// ========== Register Tile Entities ==========
	public void registerTileEntities() {
		// None
	}
    
	
    // ========== Get Minecraft Directory ==========
    public File getMinecraftDir() {
    	return new File(".");
    }
	
	
	// ========== Client Only ==========
    public void registerAssets() {}
    public void registerRenders(GroupInfo groupInfo) {}
    public EntityPlayer getClientPlayer() { return null; }


    // ========== Renders ==========
    public void addBlockRender(GroupInfo group, Block block) {}
    public void addItemRender(GroupInfo group, Item item) {}

	/**
	 * Returns the Font Renderer used by Lycnaites Mobs.
	 * @return A sexy Font Renderer, thanks for the heads up CedKilleur!
	 */
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.gui.FontRenderer getFontRenderer() {
		return null;
	}
}
