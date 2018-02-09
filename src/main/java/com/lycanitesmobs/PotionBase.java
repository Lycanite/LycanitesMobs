package com.lycanitesmobs;

import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBase extends Potion {
	public String name;
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public PotionBase(String name, boolean badEffect, int color) {
		super(badEffect, color);
		this.name = name;
		this.setRegistryName(LycanitesMobs.modid, name);
		this.setPotionName("effect." + name);
		AssetManager.addTexture("effect." + name, LycanitesMobs.group, "textures/effects/" + name + ".png");
	}
	
	
	// ==================================================
	//                    Effects
	// ==================================================
	@Override
	public boolean isInstant() {
        return false;
    }
	
	
	// ==================================================
	//                    Visuals
	// ==================================================
	@SideOnly(Side.CLIENT)
	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) {
		if (mc.currentScreen == null) {
			return;
		}

		ResourceLocation texture = AssetManager.getTexture("effect." + this.name);
		if(texture == null) {
			return;
		}

		mc.getTextureManager().bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) {
		ResourceLocation texture = AssetManager.getTexture("effect." + this.name);
		if(texture == null) {
			return;
		}

		mc.getTextureManager().bindTexture(texture);
		Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
	}
}
