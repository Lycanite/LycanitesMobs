package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.pets.PetEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.ArrayList;
import java.util.List;

public class GUIFamiliarList extends GuiScrollingList {
    GUIFamiliar parentGUI;
	List<PetEntry> familiarList = new ArrayList<PetEntry>();

	// ==================================================
  	//                    Constructor
  	// ==================================================
	public GUIFamiliarList(GUIFamiliar parentGUI, ExtendedPlayer playerExt, int width, int height, int top, int bottom, int left) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, left, 20);
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
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		PetEntry petEntry = this.familiarList.get(index);
		MobInfo mobInfo = petEntry.summonSet.getMobInfo();
        if(mobInfo == null)
            return;

		int boxLeft = this.left;
		if(petEntry.spawningActive) {
            if(this.isSelected(index))
                super.drawGradientRect(boxLeft, boxTop, boxRight, boxBottom, 0x99FFEE, 0x99FFEE);
            super.drawGradientRect(boxLeft, boxTop, boxRight, boxBottom, 0x448877, 0x448877);
			/*GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tessellator.startDrawingQuads();
			if(this.isSelected(index)) {
				tessellator.setColorRGBA(192, 255, 232, 255);
				tessellator.addVertexWithUV((double) boxLeft, (double) (boxTop + boxBottom + 2), 0.0D, 0.0D, 1.0D);
				tessellator.addVertexWithUV((double) boxRight, (double) (boxTop + boxBottom + 2), 0.0D, 1.0D, 1.0D);
				tessellator.addVertexWithUV((double) boxRight, (double) (boxTop - 2), 0.0D, 1.0D, 0.0D);
				tessellator.addVertexWithUV((double) boxLeft, (double) (boxTop - 2), 0.0D, 0.0D, 0.0D);
			}
			tessellator.setColorRGBA(64, 128, 96, 32);
			tessellator.addVertexWithUV((double) (boxLeft + 1), (double) (boxTop + boxBottom + 1), 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV((double) (boxRight - 1), (double) (boxTop + boxBottom + 1), 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV((double) (boxRight - 1), (double) (boxTop - 1), 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV((double) (boxLeft + 1), (double) (boxTop - 1), 0.0D, 0.0D, 0.0D);
			tessellator.draw();
			GL11.glEnable(GL11.GL_TEXTURE_2D);*/
		}

		this.parentGUI.getFontRenderer().drawString(petEntry.getDisplayName(), boxLeft + 20 , boxTop + 4, 0xFFFFFF);
		Minecraft.getMinecraft().getTextureManager().bindTexture(mobInfo.getIcon());
		this.parentGUI.drawTexturedModalRect(this.left + 2, boxTop, 0, 0, 16, 16);

	}
}
