package lycanite.lycanitesmobs.api.entity.navigate;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class FlightMoveHelper extends EntityMoveHelper {
    private EntityCreatureBase entityCreature;
    private int courseChangeCooldown;

    public FlightMoveHelper(EntityCreatureBase entityCreatureBase) {
        super(entityCreatureBase);
        this.entityCreature = entityCreatureBase;
    }

    /**
     * Called on the update to apply movement.
     * field_188491_h = Current Move Action
     */
    public void onUpdateMoveHelper() {
        if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
            double xDistance = this.posX - this.entityCreature.posX;
            double yDistance = this.posY - this.entityCreature.posY;
            double zDistance = this.posZ - this.entityCreature.posZ;
            double distance = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.entityCreature.getRNG().nextInt(5) + 2;
                distance = (double)MathHelper.sqrt_double(distance);
                if(distance >= 1D) {
                    this.entityCreature.setAIMoveSpeed((float)this.entityCreature.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                    double speed = (this.entityCreature.getAIMoveSpeed() / 2.4D) * this.getSpeed();
                    this.entityCreature.motionX += xDistance / distance * speed;
                    this.entityCreature.motionY += yDistance / distance * speed;
                    this.entityCreature.motionZ += zDistance / distance * speed;
                }
                else {
                    this.field_188491_h = EntityMoveHelper.Action.WAIT;
                }
            }
        }

        // Look At Target or Movement Direction:
        if (this.entityCreature.getAttackTarget() != null) {
            EntityLivingBase entitylivingbase = this.entityCreature.getAttackTarget();
            double distanceX = entitylivingbase.posX - this.entityCreature.posX;
            double distanceY = entitylivingbase.posZ - this.entityCreature.posZ;
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceY)) * (180F / (float)Math.PI);
        }
        else if(this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
            this.entityCreature.renderYawOffset = this.entityCreature.rotationYaw = -((float)MathHelper.atan2(this.entityCreature.motionX, this.entityCreature.motionZ)) * (180F / (float)Math.PI);
        }
    }

    /**
     * Checks if entity bounding box is not colliding with terrain.
     */
    private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
        double d0 = (x - this.entityCreature.posX) / p_179926_7_;
        double d1 = (y - this.entityCreature.posY) / p_179926_7_;
        double d2 = (z - this.entityCreature.posZ) / p_179926_7_;
        AxisAlignedBB axisalignedbb = this.entityCreature.getEntityBoundingBox();

        for (int i = 1; (double)i < p_179926_7_; ++i) {
            axisalignedbb = axisalignedbb.offset(d0, d1, d2);

            if (!this.entityCreature.worldObj.getCubes(this.entityCreature, axisalignedbb).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
