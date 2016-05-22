package lycanite.lycanitesmobs.demonmobs.info;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.AltarInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarInfoRahovart extends AltarInfo {

    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfoRahovart(String name) {
        super(name);
    }


    // ==================================================
    //                     Checking
    // ==================================================
    /** Called first when checking for a valid altar, this should be fairly lightweight such as just checking if the first block checked is valid, a more in depth check if then done after. **/
    @Override
    public boolean quickCheck(Entity entity, World world, BlockPos pos) {
        if(world.getBlockState(pos).getBlock() != ObjectManager.getBlock("soulcubedemonic"))
            return false;
        return true;
    }

    /** Called if the QuickCheck() is passed, this should check the entire altar structure and if true is returned, the altar will activate. **/
    @Override
    public boolean fullCheck(Entity entity, World world, BlockPos pos) {
        if(!this.quickCheck(entity, world, pos))
            return false;

        Block bodyBlock = Blocks.OBSIDIAN;

        // Middle:
        if(world.getBlockState(pos.add(0, -2, 0)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(pos.add(0, -1, 0)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(pos.add(0, 1, 0)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(pos.add(0, 2, 0)).getBlock() != bodyBlock)
            return false;

        // Corners:
        if(!this.checkPillar(bodyBlock, entity, world, pos.add(-2, 0, -2)))
            return false;
        if(!this.checkPillar(bodyBlock, entity, world, pos.add(2, 0, -2)))
            return false;
        if(!this.checkPillar(bodyBlock, entity, world, pos.add(-2, 0, 2)))
            return false;
        if(!this.checkPillar(bodyBlock, entity, world,pos.add(2, 0, 2)))
            return false;

        return true;
    }

    public boolean checkPillar(Block bodyBlock, Entity entity, World world, BlockPos pos) {
        if(world.getBlockState(pos.add(0, -2, 0)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(pos.add(0, -1, 0)).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(pos).getBlock() != bodyBlock)
            return false;
        if(world.getBlockState(pos.add(0, 1, 0)).getBlock() != Blocks.DIAMOND_BLOCK)
            return false;
        return true;
    }


    // ==================================================
    //                     Activate
    // ==================================================
    /** Called when this Altar should activate. This will typically destroy the Altar and summon a rare mob or activate an event such as a boss event. If false is returned then the activation did not work, this is the place to check for things like dimensions. **/
    @Override
    public boolean activate(Entity entity, World world, BlockPos pos) {
        if (world.isRemote)
            return true;

        ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
        if(worldExt == null)
            return false;

        // Offset:
        pos = new BlockPos(pos.getX(), Math.max(1, pos.getY() - 3), pos.getZ());
        if(entity != null)
            pos = this.getFacingPosition(pos, 10, entity.rotationYaw);

        worldExt.startMobEvent("rahovart", pos.getX(), pos.getY(), pos.getZ());

        return true;
    }
}
