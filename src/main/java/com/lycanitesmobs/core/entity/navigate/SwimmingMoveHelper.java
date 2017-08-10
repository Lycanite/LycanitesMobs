package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

public class SwimmingMoveHelper extends EntityMoveHelper {
    private EntityCreatureBase parentEntity;

    public SwimmingMoveHelper(EntityCreatureBase parentEntity) {
        super(parentEntity);
        this.parentEntity = parentEntity;
    }

    @Override
    public void onUpdateMoveHelper() {
        if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.parentEntity.getNavigator().noPath()) {
            double x = this.posX - this.parentEntity.posX;
            double y = this.posY - this.parentEntity.posY;
            double z = this.posZ - this.parentEntity.posZ;
            double distance = x * x + y * y + z * z;
            distance = (double) MathHelper.sqrt_double(distance);
            y = y / distance;
            float f = (float)(MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
            this.parentEntity.rotationYaw = this.limitAngle(this.parentEntity.rotationYaw, f, 90.0F);
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
            float f1 = (float)(this.speed * this.parentEntity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            this.parentEntity.setAIMoveSpeed(this.parentEntity.getAIMoveSpeed() + (f1 - this.parentEntity.getAIMoveSpeed()) * 0.125F);
            double d4 = Math.sin((double)(this.parentEntity.ticksExisted + this.parentEntity.getEntityId()) * 0.5D) * 0.05D;
            double d5 = Math.cos((double)(this.parentEntity.rotationYaw * 0.017453292F));
            double d6 = Math.sin((double)(this.parentEntity.rotationYaw * 0.017453292F));
            this.parentEntity.motionX += d4 * d5;
            this.parentEntity.motionZ += d4 * d6;
            d4 = Math.sin((double)(this.parentEntity.ticksExisted + this.parentEntity.getEntityId()) * 0.75D) * 0.05D;
            this.parentEntity.motionY += d4 * (d6 + d5) * 0.25D;
            this.parentEntity.motionY += (double)this.parentEntity.getAIMoveSpeed() * y * 0.1D;
            EntityLookHelper entitylookhelper = this.parentEntity.getLookHelper();
            double d7 = this.parentEntity.posX + x / distance * 2.0D;
            double d8 = (double)this.parentEntity.getEyeHeight() + this.parentEntity.posY + y / distance;
            double d9 = this.parentEntity.posZ + z / distance * 2.0D;
            double d10 = entitylookhelper.getLookPosX();
            double d11 = entitylookhelper.getLookPosY();
            double d12 = entitylookhelper.getLookPosZ();

            if (!entitylookhelper.getIsLooking()) {
                d10 = d7;
                d11 = d8;
                d12 = d9;
            }

            this.parentEntity.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
        }
        else {
            this.parentEntity.setAIMoveSpeed(0.0F);
        }
    }
}
