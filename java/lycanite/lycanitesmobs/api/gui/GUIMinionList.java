package lycanite.lycanitesmobs.api.gui;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.client.GuiScrollingList;
import org.lwjgl.opengl.GL11;

public class GUIMinionList extends GuiScrollingList {
	GUIMinion parentGUI;
	Map<Integer, String> minionList = new HashMap<Integer, String>();
	
	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIMinionList(GUIMinion parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 28);
		this.parentGUI = parentGUI;
		this.minionList = playerExt.getBeastiary().getSummonableList();
	}
	
	
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
        return this.getSize() * 24;
    }

	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		String mobName = this.minionList.get(index);
		MobInfo mobInfo = MobInfo.mobNameToInfo.get(mobName);

		int boxLeft = this.left;
		int levelBarWidth = 9;
		int levelBarHeight = 9;
		int levelBarX = boxLeft + 20;
		int levelBarY = boxTop + boxBottom - levelBarHeight - 4;
		int levelBarU = 256 - (levelBarWidth * 2);
		int levelBarV = 256 - levelBarHeight;
		int level = mobInfo.summonCost;

		// Summon Level:
		Minecraft.getMinecraft().getTextureManager().bindTexture(AssetManager.getTexture("GUIBeastiary"));
		for(int currentLevel = 0; currentLevel < level; currentLevel++) {
			this.parentGUI.drawImage(levelBarX + (levelBarWidth * currentLevel), levelBarY, levelBarU, levelBarV, levelBarWidth, levelBarHeight, 0.00390625F, 0.00390625F);
		}

		this.parentGUI.getFontRenderer().drawString(mobInfo.getTitle(), this.left + 20 , boxTop + 4, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
		this.parentGUI.drawImage(this.left + 2, boxTop + 4, 0, 0, 16, 16, 0.0625F, 0.0625F);
	}
}
