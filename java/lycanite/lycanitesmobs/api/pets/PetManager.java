package lycanite.lycanitesmobs.api.pets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;

public class PetManager {
	public EntityLivingBase host;
	public List<PetEntry> pets = new ArrayList<PetEntry>();
	public List<PetEntry> mounts = new ArrayList<PetEntry>();
	public List<PetEntry> minions = new ArrayList<PetEntry>();
	public List<PetEntry> guardians = new ArrayList<PetEntry>();
	public List<PetEntry> familiars = new ArrayList<PetEntry>();
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetManager(EntityLivingBase host) {
		this.host = host;
	}
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the host's entity update, runs any logic to manage pet entries. **/
	public void onUpdate() {
		// Update Pets:
		for(PetEntry petEntry : this.pets) {
			petEntry.onUpdate();
		}

		// Update Mounts:
		for(PetEntry petEntry : this.mounts) {
			petEntry.onUpdate();
		}

		// Update Minions:
		for(PetEntry petEntry : this.minions) {
			petEntry.onUpdate();
		}

		// Update Guardians:
		for(PetEntry petEntry : this.guardians) {
			petEntry.onUpdate();
		}

		// Update Familiars:
		for(PetEntry petEntry : this.familiars) {
			petEntry.onUpdate();
		}
	}
}
