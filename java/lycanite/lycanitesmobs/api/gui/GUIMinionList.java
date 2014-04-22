package lycanite.lycanitesmobs.api.gui;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.SummonSet;
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
		for(String minionName : playerExt.getBeastiary().creatureKnowledgeList.keySet()) {
			if(SummonSet.isSummonableCreature(minionName)) {
				this.minionList.put(minionIndex, minionName);
				minionIndex++;
			}
		}
	}
	
	
	// ==================================================
  	//                    Draw Screen
  	// ==================================================
	//TODO Fix the graphical glitch!
	
	
	// ==================================================
  	//                    List Info
  	// ==================================================
	@Override
	protected int getSize() {
		return minionList.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGUI.selectMinion(this.minionList.get(index));
	}

	@Override
	protected boolean isSelected(int index) {
		return this.parentGUI.getSelectedMinion() != null && this.parentGUI.getSelectedMinion().equals(this.minionList.get(index));
	}
	
	
	// ==================================================
  	//                    Background
  	// ==================================================
	@Override
	protected void drawBackground() {}

    @Override
    protected int getContentHeight() {
        return (this.getSize()) * 35 + 1;
    }

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		String mobName = this.minionList.get(index);
		MobInfo mobInfo = MobInfo.mobNameToInfo.get(mobName);
		this.parentGUI.getFontRenderer().drawString(mobInfo.title, this.left + 18 , boxTop + 4, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
		this.parentGUI.drawImage(this.left, boxTop, 0, 0, 16, 16, 0.0625F, 0.0625F);
	}
}
