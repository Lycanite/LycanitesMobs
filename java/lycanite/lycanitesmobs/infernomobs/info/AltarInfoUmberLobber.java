package lycanite.lycanitesmobs.infernomobs.info;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.AltarInfo;
import lycanite.lycanitesmobs.infernomobs.entity.EntityLobber;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarInfoUmberLobber extends AltarInfo {

    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfoUmberLobber(String name) {
        super(name);
    }


    // ==================================================
    //                     Checking
    // ==================================================
    /** Called first when checking for a valid altar, this should be fairly lightweight such as just checking if the first block checked is valid, a more in depth check if then done after. **/
    @Override
    public boolean quickCheck(Entity entity, World world, BlockPos pos) {
        if(world.getBlockState(pos).getBlock() != Blocks.diamond_block)
            return false;
        return true;
    }

    /** Called if the QuickCheck() is passed, this should check the entire altar structure and if true is returned, the altar will activate. **/
    @Override
    public boolean fullCheck(Entity entity, World world, BlockPos pos) {
        if(!this.quickCheck(entity, world, pos))
            return false;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        Block bodyBlock = Blocks.obsidian;

        // Upper:
        if(world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y + 2, z)).getBlock() != bodyBlock)
            return false;

        // Lower:
        if(world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() != bodyBlock)
            return false;

        // X Rotation:
        if(this.checkRotationX(bodyBlock, entity, world, x, y, z))
            return true;

        // Z Rotation:
        return this.checkRotationZ(bodyBlock, entity, world, x, y, z);
    }


    private boolean checkRotationX(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
        // Left Arm:
        if(world.getBlockState(new BlockPos(x - 1, y + 2, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x - 2, y + 2, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x - 2, y + 1, z)).getBlock() != bodyBlock)
            return false;

        // Right Arm:
        if(world.getBlockState(new BlockPos(x + 1, y + 2, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x + 2, y + 2, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x + 2, y + 1, z)).getBlock() != bodyBlock)
            return false;

        // Left Leg:
        if(world.getBlockState(new BlockPos(x - 1, y - 1, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x - 1, y - 2, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x - 2, y - 2, z)).getBlock() != bodyBlock)
            return false;

        // Right Leg:
        if(world.getBlockState(new BlockPos(x + 1, y - 1, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x + 1, y - 2, z)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x + 2, y - 2, z)).getBlock() != bodyBlock)
            return false;

        return true;
    }


    private boolean checkRotationZ(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
        // Left Arm:
        if(world.getBlockState(new BlockPos(x, y + 2, z - 1)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y + 2, z - 2)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y + 1, z - 2)).getBlock() != bodyBlock)
            return false;

        // Right Arm:
        if(world.getBlockState(new BlockPos(x, y + 2, z + 1)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y + 2, z + 2)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y + 1, z + 2)).getBlock() != bodyBlock)
            return false;

        // Left Leg:
        if(world.getBlockState(new BlockPos(x, y - 1, z - 1)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y - 2, z - 1)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y - 2, z - 2)).getBlock() != bodyBlock)
            return false;

        // Right Leg:
        if(world.getBlockState(new BlockPos(x, y - 1, z + 1)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y - 2, z + 1)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(new BlockPos(x, y - 2, z + 2)).getBlock() != bodyBlock)
            return false;

        return true;
    }


    // ==================================================
    //                     Activate
    // ==================================================
    /** Called when this Altar should activate. This will typically destroy the Altar and summon a rare mob or activate an event such as a boss event. If false is returned then the activation did not work, this is the place to check for things like dimensions. **/
    @Override
    public boolean activate(Entity entity, World world, BlockPos pos) {
        if(world.isRemote)
            return true;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // Create Mini Boss:
        EntityCreatureBase entityLobber = new EntityLobber(world);
        if(checkDimensions && !entityLobber.isNativeDimension(world))
            return false;

        // Destroy Altar:
        int size = 4;
        for (int xTarget = x - size; xTarget <= x + size; xTarget++) {
            for (int zTarget = z - size; zTarget <= z + size; zTarget++) {
                for (int yTarget = y - size; yTarget <= y + size; yTarget++) {
                    if (y > 0)
                        world.setBlockToAir(new BlockPos(xTarget, yTarget, zTarget));
                }
            }
        }

        // Offset:
        if(entity != null)
            pos = this.getFacingPosition(pos, 10, entity.rotationYaw);

        // Clear Spawn Area:
        for (int xTarget = x - size; xTarget <= x + size; xTarget++) {
            for (int zTarget = z - size; zTarget <= z + size; zTarget++) {
                for (int yTarget = y - size; yTarget <= y + size; yTarget++) {
                    if (y > 0)
                        world.setBlockToAir(new BlockPos(xTarget, yTarget, zTarget));
                }
            }
        }

        // Spawn Mini Boss:
        entityLobber.altarSummoned = true;
        entityLobber.forceBossHealthBar = true;
        entityLobber.setSubspecies(3, true);
        entityLobber.setLocationAndAngles(x, y - 2, z, 0, 0);
        world.spawnEntityInWorld(entityLobber);
        entityLobber.destroyArea(x, y, z, 10000, false, 2);

        return true;
    }
}
