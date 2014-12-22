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
	public PetEntryFamiliar(EntityLivingBase host, String summonType) {
        super("familiar", host, summonType);
	}


    // ==================================================
    //                       On Add
    // ==================================================
    /** Called when this entry is first added. A Pet Manager is passed if added to one, otherwise null. **/
    public void onAdd(PetManager petManager) {
        this.petManager = petManager;
    }


    // ==================================================
    //                    Spawn Entity
    // ==================================================
    /** Called when the entity for this entry is spawned just before it is added to the world. **/
    public void onSpawnEntity(Entity entity) {
        if(this.host instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)this.host;
            if("Lycanite".equals(player.getCommandSenderName())) {
                if (entity instanceof EntityCreatureBase && "grue".equals(this.summonSet.summonType)) {
                    EntityCreatureBase entityCreature = (EntityCreatureBase) entity;
                    entityCreature.setCustomNameTag("Jasper");
                    entityCreature.setSubspecies(2, false);
                }
            }
        }
    }
}
