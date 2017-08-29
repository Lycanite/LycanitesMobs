package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class CreatureMoveHelper extends EntityMoveHelper {

    protected EntityCreatureBase entityCreature;
    /** Used by flight movement for changing course, makes for smoother movement. **/
    protected int courseChangeCooldown;

    public CreatureMoveHelper(EntityCreatureBase entityCreatureBase) {
        super(entityCreatureBase);
        this.entityCreature = entityCreatureBase;
    }

    /** Called on update to move the entity. **/
    @Override
    public void onUpdateMoveHelper() {
        // Rider:
        if(this.isControlledByRider()) {
            return;
        }

        // Swimming:
        if(this.entityCreature.isStrongSwimmer() && this.entityCreature.isInWater()) {
            this.onUpdateSwimming();
            return;
        }

        // Flying:
        if(this.entityCreature.isFlying() && !this.entityCreature.isInWater()) {
            this.onUpdateFlying();
            return;
        }

        // Walking:
        super.onUpdateMoveHelper();
    }


    // ==================== Checks ====================
    /** Returns true if the entity is controlled by its rider. **/
    public boolean isControlledByRider() {
        // Mounted By Player:
        if(this.entityCreature != null && this.entityCreature.getControllingPassenger() instanceof EntityPlayer && this.entityCreature.canBeSteered()) {

            // Strong Swimmings Can Always Be Controlled:
            if(this.entityCreature.isStrongSwimmer()) {
                return true;
            }

            // Flyers Can't Be Controlled In Water (Unless Strong Swimmer):
            if(this.entityCreature.isFlying() && this.entityCreature.isInWater()) {
                return false;
            }

            // Ground Mounts Can Always Be Controlled:
            return true;
        }

        return false;
    }


    // ==================== Movements ====================
    /** Used by strong swimmers for fast, smooth movement. **/
    public void onUpdateSwimming() {
        if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.entityCreature.getNavigator().noPath()) {
            double x = this.posX - this.entityCreature.posX;
            double y = this.posY - this.entityCreature.posY;
            double z = this.posZ - this.entityCreature.posZ;
            double distance = x * x + y * y + z * z;
            distance = (double) MathHelper.sqrt(distance);
            y = y / distance;
            float f = (float)(MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
            this.entityCreature.rotationYaw = this.limitAngle(this.entityCreature.rotationYaw, f, 90.0F);
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw;
            float f1 = (float)(this.speed * this.entityCreature.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            this.entityCreature.setAIMoveSpeed(this.entityCreature.getAIMoveSpeed() + (f1 - this.entityCreature.getAIMoveSpeed()) * 0.125F);
            double d4 = Math.sin((double)(this.entityCreature.ticksExisted + this.entityCreature.getEntityId()) * 0.5D) * 0.05D;
            double d5 = Math.cos((double)(this.entityCreature.rotationYaw * 0.017453292F));
            double d6 = Math.sin((double)(this.entityCreature.rotationYaw * 0.017453292F));
            this.entityCreature.motionX += d4 * d5;
            this.entityCreature.motionZ += d4 * d6;
            d4 = Math.sin((double)(this.entityCreature.ticksExisted + this.entityCreature.getEntityId()) * 0.75D) * 0.05D;
            this.entityCreature.motionY += d4 * (d6 + d5) * 0.25D;
            this.entityCreature.motionY += (double)this.entityCreature.getAIMoveSpeed() * y * 0.1D;
            EntityLookHelper entitylookhelper = this.entityCreature.getLookHelper();
            double d7 = this.entityCreature.posX + x / distance * 2.0D;
            double d8 = (double)this.entityCreature.getEyeHeight() + this.entityCreature.posY + y / distance;
            double d9 = this.entityCreature.posZ + z / distance * 2.0D;
            double d10 = entitylookhelper.getLookPosX();
            double d11 = entitylookhelper.getLookPosY();
            double d12 = entitylookhelper.getLookPosZ();

            if (!entitylookhelper.getIsLooking()) {
                d10 = d7;
                d11 = d8;
                d12 = d9;
            }

            this.entityCreature.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
        }
        else {
            this.entityCreature.setAIMoveSpeed(0.0F);
        }
    }

    /** Used by flyers for swift, fast air movement. **/
    public void onUpdateFlying() {
        if (this.action == EntityMoveHelper.Action.MOVE_TO) {
            double xDistance = this.posX - this.entityCreature.posX;
            double yDistance = this.posY - this.entityCreature.posY;
            double zDistance = this.posZ - this.entityCreature.posZ;
            double distance = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.entityCreature.getRNG().nextInt(5) + 2;
                distance = (double)MathHelper.sqrt(distance);
                if(distance >= 1D) {
                    this.entityCreature.setAIMoveSpeed((float)this.entityCreature.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                    double speed = (this.entityCreature.getAIMoveSpeed() / 2.4D) * this.getSpeed();
                    this.entityCreature.motionX += xDistance / distance * speed;
                    this.entityCreature.motionY += yDistance / distance * speed;
                    this.entityCreature.motionZ += zDistance / distance * speed;
                }
                else {
                    this.action = EntityMoveHelper.Action.WAIT;
                }
            }
        }

        // Look At Target or Movement Direction:
        if (this.entityCreature.getAttackTarget() != null) {
            EntityLivingBase entitylivingbase = this.entityCreature.getAttackTarget();
            double distanceX = entitylivingbase.posX - this.entityCreature.posX;
            double distanceZ = entitylivingbase.posZ - this.entityCreature.posZ;
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);
        }
        else if(this.action == EntityMoveHelper.Action.MOVE_TO) {
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(this.entityCreature.motionX, this.entityCreature.motionZ)) * (180F / (float)Math.PI);
        }
    }
}
