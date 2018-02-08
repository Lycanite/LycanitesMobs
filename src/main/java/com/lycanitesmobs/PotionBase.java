package com.lycanitesmobs;

import net.minecraft.potion.Potion;

public class PotionBase extends Potion {
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public PotionBase(String name, boolean badEffect, int color) {
		super(badEffect, color);
		this.setRegistryName(LycanitesMobs.modid, name);
		this.setPotionName("effect." + name);
	}
	
	
	// ==================================================
	//                    Visuals
	// ==================================================
	@Override
	 public boolean isInstant() {
        return false;
    }
	
	
	// ==================================================
	//                    Visuals
	// ==================================================
	@Override
	public Potion setIconIndex(int par1, int par2) {
        super.setIconIndex(par1, par2);
        return this;
    }
}
