package lycanite.lycanitesmobs.api.gui;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.client.GuiScrollingList;

public class GUIBeastiaryGroupList extends GuiScrollingList {
	GUIBeastiary parentGUI;
	Map<Integer, GroupInfo> groupList = new HashMap<Integer, GroupInfo>();
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIBeastiaryGroupList(GUIBeastiary parentGUI, int width, int height, int top, int bottom, int left, int entryHeight) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, entryHeight);
		this.parentGUI = parentGUI;
		this.updateList();
	}
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	public void updateList() {
		this.groupList = new HashMap<Integer, GroupInfo>();
		
		int groupIndex = 0;
		for(String groupName : MobInfo.mobGroups.keySet()) {
			GroupInfo group = MobInfo.mobGroups.get(groupName);
			if(group != null && parentGUI.playerExt.beastiary.hasCreatureFromGroup(group)) {
				this.groupList.put(groupIndex++, group);
			}
		}
	}
	
	@Override
	protected int getSize() {
		return groupList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectGroup(this.groupList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedGroup() != null && this.parentGUI.getSelectedGroup().equals(this.groupList.get(index));
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
		GroupInfo group = this.groupList.get(index);
		if(group == null) return;
		this.parentGUI.getFontRenderer().drawString(StatCollector.translateToLocal(group.filename + ".name"), this.left + 2 , boxTop + 4, 0xFFFFFF);
	}
}
