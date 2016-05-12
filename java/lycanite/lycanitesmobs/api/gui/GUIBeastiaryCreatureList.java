package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.HashMap;
import java.util.Map;

public class GUIBeastiaryCreatureList extends GuiScrollingList {
	GUIBeastiary parentGUI;
	Map<Integer, MobInfo> creatureList = new HashMap<Integer, MobInfo>();
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBeastiaryCreatureList(GUIBeastiary parentGUI, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 20);
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
			if(mobInfo != null && mobInfo.group == this.parentGUI.getSelectedGroup()) {
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
		this.parentGUI.getFontRenderer().drawString(mobInfo.getTitle(), this.left + 20 , boxTop + 4, 0xFFFFFF);
		if(mobInfo.getIcon() != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
			this.parentGUI.drawTexturedModalRect(this.left + 2, boxTop, 0, 0, 16, 16, 16);
		}
	}
}
