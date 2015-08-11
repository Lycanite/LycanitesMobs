package lycanite.lycanitesmobs.demonmobs.mobevent;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventBoss;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireWall;
import lycanite.lycanitesmobs.demonmobs.entity.EntityRahovart;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
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
        int height = 120;
        if(world.getHeight() <= height)
            originY = 1;
        else if(originY + height >= world.getHeight())
            originY = Math.max(0, world.getHeight() - height - 1);

        if(time == 1 * 20) {
            this.buildArena(world, originX, originY, originZ);
        }

        if(time == 2 * 20) {
            for(Object playerObject : world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(originX - 20, originY - 20, originZ - 20, originX + 20, originY + 20, originZ + 20))) {
                if(playerObject instanceof EntityPlayer) {
                    EntityPlayer entityPlayer = (EntityPlayer)playerObject;
                    entityPlayer.setLocationAndAngles(originX - 40, originY + 1, originZ, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
                }
            }
        }

        if(time >= 3 * 20 && time % 10 == 0) {
            world.createExplosion(null, originX - 20 + world.rand.nextInt(40), originY + 50 + world.rand.nextInt(20), originZ - 20 + world.rand.nextInt(40), 2, true);
        }

        if(time == 5 * 20) {
            this.buildObstacles(world, originX, originY, originZ);
        }

        if(time == 10 * 20) {
            for(int i = 0; i < 10; i++) {
                EntityProjectileBase entityProjectileBase = new EntityHellfireWall(world, originX, originY + (10 * i), originZ);
                entityProjectileBase.projectileLife = 9 * 20;
                world.spawnEntityInWorld(entityProjectileBase);
            }
        }

        if(time == 19 * 20) {
            EntityCreatureBase entityCreatureBase = new EntityRahovart(world);
            entityCreatureBase.setLocationAndAngles(originX, originY, originZ, 0, 0);
            world.spawnEntityInWorld(entityCreatureBase);
            entityCreatureBase.setHome(originX, originY, originZ, 20);
        }
    }


    public void buildArena(World world, int originX, int originY, int originZ) {
        double rubbleChance = 0.01D;
        int radius = 60;
        int height = 120;
        Block primaryBlock = Blocks.obsidian;

        int stripNumber = 1;
        for(int x = originX - radius; x < originX + radius; x++) {
            float stripNormal = (float)stripNumber / (float)radius;
            if(stripNumber > radius)
                stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
            int stripRadius = Math.round(radius * (float) Math.sin(Math.toRadians(90 * stripNormal)));

            for(int z = originZ - stripRadius; z < originZ + stripRadius; z++) {
                int y = originY;
                world.setBlock(x, y, z, primaryBlock, 0, 2);
                y++;
                if(world.rand.nextDouble() <= rubbleChance) {
                    world.setBlock(x, y, z, primaryBlock, 0, 2);
                    y++;
                }
                while(y <= originY + height && y < world.getHeight()) {
                    world.setBlock(x, y, z, Blocks.air, 0, 2);
                    y++;
                }
            }

            stripNumber++;
        }
    }


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


    public int[] buildPillar(World world, int originX, int originY, int originZ) {
        int radius = 5;
        int height = 40 + Math.round(20 * world.rand.nextFloat());
        Block primaryBlock = Blocks.obsidian;
        int[] decorationCoord = new int[] {originX, originY, originZ};

        int stripNumber = 1;
        for(int y = originY; y <= originY + height; y++) {
            if(y <= originY + radius) {
                for (int x = originX - radius; x <= originX + radius; x++) {
                    float stripNormal = (float)stripNumber / (float)radius;
                    if(stripNumber > radius)
                        stripNormal = (float)(radius - (stripNumber - radius)) / (float)radius;
                    int stripRadius = Math.round(radius * (float) Math.sin(Math.toRadians(90 * stripNormal)));

                    for (int z = originZ - stripRadius; z <= originZ + stripRadius; z++) {
                        world.setBlock(x, y, z, primaryBlock, 0, 2);
                    }

                    stripNumber++;
                }
            }
            else {
                world.setBlock(originX, y, originZ, primaryBlock, 0, 2);
                decorationCoord = new int[] {originX, y, originZ};
            }
        }

        return decorationCoord;
    }


    public void buildDecoration(World world, int originX, int originY, int originZ) {
        Block primaryBlock = Blocks.netherrack;
        Block hazardBlock = ObjectManager.getBlock("hellfire");
        world.setBlock(originX, originY + 1, originZ, primaryBlock, 0, 2);
        world.setBlock(originX, originY + 2, originZ, primaryBlock, 0, 2);
        world.setBlock(originX, originY + 3, originZ, hazardBlock, 0, 2);
        world.setBlock(originX + 1, originY + 1, originZ, primaryBlock, 0, 2);
        world.setBlock(originX + 1, originY + 2, originZ, hazardBlock, 0, 2);
        world.setBlock(originX - 1, originY + 1, originZ, primaryBlock, 0, 2);
        world.setBlock(originX - 1, originY + 2, originZ, hazardBlock, 0, 2);
        world.setBlock(originX, originY + 1, originZ + 1, primaryBlock, 0, 2);
        world.setBlock(originX, originY + 2, originZ + 1, hazardBlock, 0, 2);
        world.setBlock(originX, originY + 1, originZ - 1, primaryBlock, 0, 2);
        world.setBlock(originX, originY + 2, originZ - 1, hazardBlock, 0, 2);
    }
}
