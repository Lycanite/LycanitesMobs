package lycanite.lycanitesmobs.api.gui;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.client.GuiScrollingList;

public class GUIBeastiaryCreatureList extends GuiScrollingList {
	GUIBeastiary parentGUI;
	Map<Integer, MobInfo> creatureList = new HashMap<Integer, MobInfo>();
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBeastiaryCreatureList(GUIBeastiary parentGUI, int width, int height, int top, int bottom, int left, int entryHeight) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, entryHeight);
		this.parentGUI = parentGUI;
		this.updateList();
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	public void updateList() {
		this.creatureList = new HashMap<Integer, MobInfo>();
		if(this.parentGUI.getSelectedGroup() == null)
			return;
		
		int creatureIndex = 0;
		for(String minionName : this.parentGUI.playerExt.getBeastiary().creatureKnowledgeList.keySet()) {
			MobInfo mobInfo = MobInfo.getFromName(minionName.toLowerCase());
			if(mobInfo != null && mobInfo.mod == this.parentGUI.getSelectedGroup()) {
				this.creatureList.put(creatureIndex++, mobInfo);
			}
		}
	}
	
	@Override
	protected int getSize() {
		return creatureList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectCreature(this.creatureList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedCreature() != null && this.parentGUI.getSelectedCreature().equals(this.creatureList.get(index));
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
		MobInfo mobInfo = this.creatureList.get(index);
		if(mobInfo == null) return;
		this.parentGUI.getFontRenderer().drawString(mobInfo.getTitle(), this.left + 18 , boxTop + 4, 0xFFFFFF);
		if(mobInfo.getIcon() != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
			this.parentGUI.drawImage(this.left, boxTop, 0, 0, 16, 16, 0.0625F, 0.0625F);
		}
	}
}
