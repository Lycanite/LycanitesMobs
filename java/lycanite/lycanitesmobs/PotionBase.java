package lycanite.lycanitesmobs;

import net.minecraft.potion.Potion;

public class PotionBase extends Potion {
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public PotionBase(int id, boolean badEffect, int color) {
		super(id, badEffect, color);
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
