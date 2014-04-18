package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.info.Beastiary;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	public static String EXT_PROP_NAME = "LycanitesMobsPlayer";
	public static Map<EntityPlayer, ExtendedPlayer> extendedPlayers = new HashMap<EntityPlayer, ExtendedPlayer>();
	public static Map<String, NBTTagCompound> backupNBTTags = new HashMap<String, NBTTagCompound>();
	
	// Player Info and Containers:
	public EntityPlayer player;
	public Beastiary beastiary;
	
	// Summoning:
	public int summonFocusCharge = 600;
	public int summonFocusMax = (this.summonFocusCharge * 10);
	public int summonFocus = this.summonFocusMax;
	public MobInfo summonMobInfo;
	
	// ==================================================
    //                    Constructor
    // ==================================================
	public ExtendedPlayer(EntityPlayer player) {
		if(backupNBTTags.containsKey(player)) {
			this.loadNBTData(ExtendedPlayer.backupNBTTags.get(player.username));
			backupNBTTags.remove(player);
		}
		
		this.player = player;
		this.beastiary = new Beastiary(player);
		
		extendedPlayers.put(player, this);
	}
	
	// ==================================================
    //                       Init
    // ==================================================
	@Override
	public void init(Entity entity, World world) {
		
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
	@Override
    public void loadNBTData(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag(EXT_PROP_NAME);
		
		if(extTagCompound.hasKey("SummonFocus"))
			this.summonFocus = extTagCompound.getInteger("SummonFocus");
    	this.beastiary.readFromNBT(extTagCompound);
    }
    
    // ========== Write ==========
    /** Writes a list of Creature Knowledge to a player's NBTTag. **/
	@Override
    public void saveNBTData(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = new NBTTagCompound();
		
		extTagCompound.setInteger("SummonFocus", this.summonFocus);
    	this.beastiary.writeToNBT(extTagCompound);
    	
    	nbtTagCompound.setCompoundTag(EXT_PROP_NAME, extTagCompound);
    }
}
