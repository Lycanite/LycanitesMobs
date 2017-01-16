package lycanite.lycanitesmobs.core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;

public class MinionEntityDamageSource extends EntityDamageSource {
    EntityDamageSource minionDamageSource;
    private Entity minionOwner;
	
    // ==================================================
  	//                     Constructor
  	// ==================================================
	public MinionEntityDamageSource(EntityDamageSource minionDamageSource, Entity owner) {
		super(minionDamageSource.damageType, minionDamageSource.getSourceOfDamage());
        this.minionDamageSource = minionDamageSource;
        this.minionOwner = owner;
	}


    // ==================================================
    //                     Get Entity
    // ==================================================
    // This Entity Caused The Damage:
    @Override
    public Entity getSourceOfDamage() {
        return this.damageSourceEntity;
    }

    // This Entity Gets Credit for The Kill:
    @Override
    public Entity getEntity() {
        return this.minionOwner;
    }

    // ==================================================
    //                    Chat Message
    // ==================================================
    @Override
    public ITextComponent getDeathMessage(EntityLivingBase slainEntity) {
        return this.minionDamageSource.getDeathMessage(slainEntity);
        /*String minionName = this.getSourceOfDamage().getCommandSenderName();
        String ownerName = this.minionOwner.getCommandSenderName();
        String ownerSuffix = "'s ";
        if("s".equals(ownerName.substring(ownerName.length() - 1)) || "S".equals(ownerName.substring(ownerName.length() - 1)))
            ownerSuffix = "' ";
        String ownedName = ownerName + ownerSuffix + minionName;
        IChatComponent minionTitle = new ChatComponentText(ownedName);

        ItemStack itemstack = this.damageSourceEntity instanceof EntityLivingBase ? ((EntityLivingBase)this.damageSourceEntity).getHeldItem() : null;
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        return itemstack != null && itemstack.hasDisplayName() && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, new Object[] {slainEntity.func_145748_c_(), minionTitle, itemstack.func_151000_E()}): new ChatComponentTranslation(s, new Object[] {slainEntity.func_145748_c_(), minionTitle});*/
    }
}
