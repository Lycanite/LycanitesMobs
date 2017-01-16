package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.gui.*;
import lycanite.lycanitesmobs.core.inventory.ContainerCreature;
import lycanite.lycanitesmobs.core.tileentity.TileEntityBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	public static GuiHandler instance;
	
	// GUI IDs:
    public static enum GuiType {
		TILEENTITY((byte)0), ENTITY((byte)1), ITEM((byte)2), PLAYER((byte)3);
		public byte id;
		private GuiType(byte i) { id = i; }
	}
    public static enum PlayerGuiType {
		LM_MAIN_MENU((byte)0), BEASTIARY((byte)1), PET_MANAGER((byte)2), MOUNT_MANAGER((byte)3), FAMILIAR_MANAGER((byte)4), MINION_MANAGER((byte)5), MINION_SELECTION((byte)6);
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
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if(tileEntity instanceof TileEntityBase)
                return ((TileEntityBase)tileEntity).getGUI(player);

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
			return null;
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
			TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity instanceof TileEntityBase)
                return ((TileEntityBase)tileEntity).getGUI(player);
		}
		
		// ========== Entity ==========
		else if(id == GuiType.ENTITY.id) {
			Entity entity = world.getEntityByID(x);
			if(entity instanceof EntityCreatureBase)
				return new GUICreature((EntityCreatureBase)entity, player.inventory);
		}
		
		// ========== Item ==========
		else if(id == GuiType.ITEM.id) {
			// No item GUIs just yet.
		}
		
		// ========== Player ==========
		else if(id == GuiType.PLAYER.id) {
			if(x == PlayerGuiType.LM_MAIN_MENU.id) {
				return new GUILMMainMenu(player);
			}
			if(x == PlayerGuiType.BEASTIARY.id) {
				return new GUIBeastiary(player);
			}
			if(x == PlayerGuiType.PET_MANAGER.id) {
				return new GUIPetManager(player);
			}
			if(x == PlayerGuiType.MOUNT_MANAGER.id) {
				return new GUIMountManager(player);
			}
            if(x == PlayerGuiType.FAMILIAR_MANAGER.id) {
                return new GUIFamiliar(player);
            }
            if(x == PlayerGuiType.MINION_MANAGER.id) {
                return new GUIMinion(player, y);
            }
			if(x == PlayerGuiType.MINION_SELECTION.id) {
				return new GUIMinionSelection(player);
			}
		}
		
		return null;
	}
	
}
