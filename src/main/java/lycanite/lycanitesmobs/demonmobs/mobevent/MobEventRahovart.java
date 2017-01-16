package lycanite.lycanitesmobs.demonmobs.mobevent;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.mobevent.MobEventBoss;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireWall;
import lycanite.lycanitesmobs.demonmobs.entity.EntityRahovart;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MobEventRahovart extends MobEventBoss {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventRahovart(String name, GroupInfo group) {
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
        int height = 40;
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

        // Hellfire Pillar Effect:
        if(time == 25 * 20) {
            for(int i = 0; i < 5; i++) {
                EntityProjectileBase entityProjectileBase = new EntityHellfireWall(world, originX, originY + (10 * i), originZ);
                entityProjectileBase.projectileLife = 9 * 20;
                world.spawnEntityInWorld(entityProjectileBase);
            }
        }

        // Spawn Boss:
        if(time == 29 * 20) {
            EntityCreatureBase entityCreatureBase = new EntityRahovart(world);
            entityCreatureBase.setLocationAndAngles(originX, originY + 1, originZ, 0, 0);
            world.spawnEntityInWorld(entityCreatureBase);
            entityCreatureBase.setArenaCenter(new BlockPos(originX, originY + 1, originZ));
        }
    }


    // ==================================================
    //                     Arena Floor
    // ==================================================
    public void buildArenaFloor(World world, int originX, int originY, int originZ) {
        double rubbleChance = 0.01D;
        int radius = 60;
        int height = 40;
        Block primaryBlock = ObjectManager.getBlock("demonstonetile");
        Block secondaryBlock = ObjectManager.getBlock("demoncrystal");
        double secondaryChance = 0.05D;

        int stripNumber = 1;
        for(int x = originX - radius; x < originX + radius; x++) {
            float stripNormal = (float)stripNumber / (float)radius;
            if(stripNumber > radius)
                stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
            int stripRadius = Math.round(radius * (float) Math.sin(Math.toRadians(90 * stripNormal)));

            for(int z = originZ - stripRadius; z < originZ + stripRadius; z++) {
                int y = originY;
                // Build Floor:
                Block buildBlock = primaryBlock;
                if(world.rand.nextDouble() <= secondaryChance)
                    buildBlock = secondaryBlock;
                world.setBlockState(new BlockPos(x, y, z), buildBlock.getDefaultState(), 2);
                world.setBlockState(new BlockPos(x, y - 1, z), buildBlock.getDefaultState(), 2);
                world.setBlockState(new BlockPos(x, y - 2, z), buildBlock.getDefaultState(), 2);
                y++;
                while(y <= originY + height && y < world.getHeight()) {
                    world.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 2);
                    y++;
                }
            }

            stripNumber++;
        }
    }


    // ==================================================
    //                   Arena Obstacles
    // ==================================================
    public void buildObstacles(World world, int originX, int originY, int originZ) {
        double angle = 0;
        int radius = 50;
        List<int[]> decorationCoords = new ArrayList<int[]>();

        while(angle < 360) {
            angle += 5 + (5 * world.rand.nextDouble());
            double angleRadians = Math.toRadians(angle);
            double x = radius * Math.cos(angleRadians) - Math.sin(angleRadians);
            double z = radius * Math.sin(angleRadians) + Math.cos(angleRadians);
            decorationCoords.add(this.buildPillar(world, originX + (int) Math.ceil(x), originY, originZ + (int) Math.ceil(z)));
        }

        for(int[] decorationCoord : decorationCoords)
            this.buildDecoration(world, decorationCoord[0], decorationCoord[1], decorationCoord[2]);
    }

    /** Builds an actual pillar. **/
    public int[] buildPillar(World world, int originX, int originY, int originZ) {
        int radiusMax = 5;
        int height = 20 + Math.round(20 * world.rand.nextFloat());
        Block primaryBlock = ObjectManager.getBlock("demonstonebrick");
        Block secondaryBlock = ObjectManager.getBlock("demonstone");
        Block tetriaryBlock = ObjectManager.getBlock("demonstonechiseled");
        Block pillarBlock = ObjectManager.getBlock("demonstonepillar");
        double secondaryChance = 0.4D;
        double tetriaryChance = 0.05D;
        int[] decorationCoord = new int[] {originX, originY, originZ};

        int radius = radiusMax;
        int radiusHeight = radiusMax;
        for(int y = originY; y <= originY + height; y++) {
            if(y <= originY + (radiusMax * radiusMax)) {
                int stripNumber = 1;
                for (int x = originX - radius; x <= originX + radius; x++) {
                    float stripNormal = (float)stripNumber / (float)radius;
                    if(stripNumber > radius)
                        stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
                    int stripRadius = Math.round(radius * (float) Math.sin(Math.toRadians(90 * stripNormal)));

                    for (int z = originZ - stripRadius; z <= originZ + stripRadius; z++) {
                        if(x == originX && z == originZ) {
                            world.setBlockState(new BlockPos(x, y, z), pillarBlock.getDefaultState(), 2);
                        }
                        else {
                            if (world.rand.nextDouble() > secondaryChance)
                                world.setBlockState(new BlockPos(x, y, z), primaryBlock.getDefaultState(), 2);
                            else if (world.rand.nextDouble() > tetriaryChance)
                                world.setBlockState(new BlockPos(x, y, z), secondaryBlock.getDefaultState(), 2);
                            else
                                world.setBlockState(new BlockPos(x, y, z), tetriaryBlock.getDefaultState(), 2);
                        }
                    }

                    stripNumber++;
                }
            }
            else {
                world.setBlockState(new BlockPos(originX, y, originZ), pillarBlock.getDefaultState(), 2);
                decorationCoord = new int[] {originX, y, originZ};
            }
            if(--radiusHeight <= 0) {
                radiusHeight = radiusMax;
                radius--;
            }
        }

        return decorationCoord;
    }

    /** Adds decoration to a pillar. **/
    public void buildDecoration(World world, int originX, int originY, int originZ) {
        Block primaryBlock = Blocks.NETHERRACK;
        Block hazardBlock = ObjectManager.getBlock("hellfire");
        world.setBlockState(new BlockPos(originX, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX, originY + 2, originZ), primaryBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX, originY + 3, originZ), hazardBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX + 1, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX + 1, originY + 2, originZ), hazardBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX - 1, originY + 1, originZ), primaryBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX - 1, originY + 2, originZ), hazardBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX, originY + 1, originZ + 1), primaryBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX, originY + 2, originZ + 1), hazardBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX, originY + 1, originZ - 1), primaryBlock.getDefaultState(), 2);
        world.setBlockState(new BlockPos(originX, originY + 2, originZ - 1), hazardBlock.getDefaultState(), 2);
    }
}
