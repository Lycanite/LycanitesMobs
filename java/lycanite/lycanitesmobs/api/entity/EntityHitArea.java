package lycanite.lycanitesmobs.api.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;


public class EntityHitArea extends Entity {
    public Entity owner;

    // Datawatcher:
    protected static final DataParameter<Float> WIDTH = EntityDataManager.<Float>createKey(EntityHitArea.class, DataSerializers.FLOAT);
    protected static final DataParameter<Float> HEIGHT = EntityDataManager.<Float>createKey(EntityHitArea.class, DataSerializers.FLOAT);

    public EntityHitArea(Entity ownerEntity, float width, float height) {
        super(ownerEntity.worldObj);
        this.owner = ownerEntity;
        this.setSize(width, height);
        this.noClip = true;
    }

    public EntityHitArea(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.register(WIDTH, this.width);
        this.dataWatcher.register(HEIGHT, this.height);
    }


    @Override
    public void onUpdate() {
        if((this.owner == null || this.owner.isDead) && !this.worldObj.isRemote)
            this.setDead();
        super.onUpdate();
        if(!this.worldObj.isRemote) {
            this.dataWatcher.set(WIDTH, this.width);
            this.dataWatcher.set(HEIGHT, this.height);
        }
        else {
            float newWidth = this.dataWatcher.get(WIDTH);
            float newHeight = this.dataWatcher.get(HEIGHT);
            if(this.width != newWidth || this.height != newHeight)
                this.setSize(newWidth, newHeight);
        }
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {}


    @Override
    protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {}


    @Override
    public boolean canBeCollidedWith() {
        return true;
    }


    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
        if(this.worldObj.isRemote)
            return true;
        if(this.isEntityInvulnerable(damageSource))
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

    @Override
    public String getName() {
        if(this.owner != null)
            return this.owner.getName();
        return "Hit Area";
    }
}
