package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.model.ModelItemBase;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class EquipmentPartRenderer extends TileEntitySpecialRenderer<TileEntityEquipmentPart> implements IItemModelRenderer {

	@Override
	public void render(TileEntityEquipmentPart te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ItemStack itemStack = ItemEquipmentPart.ITEMSTACK_TO_RENDER; // This is disgusting haxx, I am sorry, but I can't see another way. :(
		ItemEquipmentPart.ITEMSTACK_TO_RENDER = null;

		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return;
		}

		EnumHand hand = null;

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);

		GlStateManager.translate(0.9F, -0.5F, -0.5F);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(45, 0, 0, 1);
		modelItemBase.render(itemStack, hand, this, null);
		GlStateManager.popMatrix();

		if(te != null) {
			super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		}
	}

	@Override
	public void bindItemTexture(ResourceLocation location) {
		if(location == null) {
			return;
		}
		this.bindTexture(location);
	}
}
