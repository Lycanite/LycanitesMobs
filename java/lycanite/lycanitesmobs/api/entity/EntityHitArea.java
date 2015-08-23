package lycanite.lycanitesmobs.api.entity;

import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


public class EntityHitArea extends Entity {
    public Entity owner;
    private static byte widthID = 12;
    private static byte heightID = 13;

    public EntityHitArea(Entity ownerEntity, float width, float height) {
        super(ownerEntity.worldObj);
        this.owner = ownerEntity;
        this.setSize(width, height);
    }

    public EntityHitArea(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(this.widthID, this.width);
        this.dataWatcher.addObject(this.heightID, this.height);
    }


    @Override
    public void onUpdate() {
        if((this.owner == null || this.owner.isDead) && !this.worldObj.isRemote)
            this.setDead();
        super.onUpdate();
        if(!this.worldObj.isRemote) {
            this.dataWatcher.updateObject(this.widthID, this.width);
            this.dataWatcher.updateObject(this.heightID, this.height);
        }
        else {
            float newWidth = this.dataWatcher.getWatchableObjectFloat(this.widthID);
            float newHeight = this.dataWatcher.getWatchableObjectFloat(this.heightID);
            if(this.width != newWidth || this.height != newHeight)
                this.setSize(newWidth, newHeight);
        }
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}


    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}


    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }


    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
        if(this.worldObj.isRemote)
            return true;
        if(this.isEntityInvulnerable())
            return false;
        if(this.owner == null)
            return true;
        if(this.owner instanceof EntityCreatureBase)
            return ((EntityCreatureBase)this.owner).attackEntityFromArea(this, damageSource, damageAmount);
        return this.owner.attackEntityFrom(damageSource, damageAmount);
    }

    @Override
    public boolean isEntityEqual(Entity entity) {
        return this == entity || this.owner == entity;
    }
}
