package com.lycanitesmobs;

import net.minecraft.potion.Potion;

public class PotionBase extends Potion {
	public static int customPotionOffset = 0;
	public static int customPotionLength = 24;
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public static void reserveEffectIDSpace() {
		/*int vanillaLength = 32;
		int safeLength = 128;
		int originalLength = Potion.potionTypes.length;
		int newLength = originalLength;
		if(originalLength < safeLength)
			newLength = originalLength + customPotionLength;
		customPotionOffset = Math.min(originalLength, (safeLength - 1) - customPotionLength);

		LycanitesMobs.printDebug("EffectsSetup", "~O========== Custom Potion Effects Setup ==========O~");
		LycanitesMobs.printDebug("EffectsSetup", "Initial size is: " + originalLength + " (vanilla size is: " + vanillaLength + ")");
		if(originalLength < safeLength)
			LycanitesMobs.printDebug("EffectsSetup", "Size will be increased by: " + customPotionLength + " as it is smaller than the safe limit of " + safeLength + ".");
		LycanitesMobs.printDebug("EffectsSetup", "New Size will be: " + newLength);
		LycanitesMobs.printDebug("EffectsSetup", "New Effects from this mod should automatically start with ID: " + customPotionOffset + " up to " + (customPotionOffset + customPotionLength) + " (Reserving 24 slots for current and future effects.) Note: IDs beyond 127 can cause crashes! This mod is forced to this cap.");
		if(originalLength > vanillaLength) {
			LycanitesMobs.printDebug("EffectsSetup", "The initial size is larger than the vanilla which means another mod has increased its size, here it should then be increased further, unless it is at the safe limit.");
			LycanitesMobs.printDebug("EffectsSetup", "Any mods that add effects after this mod can then extend the list further.");
		}
		
		Potion[] newPotionTypes = new Potion[newLength];
		LycanitesMobs.printDebug("EffectsSetup", "Created the new extended list: " + newPotionTypes);
		System.arraycopy(Potion.potionTypes, 0, newPotionTypes, 0, originalLength);
		
		LMReflectionHelper.setPrivateFinalValue(Potion.class, null, newPotionTypes, "potionTypes", "field_76425_a");
		LycanitesMobs.printDebug("EffectsSetup", "Replaced the old list with the new list: " + Potion.potionTypes);
		LycanitesMobs.printDebug("EffectsSetup", "New list length is: " + Potion.potionTypes.length + " this should be " + newLength);*/
	}
	
	public static int getNextPotionID() {
		return customPotionOffset++;
	}
	
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public PotionBase(String name, boolean badEffect, int color) {
		super(badEffect, color);
		this.setPotionName(name);
		this.effectAdded(false);
	}
	
	public void effectAdded(boolean forced) {
		/*if(this.getId() >= Potion.potionTypes.length) {
			LycanitesMobs.printWarning("", "A problem occured when adding a new Potion Effect:");
			LycanitesMobs.printWarning("", "The effect ID is higher than the effect bounds, this will cause problems in game. For safety the game has been crashed!");
			if(forced)
				LycanitesMobs.printWarning("", "Automatic Effect IDs should work now, please try them, if the error persists please enable Custmo Effects Debugging via the config and psot the bug report.");
			else
				LycanitesMobs.printWarning("", "Please enable Effect ID Debugging via the config and report this bug along the debug into and any mods that may also add custom effect IDs.");
			LycanitesMobs.printWarning("", "You can disable custom effects via the config which should stop this issue, however the custom effects wont be available.");
			LycanitesMobs.printWarning("", "Potion Effect Name: " + this.getName() + " ID: " + this.getId());
			throw new RuntimeException();
		}
		LycanitesMobs.printDebug("EffectsSetup", "New Potion Effect added: " + this.getName() + "(ID: " + this.getId() + (forced ? " FORCED" : "") + ")");*/
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
