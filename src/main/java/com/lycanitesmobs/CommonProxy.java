package com.lycanitesmobs;

import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

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
}
