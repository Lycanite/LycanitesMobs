package lycanite.lycanitesmobs.api.pets;


import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PetEntryFamiliar extends PetEntry {

    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntryFamiliar(String name, EntityLivingBase host, String summonType) {
        super(name, "familiar", host, summonType);
	}
}
