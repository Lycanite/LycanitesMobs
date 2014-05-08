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
		
		for(Field field : Potion.class.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				if(field.getName().equals("potionTypes") || field.getName().equals("field_76425_a")) {
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
					
					Potion[] potionTypes;
					potionTypes = (Potion[])field.get(null);
					
					final Potion[] newPotionTypes = new Potion[newLength];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					field.set(null, newPotionTypes);
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
