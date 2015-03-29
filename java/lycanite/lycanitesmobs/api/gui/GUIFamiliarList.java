package lycanite.lycanitesmobs.api.gui;

import cpw.mods.fml.client.GuiScrollingList;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIFamiliarList extends GuiScrollingList {
    GUIFamiliar parentGUI;
	List<PetEntry> familiarList = new ArrayList<PetEntry>();

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIFamiliarList(GUIFamiliar parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left, int entryHeight) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, entryHeight);
		this.parentGUI = parentGUI;
		this.familiarList = playerExt.petManager.getEntryList("familiar");
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	@Override
	protected int getSize() {
		return familiarList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectPetEntry(this.familiarList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedPetEntry() == this.familiarList.get(index);
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawBackground() {}

    @Override
    protected int getContentHeight() {
        return this.getSize() * 24;
    }

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		MobInfo mobInfo = this.familiarList.get(index).summonSet.getMobInfo();
        if(mobInfo == null)
            return;
		this.parentGUI.getFontRenderer().drawString(mobInfo.getTitle(), this.left + 18 , boxTop + 4, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
		this.parentGUI.drawImage(this.left, boxTop, 0, 0, 16, 16, 0.0625F, 0.0625F);
	}
}
