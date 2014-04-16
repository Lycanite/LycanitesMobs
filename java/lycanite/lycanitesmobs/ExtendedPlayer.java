package lycanite.lycanitesmobs;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.info.Beastiary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedPlayer implements IExtendedEntityProperties {
	public static Map<EntityPlayer, ExtendedPlayer> extendedPlayers = new HashMap<EntityPlayer, ExtendedPlayer>();
	public EntityPlayer player;
	public Beastiary beastiary;
	
	// Summoning Focus:
	public int summonFocusCharge = 6000;
	public int summonFocusMax = this.summonFocusCharge * 10;
	public int summonFocus = this.summonFocusMax;
	
	// ==================================================
    //                       Init
    // ==================================================
	@Override
	public void init(Entity entity, World world) {
		if(!(entity instanceof EntityPlayer))
			return;
		this.player = (EntityPlayer)entity;
		this.beastiary = new Beastiary(player);
		
		extendedPlayers.put(player, this);
	}
	
	
	// ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Reads a list of Creature Knowledge from a player's NBTTag. **/
	@Override
    public void loadNBTData(NBTTagCompound nbtTagCompound) {
		NBTTagCompound extTagCompound = nbtTagCompound.getCompoundTag("LycanitesMobs");
		
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
    	
    	nbtTagCompound.setCompoundTag("LycanitesMobs", extTagCompound);
    }
}
