package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class SwimmingMoveHelper extends EntityMoveHelper {
    private EntityCreatureBase entityCreature;

    public SwimmingMoveHelper(EntityCreatureBase parentEntity) {
        super(parentEntity);
        this.entityCreature = parentEntity;
    }

    @Override
    public void onUpdateMoveHelper() {
        if(this.entityCreature != null && this.entityCreature.getControllingPassenger() instanceof EntityPlayer)
            return;
        if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.entityCreature.getNavigator().noPath()) {
            double x = this.posX - this.entityCreature.posX;
            double y = this.posY - this.entityCreature.posY;
            double z = this.posZ - this.entityCreature.posZ;
            double distance = x * x + y * y + z * z;
            distance = (double) MathHelper.sqrt_double(distance);
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
}
