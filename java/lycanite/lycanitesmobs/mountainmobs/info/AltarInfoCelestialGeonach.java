package lycanite.lycanitesmobs.mountainmobs.info;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.AltarInfo;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityGeonach;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class AltarInfoCelestialGeonach extends AltarInfo {

    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfoCelestialGeonach(String name) {
        super(name);
    }


    // ==================================================
    //                     Checking
    // ==================================================
    /** Called first when checking for a valid altar, this should be fairly lightweight such as just checking if the first block checked is valid, a more in depth check if then done after. **/
    public boolean quickCheck(Entity entity, World world, int x, int y, int z) {
        if(world.getBlock(x, y, z) != Blocks.diamond_block)
            return false;
        return true;
    }

    /** Called if the QuickCheck() is passed, this should check the entire altar structure and if true is returned, the altar will activate. **/
    public boolean fullCheck(Entity entity, World world, int x, int y, int z) {
        if(!this.quickCheck(entity, world, x, y, z))
            return false;

        Block bodyBlock = Blocks.obsidian;

        // Upper:
        if(world.getBlock(x, y + 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 2, z) != bodyBlock)
            return false;

        // Lower:
        if(world.getBlock(x, y - 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 2, z) != bodyBlock)
            return false;

        // X Rotation:
        if(this.checkRotationX(bodyBlock, entity, world, x, y, z))
            return true;

        // Z Rotation:
        return this.checkRotationZ(bodyBlock, entity, world, x, y, z);
    }


    private boolean checkRotationX(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
        // Left Arm:
        if(world.getBlock(x - 1, y + 2, z) != bodyBlock)
            return false;
        if(world.getBlock(x - 1, y + 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x - 2, y + 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x - 1, y, z) != bodyBlock)
            return false;
        if(world.getBlock(x - 2, y, z) != bodyBlock)
            return false;

        // Right Arm:
        if(world.getBlock(x + 1, y + 2, z) != bodyBlock)
            return false;
        if(world.getBlock(x + 1, y + 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x + 2, y + 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x + 1, y, z) != bodyBlock)
            return false;
        if(world.getBlock(x + 2, y, z) != bodyBlock)
            return false;

        // Left Leg:
        if(world.getBlock(x - 1, y - 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x - 1, y - 2, z) != bodyBlock)
            return false;
        if(world.getBlock(x - 1, y - 2, z) != bodyBlock)
            return false;

        // Right Leg:
        if(world.getBlock(x + 1, y - 1, z) != bodyBlock)
            return false;
        if(world.getBlock(x + 1, y - 2, z) != bodyBlock)
            return false;
        if(world.getBlock(x + 1, y - 2, z) != bodyBlock)
            return false;

        return true;
    }


    private boolean checkRotationZ(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
        // Left Arm:
        if(world.getBlock(x, y + 2, z - 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 1, z - 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 1, z - 2) != bodyBlock)
            return false;
        if(world.getBlock(x, y, z - 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y, z - 2) != bodyBlock)
            return false;

        // Right Arm:
        if(world.getBlock(x, y + 2, z + 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 1, z + 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y + 1, z + 2) != bodyBlock)
            return false;
        if(world.getBlock(x, y, z + 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y, z + 2) != bodyBlock)
            return false;

        // Left Leg:
        if(world.getBlock(x, y - 1, z - 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 2, z - 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 2, z - 1) != bodyBlock)
            return false;

        // Right Leg:
        if(world.getBlock(x, y - 1, z + 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 2, z + 1) != bodyBlock)
            return false;
        if(world.getBlock(x, y - 2, z + 1) != bodyBlock)
            return false;

        return true;
    }


    // ==================================================
    //                     Activate
    // ==================================================
    /** Called when this Altar should activate. This will typically destroy the Altar and summon a rare mob or activate an event such as a boss event. If false is returned then the activation did not work, this is the place to check for things like dimensions. **/
    public boolean activate(Entity entity, World world, int x, int y, int z) {
        if(world.isRemote)
            return true;

        // Create Mini Boss:
        EntityCreatureBase entityGeonach = new EntityGeonach(world);
        if(checkDimensions && !entityGeonach.isNativeDimension(world))
            return false;

        // Destroy Altar:
        int size = 4;
        for (int xTarget = x - size; xTarget <= x + size; xTarget++) {
            for (int zTarget = z - size; zTarget <= z + size; zTarget++) {
                for (int yTarget = y - size; yTarget <= y + size; yTarget++) {
                    if (y > 0)
                        world.func_147480_a(xTarget, yTarget, zTarget, false);
                }
            }
        }

        // Knockback:
        double angle = Math.toRadians(entity.rotationYaw + 180);
        double xAmount = -Math.sin(angle);
        double zAmount = Math.cos(angle);
        entity.motionX = xAmount * 2 + entity.motionX * 0.2D;
        entity.motionZ = zAmount * 2 + entity.motionZ * 0.2D;
        entity.motionY = 0.5D;

        // Spawn Mini Boss:
        entityGeonach.altarSummoned = true;
        entityGeonach.forceBossHealthBar = true;
        entityGeonach.setSubspecies(3, true);
        entityGeonach.setLocationAndAngles(x, y - 2, z, 0, 0);
        world.spawnEntityInWorld(entityGeonach);
        entityGeonach.destroyArea(x, y, z, 10000, false, 2);

        return true;
    }
}
