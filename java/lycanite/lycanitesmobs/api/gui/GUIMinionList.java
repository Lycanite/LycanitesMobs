package lycanite.lycanitesmobs.api.gui;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.client.GuiScrollingList;

public class GUIMinionList extends GuiScrollingList {
	GUIMinion parentGUI;
	Map<Integer, String> minionList = new HashMap<Integer, String>();
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMinionList(GUIMinion parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left, int entryHeight) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, entryHeight);
		this.parentGUI = parentGUI;
		int minionIndex = 0;
		for(String minionName : playerExt.beastiary.creatureKnowledgeList.keySet()) {
			this.minionList.put(minionIndex, minionName);
			minionIndex++;
		}
	}

	@Override
	protected int getSize() {
		return this.parentGUI.playerExt.beastiary.creatureKnowledgeList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectMinion(this.minionList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedMinion() != null && this.parentGUI.getSelectedMinion().equals(this.minionList.get(index));
	}

	@Override
	protected void drawBackground() {
		//this.parentGUI.drawDefaultBackground();
	}

    @Override
    protected int getContentHeight() {
        return (this.getSize()) * 35 + 1;
    }

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		String mobTitle = MobInfo.mobNameToInfo.get(this.minionList.get(index)).title;
		this.parentGUI.getFontRenderer().drawString(mobTitle, this.left + 3 , boxTop + 2, 0xFFFFFF);
	}
}
