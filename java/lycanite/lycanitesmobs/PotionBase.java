package lycanite.lycanitesmobs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.minecraft.potion.Potion;

public class PotionBase extends Potion {
	public static int customPotionStartID = 0;
	public static int customPotionLength = 24;
	
	// ==================================================
	//                    Constructor
	// ==================================================
	public static void reserveEffectIDSpace() {
		customPotionStartID = Potion.potionTypes.length;
		int newLength = customPotionStartID + customPotionLength;
		
		for(Field f : Potion.class.getDeclaredFields()) {
			f.setAccessible(true);
			try {
				if(f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
					
					Potion[] potionTypes;
					potionTypes = (Potion[])f.get(null);
					
					final Potion[] newPotionTypes = new Potion[newLength];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					f.set(null, newPotionTypes);
				}
			}
			catch (Exception e) {
				System.err.println("[Lycanites Mobs] An error occured when adding custom potion effects:");
				System.err.println(e);
			}
		}
	}
	
	
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
