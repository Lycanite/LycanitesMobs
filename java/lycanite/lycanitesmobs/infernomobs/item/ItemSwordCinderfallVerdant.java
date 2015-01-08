package lycanite.lycanitesmobs.infernomobs.item;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.item.ItemSwordBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCinder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSwordCinderfallVerdant extends ItemSwordCinderfall {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordCinderfallVerdant() {
        super();
    	this.itemName = "verdantcinderfallsword";
        this.setup();
        this.textureName = "swordcinderfallverdant";
    }


    // ==================================================
    //                  Entity Spawning
    // ==================================================
    @Override
    public void onSpawnEntity(Entity entity) {
        super.onSpawnEntity(entity);
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            entityCreature.setSubspecies(2, true);
        }
    }

    @Override
    public float getSpecialEffectChance() { return 0.4F; }


    // ==================================================
    //                     Tool/Weapon
    // ==================================================
    // ========== Get Sword Damage ==========
    @Override
    public float getDamage() {
        return 4F;
    }
}
