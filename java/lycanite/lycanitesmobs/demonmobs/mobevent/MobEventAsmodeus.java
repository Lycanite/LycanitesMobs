package lycanite.lycanitesmobs.demonmobs.mobevent;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBoss;
import lycanite.lycanitesmobs.demonmobs.entity.EntityAsmodeus;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobEventAsmodeus extends MobEventBoss {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventAsmodeus(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world) {
        super.onStart(world);
		world.getWorldInfo().setRaining(false);
		world.getWorldInfo().setThundering(false);
    }


    // ==================================================
    //                     Boss Setup
    // ==================================================
    /** This is the main boss setup, this will create the arena, decorate it, move players and finally, summon the boss. The time value is used to determine what to do. **/
    @Override
    public void bossSetup(int time, World world, int originX, int originY, int originZ) {
        originX += 20;
        int height = 120;
        if(originY < 5)
            originY = 5;
        if(world.getHeight() <= height)
            originY = 5;
        else if(originY + height >= world.getHeight())
            originY = Math.max(5, world.getHeight() - height - 1);

        // Build Floor:
        if(time == 1 * 20) {
            this.buildArenaFloor(world, originX, originY, originZ);
        }

        // Explosions:
        if(time >= 3 * 20 && time % 10 == 0) {
            world.createExplosion(null, originX - 20 + world.rand.nextInt(40), originY + 25 + world.rand.nextInt(10), originZ - 20 + world.rand.nextInt(40), 2, true);
        }

        // Build Obstacles:
        if(time == 10 * 20) {
            this.buildObstacles(world, originX, originY, originZ);
        }

        // Build Walls:
        if(time == 20 * 20) {
            this.buildArenaWalls(world, originX, originY, originZ);
        }

        // Hellfire Pillar Effect:
        if(time == 25 * 20) {
            for(int i = 0; i < 5; i++) {
                EntityProjectileBase entityProjectileBase = new EntityHellfireWall(world, originX, originY + (10 * i), originZ);
                entityProjectileBase.projectileLife = 9 * 20;
                world.spawnEntity(entityProjectileBase);
            }
        }

        // Spawn Boss:
        if(time == 29 * 20) {
            EntityCreatureBase entityCreatureBase = new EntityAsmodeus(world);
            entityCreatureBase.setLocationAndAngles(originX, originY + 1, originZ, 0, 0);
            world.spawnEntity(entityCreatureBase);
            entityCreatureBase.setArenaCenter(new BlockPos(originX, originY + 1, originZ));
        }
    }


    // ==================================================
    //                     Arena Floor
    // ==================================================
    public void buildArenaFloor(World world, int originX, int originY, int originZ) {
        int radius = 80;
        int height = 30;
        int minX = originX - radius;
        int maxX = originX + radius;
        int minY = originY;
        int maxY = originY + height;
        int minZ = originZ - radius;
        int maxZ = originZ + radius;
        IBlockState floor = ObjectManager.getBlock("demonstonetile").getDefaultState();
        IBlockState light = ObjectManager.getBlock("demoncrystal").getDefaultState();
        IBlockState trimming = ObjectManager.getBlock("demonstonechiseled").getDefaultState();

        for(int x = minX; x <= maxX; x++) {
            for(int z = minZ; z <= maxZ; z++) {
                int topY = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
                for(int y = minY; y <= maxY; y++) {
                    BlockPos buildPos = new BlockPos(x, y, z);
                    if(y == minY) {
                        if(x == minX || x == maxX || z == minZ || z == maxZ)
                            world.setBlockState(buildPos, trimming, 2);
                        else if(x % 6 == 0 && z % 6 == 0)
                            world.setBlockState(buildPos, light, 2);
                        else
                            world.setBlockState(buildPos, floor, 2);
                    }
                    else {
                        if (y > minY + 3 && y >= topY)
                            break;
                        world.setBlockToAir(buildPos);
                    }
                }
            }
        }
    }


    // ==================================================
    //                     Arena Walls
    // ==================================================
    public void buildArenaWalls(World world, int originX, int originY, int originZ) {
        int radius = 80;
        int thickness = 4;
        int height = 20;
        int minX = originX - (radius + thickness);
        int maxX = originX + (radius + thickness);
        int minY = originY;
        int maxY = originY + height;
        int minZ = originZ - (radius + thickness);
        int maxZ = originZ + (radius + thickness);
        IBlockState base = ObjectManager.getBlock("demonstonebrick").getDefaultState();
        IBlockState light = ObjectManager.getBlock("demoncrystal").getDefaultState();
        IBlockState trimming = ObjectManager.getBlock("demonstonechiseled").getDefaultState();
        IBlockState top = ObjectManager.getBlock("demonstonepolished").getDefaultState();
        IBlockState fireBase = Blocks.NETHERRACK.getDefaultState();
        IBlockState fire = ObjectManager.getBlock("hellfire").getDefaultState();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if(x > minX + thickness && x < maxX - thickness && z  > minZ + thickness && z < maxZ - thickness)
                    continue;
                for (int y = minY; y <= maxY; y++) {
                    BlockPos buildPos = new BlockPos(x, y, z);
                    if(y < maxY - 2) {
                        if (y - 1 % 8 == 0)
                            world.setBlockState(buildPos, light, 2);
                        else if (y % 8 == 0)
                            world.setBlockState(buildPos, trimming, 2);
                        else
                            world.setBlockState(buildPos, base, 2);
                    }
                    else if(y == maxY - 2)
                        world.setBlockState(buildPos, top, 2);
                    else if(y == maxY - 1)
                        world.setBlockState(buildPos, fireBase, 2);
                    else
                        world.setBlockState(buildPos, fire, 2);
                }
            }
        }
    }


    // ==================================================
    //                   Arena Obstacles
    // ==================================================
    public void buildObstacles(World world, int originX, int originY, int originZ) {
        int gap = 20;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if(x >= -1 && x <= 1 && z >= -1 && z <= 1)
                    continue;
                this.buildPillar(world, originX + (x * gap), originY, originZ + (z * gap));
            }
        }
    }

    /** Builds an actual pillar. **/
    public void buildPillar(World world, int originX, int originY, int originZ) {
        int radius = 2;
        int height = 30;
        int minX = originX - radius;
        int maxX = originX + radius;
        int minY = originY;
        int maxY = originY + height;
        int minZ = originZ - radius;
        int maxZ = originZ + radius;
        IBlockState base = ObjectManager.getBlock("demonstonepillar").getDefaultState();
        IBlockState light = ObjectManager.getBlock("demoncrystal").getDefaultState();
        IBlockState trimming = ObjectManager.getBlock("demonstonechiseled").getDefaultState();
        IBlockState top = ObjectManager.getBlock("demonstonepolished").getDefaultState();
        IBlockState fireBase = Blocks.NETHERRACK.getDefaultState();
        IBlockState fire = ObjectManager.getBlock("hellfire").getDefaultState();

        for(int x = minX; x <= maxX; x++) {
            for(int z = minZ; z <= maxZ; z++) {
                for(int y = minY; y <= maxY; y++) {
                    BlockPos buildPos = new BlockPos(x, y, z);
                    if(y < maxY - 2) {
                        if (y % 7 == 0)
                            world.setBlockState(buildPos, light, 2);
                        else if (y % 8 == 0)
                            world.setBlockState(buildPos, trimming, 2);
                        else
                            world.setBlockState(buildPos, base, 2);
                    }
                    else if(y == maxY - 2)
                        world.setBlockState(buildPos, top, 2);
                    else if(y == maxY - 1)
                        world.setBlockState(buildPos, fireBase, 2);
                    else
                        world.setBlockState(buildPos, fire, 2);
                }
            }
        }
    }
}
