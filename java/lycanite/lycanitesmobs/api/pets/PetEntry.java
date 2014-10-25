package lycanite.lycanitesmobs.api.pets;


public class PetEntry {
	PetManager petManager;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntry(PetManager petManager) {
		this.petManager = petManager;
	}
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the PetManager, runs any logic for this entry. **/
	public void onUpdate() {
		
	}
}
