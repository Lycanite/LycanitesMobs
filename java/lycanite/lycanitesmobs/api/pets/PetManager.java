package lycanite.lycanitesmobs.api.pets;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.EntityLivingBase;

public class PetManager {
	public EntityLivingBase host;
    /** A list of all pet entries, useful for looking up everything summoned by an entity as well as ensuring that no entries are added as multiple types. **/
    public List<PetEntry> allEntries = new ArrayList<PetEntry>();
    /** Pets are mobs that the player has tamed and then bound. They can be summoned and dismissed at will. Eg: Pet Lurker. **/
    public List<PetEntry> pets = new ArrayList<PetEntry>();
    /** Mounts are mobs that the player has tamed and then bound. One can be summoned for riding at will, they will despawn if unmounted after a short while. Eg: Pet Ventoraptor. **/
	public List<PetEntry> mounts = new ArrayList<PetEntry>();
    /** Minions are mobs that the player has summoned. These are temporary. Eg: Summoned Cinder. **/
	public List<PetEntry> minions = new ArrayList<PetEntry>();
    /** Guardians are mobs that are bound to equipment/effects that the player has. They are passively summoned and dismissed based on various conditions. Eg: Cyclone Armor Reiver. **/
	public List<PetEntry> guardians = new ArrayList<PetEntry>();
    /** Familiars are mobs that are bound to the player, they are similar to guardians but aren't dependant on any equipment/effects, etc. Eg: Donation Familiars. **/
	public List<PetEntry> familiars = new ArrayList<PetEntry>();
    // I might also add slaves for mobs that are temporarily under the host's control who can break free, etc instead of despawning.
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetManager(EntityLivingBase host) {
		this.host = host;
	}


    // ==================================================
    //                        Add
    // ==================================================
    /** Adds a new PetEntry and executes onAdd() methods. The provided entry should have set whether it's a pet, mount, minion, etc. **/
    public void addEntry(PetEntry petEntry) {
        if(this.allEntries.contains(petEntry)) {
            LycanitesMobs.printWarning("", "[Pet Manager] Tried to add a Pet Entry that is already added!");
            return;
        }

        this.allEntries.add(petEntry);

        if("pet".equalsIgnoreCase(petEntry.getType()))
            this.pets.add(petEntry);
        else if("mount".equalsIgnoreCase(petEntry.getType()))
            this.mounts.add(petEntry);
        else if("minion".equalsIgnoreCase(petEntry.getType()))
            this.minions.add(petEntry);
        else if("guardian".equalsIgnoreCase(petEntry.getType()))
            this.guardians.add(petEntry);
        else if("familiar".equalsIgnoreCase(petEntry.getType()))
            this.familiars.add(petEntry);

        petEntry.onAdd(this);
    }


    // ==================================================
    //                      Remove
    // ==================================================
    /** Removes an entry from this manager. This is called automatically if the entry itself is no longer active.
     * This will not cause the entry itself to become inactive if it is still active.
     * If an entry is finished, it is best to call onRemove() on the entry itself, this method will then be called automatically. **/
    public void removeEntry(PetEntry petEntry) {
        if(!this.allEntries.contains(petEntry)) {
            LycanitesMobs.printWarning("", "[Pet Manager] Tried to remove a pet entry that isn't added!");
            return;
        }

        this.allEntries.remove(petEntry);

        if("pet".equalsIgnoreCase(petEntry.getType()))
            this.pets.remove(petEntry);
        else if("mount".equalsIgnoreCase(petEntry.getType()))
            this.mounts.remove(petEntry);
        else if("minion".equalsIgnoreCase(petEntry.getType()))
            this.minions.remove(petEntry);
        else if("guardian".equalsIgnoreCase(petEntry.getType()))
            this.guardians.remove(petEntry);
        else if("familiar".equalsIgnoreCase(petEntry.getType()))
            this.familiars.remove(petEntry);
    }
	
	
	// ==================================================
    //                       Update
    // ==================================================
	/** Called by the host's entity update, runs any logic to manage pet entries. **/
	public void onUpdate() {
		// Update Pets:
		for(PetEntry petEntry : this.pets) {
            if(petEntry.active)
			    petEntry.onUpdate();
            else
                this.removeEntry(petEntry);
		}

		// Update Mounts:
		for(PetEntry petEntry : this.mounts) {
            if(petEntry.active)
                petEntry.onUpdate();
            else
                this.removeEntry(petEntry);
		}

		// Update Minions:
		for(PetEntry petEntry : this.minions) {
            if(petEntry.active)
                petEntry.onUpdate();
            else
                this.removeEntry(petEntry);
		}

		// Update Guardians:
		for(PetEntry petEntry : this.guardians) {
            if(petEntry.active)
                petEntry.onUpdate();
            else
                this.removeEntry(petEntry);
		}

		// Update Familiars:
		for(PetEntry petEntry : this.familiars) {
            if(petEntry.active)
                petEntry.onUpdate();
            else
                this.removeEntry(petEntry);
		}
	}
}
