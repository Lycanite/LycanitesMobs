package lycanite.lycanitesmobs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.potion.Potion;

public class PotionBase extends Potion {
	public static int customPotionStartID = 0;
	private static int customPotionNextID = 0;
	public static int customPotionLength = 24;
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public static void reserveEffectIDSpace() {
		customPotionStartID = Potion.potionTypes.length;
		customPotionNextID = customPotionStartID;
		int newLength = customPotionStartID + customPotionLength;
		LycanitesMobs.printDebug("EffectsSetup", "~O========== Custom Potion Effects Setup ==========O~");
		LycanitesMobs.printDebug("EffectsSetup", "Initial size is: " + Potion.potionTypes.length + " (vanilla size is: 32)");
		LycanitesMobs.printDebug("EffectsSetup", "Size will be increased by: " + customPotionLength);
		LycanitesMobs.printDebug("EffectsSetup", "New Size will be: " + newLength);
		LycanitesMobs.printDebug("EffectsSetup", "New Effects from this mod should automatically start with ID: " + customPotionStartID + " They shouldn't exceed: " + (newLength - 1));
		LycanitesMobs.printDebug("EffectsSetup", "If the initial size is larger than the vanilla then another mod has increased its size, here it should then be increased further.");
		LycanitesMobs.printDebug("EffectsSetup", "Any mods that add effects after this mod should then extend the list further where ID " + (newLength - 1) + " may then be exceed, but not by this mod.");
		
		for(Field field : Potion.class.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				if(field.getName().equals("potionTypes") || field.getName().equals("field_76425_a")) {
					LycanitesMobs.printDebug("EffectsSetup", "Resizing the potion list, the field name is: " + field.getName());
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
					
					Potion[] potionTypes;
					potionTypes = (Potion[])field.get(null);
					LycanitesMobs.printDebug("EffectsSetup", "Got the initial list: " + potionTypes);
					
					final Potion[] newPotionTypes = new Potion[newLength];
					LycanitesMobs.printDebug("EffectsSetup", "Created the new extended list: " + newPotionTypes);
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					LycanitesMobs.printDebug("EffectsSetup", "Copied the effects into the new list: " + newPotionTypes);
					field.set(null, newPotionTypes);
					LycanitesMobs.printDebug("EffectsSetup", "Replaced the old list with the new list: " + Potion.potionTypes);
					LycanitesMobs.printDebug("EffectsSetup", "New list length is: " + Potion.potionTypes.length + " this should be " + newLength);
				}
			}
			catch (Exception e) {
				System.err.println("[Lycanites Mobs] An error occured when adding custom potion effects:");
				System.err.println(e);
			}
		}
	}
	
	public static int nextPotionID() {
		int nextID = Math.min(customPotionNextID, customPotionStartID + customPotionLength);
		customPotionNextID++;
		return nextID;
	}
	
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public PotionBase(boolean badEffect, int color) {
		super(nextPotionID(), badEffect, color);
		this.effectAdded(false);
	}

	public PotionBase(int forcedID, boolean badEffect, int color) {
		super(forcedID, badEffect, color);
		this.effectAdded(true);
	}
	
	public void effectAdded(boolean forced) {
		if(this.getId() >= Potion.potionTypes.length) {
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
		LycanitesMobs.printDebug("EffectsSetup", "New Potion Effect added: " + this.getName() + "(ID: " + this.getId() + (forced ? " FORCED" : "") + ")");
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
