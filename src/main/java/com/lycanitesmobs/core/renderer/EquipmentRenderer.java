package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.model.ModelEquipment;
import com.lycanitesmobs.core.tileentity.TileEntityEquipment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class EquipmentRenderer extends TileEntitySpecialRenderer<TileEntityEquipment> implements IItemModelRenderer {

	@Override
	public void render(TileEntityEquipment te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ItemStack itemStack = ItemEquipment.ITEMSTACK_TO_RENDER; // This is disgusting haxx, I am sorry, but I can't see another way. :(
		ItemEquipment.ITEMSTACK_TO_RENDER = null;

		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}

		EnumHand hand = null;

		// Position:
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.2F, 0.6F);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(-90, 0, 0, 1);
		GlStateManager.translate(0F, -1.5F, 0F);
		ModelEquipment modelEquipment = new ModelEquipment();
		modelEquipment.render(itemStack, hand, this);
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
