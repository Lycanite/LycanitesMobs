package lycanite.lycanitesmobs.api.pets;

import java.util.*;

import lycanite.lycanitesmobs.ExtendedPlayer;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.CreatureKnowledge;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class PetManager {
	public EntityLivingBase host;
    /** The next ID to use when adding an entry to the main list. **/
    protected int nextID = 0;
    /** A list of all pet entries, useful for looking up everything summoned by an entity as well as ensuring that no entries are added as multiple types. **/
    public Map<Integer, PetEntry> allEntries = new HashMap<Integer, PetEntry>();
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

    /** A map containing NBT Tag Compunds mapped to Unique Pet Entry Names. **/
    public Map<String, NBTTagCompound> entryNBTs = new HashMap<String, NBTTagCompound>();
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public PetManager(EntityLivingBase host) {
		this.host = host;
	}


    // ==================================================
    //                       Check
    // ==================================================
    /** Returns true if the provided entry is in this manager. **/
    public boolean hasEntry(PetEntry petEntry) {
        return this.allEntries.containsValue(petEntry);
    }


    // ==================================================
    //                        Add
    // ==================================================
    /** Adds a new PetEntry and executes onAdd() methods. The provided entry should have set whether it's a pet, mount, minion, etc. **/
    public void addEntry(PetEntry petEntry) {
        this.addEntry(petEntry, -1);
    }
    /** Adds a new PetEntry and executes onAdd() methods. The provided entry should have set whether it's a pet, mount, minion, etc. This will also set a specific ID for the entry to use which should only really be done client side. **/
    public void addEntry(PetEntry petEntry, int entryID) {
        if(this.allEntries.containsValue(petEntry)) {
            LycanitesMobs.printWarning("", "[Pet Manager] Tried to add a Pet Entry that is already added!");
            return;
        }

        // Load From NBT:
        if(this.entryNBTs.containsKey(petEntry.name))
            petEntry.readFromNBT(this.entryNBTs.get(petEntry.name));

        if(entryID < 0)
            entryID = this.nextID++;
        else if(entryID >= this.nextID)
            this.nextID = entryID + 1;
        this.allEntries.put(entryID, petEntry);

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

        petEntry.onAdd(this, entryID);

        if(this.host instanceof EntityPlayer) {
            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
            if(playerExt != null)
                playerExt.sendPetEntryToPlayer(petEntry);
        }
    }


    // ==================================================
    //                      Remove
    // ==================================================
    /** Removes an entry from this manager. This is called automatically if the entry itself is no longer active.
     * This will not cause the entry itself to become inactive if it is still active.
     * If an entry is finished, it is best to call onRemove() on the entry itself, this method will then be called automatically. **/
    public void removeEntry(PetEntry petEntry) {
        if(!this.allEntries.containsValue(petEntry)) {
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
	public void onUpdate(World world) {
		for(PetEntry petEntry : this.allEntries.values()) {
            if(petEntry.active)
			    petEntry.onUpdate(world);
            else
                this.removeEntry(petEntry);
		}
	}


    // ==================================================
    //                        Get
    // ==================================================
    /** Returns the requested pet entry from its specific id. **/
    public PetEntry getEntry(int id) {
        return this.allEntries.get(id);
    }

    /** Returns the requested pet entry from the specified type by id. **/
    public PetEntry getEntry(String type, int id) {
        return this.getEntryList(type).get(id);
    }

    /** Returns the requested entry list. **/
    public List<PetEntry> getEntryList(String type) {
        if("pet".equalsIgnoreCase(type))
            return this.pets;
        else if("mount".equalsIgnoreCase(type))
            return this.mounts;
        else if("minion".equalsIgnoreCase(type))
            return this.minions;
        else if("guardian".equalsIgnoreCase(type))
            return this.guardians;
        else if("familiar".equalsIgnoreCase(type))
            return this.familiars;
        return null;
    }


    // ==================================================
    //                        NBT
    // ==================================================
    // ========== Read ===========
    /** Reads a list of Pet Entries from a player's NBTTag. **/
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        if(!nbtTagCompound.hasKey("PetManager"))
            return;
        this.entryNBTs = new HashMap<String, NBTTagCompound>();

        // Load All NBT Data Into The Map:
        NBTTagList entryList = nbtTagCompound.getTagList("PetManager", 10);
        for(int i = 0; i < entryList.tagCount(); ++i) {
            NBTTagCompound nbtEntry = (NBTTagCompound)entryList.getCompoundTagAt(i);
            if(nbtEntry.hasKey("EntryName"))
                this.entryNBTs.put(nbtEntry.getString("EntryName"), nbtEntry);
        }

        // Have All Entries In Use Read From The Map:
        for(PetEntry petEntry : this.allEntries.values()) {
            if(this.entryNBTs.containsKey(petEntry.name)) {
                petEntry.readFromNBT(this.entryNBTs.get(petEntry.name));
                this.entryNBTs.remove(petEntry.name);
            }
        }

        // Create New Entries For Pets and Mounts:
        for(NBTTagCompound nbtEntry : this.entryNBTs.values()) {
            if(nbtEntry.hasKey("Type") && ("pet".equalsIgnoreCase(nbtEntry.getString("Type")) || "mount".equalsIgnoreCase(nbtEntry.getString("Type")))) {
                PetEntry petEntry = new PetEntry(nbtEntry.getString("EntryName"), nbtEntry.getString("Type"), this.host, nbtEntry.getString("SummonType"));
                petEntry.readFromNBT(nbtEntry);
                this.addEntry(petEntry);
            }
        }
    }

    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        List<String> writtenEntries = new ArrayList<String>();
        NBTTagList entryList = new NBTTagList();

        // Save Entries In Use:
        for(PetEntry petEntry : this.allEntries.values()) {
            NBTTagCompound nbtEntry = new NBTTagCompound();
            petEntry.writeToNBT(nbtEntry);
            entryList.appendTag(nbtEntry);
            writtenEntries.add(petEntry.name);
        }

        // Update Saved Entries Not In Use:
        for(Map.Entry<String, NBTTagCompound> entryNBTSet : this.entryNBTs.entrySet()) {
            if(!writtenEntries.contains(entryNBTSet.getKey())) {
                entryList.appendTag(entryNBTSet.getValue());
            }
        }

        nbtTagCompound.setTag("PetManager", entryList);
    }
}
