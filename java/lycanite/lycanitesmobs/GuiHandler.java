package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.gui.GUICreature;
import lycanite.lycanitesmobs.api.inventory.ContainerBase;
import lycanite.lycanitesmobs.api.inventory.ContainerCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static GuiHandler instance;
	
	// GUI IDs:
    public static enum GuiType {
		TILEENTITY((byte)0), ENTITY((byte)1), ITEM((byte)2), PLAYER((byte)3);
		public byte id;
		private GuiType(byte i) { id = i; }
	}
    public static enum PlayerGuiType {
		MINION_CONTROLS((byte)0), BEASTIARY((byte)1), SOULBOUND_PETS((byte)2);
		public byte id;
		private PlayerGuiType(byte i) { id = i; }
	}
    
    // ==================================================
    //                     Constructor
    // ==================================================
    public GuiHandler() {
    	instance = this;
    }
    
    
    // ==================================================
    //                      Server
    // ==================================================
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		// ========== Tile Entity ==========
		if(id == GuiType.TILEENTITY.id) {
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			// Use instanceof to distinguish tile entity actions.
		}
		
		// ========== Entity ==========
		else if(id == GuiType.ENTITY.id) {
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityCreatureBase)
				return new ContainerCreature((EntityCreatureBase)entity, player.inventory);
		}
		
		// ========== Item ==========
		else if(id == GuiType.ITEM.id) {
			// No item GUIs just yet.
		}
		
		// ========== Player ==========
		else if(id == GuiType.PLAYER.id) {
			if(x == PlayerGuiType.MINION_CONTROLS.id) {
				return new ContainerBase();
			}
		}
		
		return null;
	}
    
    
    // ==================================================
    //                      Client
    // ==================================================
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		// ========== Tile Entity ==========
		if(id == GuiType.TILEENTITY.id) {
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
			// Use instanceof to distinguish tile entity actions.
		}
		
		// ========== Entity ==========
		else if(id == GuiType.ENTITY.id) {
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityCreatureBase)
				return new GUICreature((EntityCreatureBase)entity, player.inventory);
		}
		
		// ========== Item ==========
		else if(id == GuiType.ITEM.id) {
			// Ready for the Soul Cube!
		}
		
		return null;
	}
	
}
