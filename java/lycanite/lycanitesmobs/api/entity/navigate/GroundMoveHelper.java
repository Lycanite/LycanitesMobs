package lycanite.lycanitesmobs.api.entity.navigate;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.MathHelper;

public class GroundMoveHelper extends EntityMoveHelper {

    public GroundMoveHelper(EntityLiving entityLiving) {
        super(entityLiving);
    }

    @Override
    public void onUpdateMoveHelper() {
        if (this.field_188491_h == EntityMoveHelper.Action.STRAFE) {
            float f = (float)this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
            float f1 = (float)this.speed * f;
            float f2 = this.field_188489_f;
            float f3 = this.field_188490_g;
            float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);

            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 = f2 * f4;
            f3 = f3 * f4;
            float f5 = MathHelper.sin(this.entity.rotationYaw * 0.017453292F);
            float f6 = MathHelper.cos(this.entity.rotationYaw * 0.017453292F);
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;

            PathNodeType pathNodeType = WalkNodeProcessor.func_186330_a(this.entity.worldObj, MathHelper.floor_double(this.entity.posX + (double) f7), MathHelper.floor_double(this.entity.posY), MathHelper.floor_double(this.entity.posZ + (double) f8));
            if (!this.walkable(pathNodeType)) {
                this.field_188489_f = 1.0F;
                this.field_188490_g = 0.0F;
                f1 = f;
            }

            this.entity.setAIMoveSpeed(f1);
            this.entity.setMoveForward(this.field_188489_f);
            this.entity.setMoveStrafing(this.field_188490_g);
            this.field_188491_h = EntityMoveHelper.Action.WAIT;
        }
        else if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
            this.field_188491_h = EntityMoveHelper.Action.WAIT;
            double d0 = this.posX - this.entity.posX;
            double d1 = this.posZ - this.entity.posZ;
            double d2 = this.posY - this.entity.posY;
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;

            if (d3 < 2.500000277905201E-7D) {
                this.entity.setMoveForward(0.0F);
                return;
            }

            float f9 = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f9, 90.0F);
            this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));

            if (d2 > (double)this.entity.stepHeight && d0 * d0 + d1 * d1 < 1.0D) {
                this.entity.getJumpHelper().setJumping();
            }
        }
        else {
            this.entity.setMoveForward(0.0F);
        }
    }

    // Only used for movement speed?
    public boolean walkable(PathNodeType pathNodeType) {
        if(pathNodeType == PathNodeType.WALKABLE)
            return true;
        if(this.entity instanceof EntityCreatureBase) {
            EntityCreatureBase creature = (EntityCreatureBase)this.entity;
            if(creature.canSwim() && !creature.waterDamage() && pathNodeType == PathNodeType.WATER)
                return true;
            if(creature.canSwim() && (creature.isImmuneToFire() || creature.isLavaCreature) && pathNodeType == PathNodeType.LAVA)
                return true;
        }
        return false;
    }
}
