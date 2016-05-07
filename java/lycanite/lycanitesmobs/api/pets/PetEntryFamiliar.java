package lycanite.lycanitesmobs.api.pets;


import net.minecraft.entity.EntityLivingBase;

public class PetEntryFamiliar extends PetEntry {

    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntryFamiliar(String name, EntityLivingBase host, String summonType) {
        super(name, "familiar", host, summonType);
	}
}
