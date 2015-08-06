package lycanite.lycanitesmobs.demonmobs.mobevent;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventBoss;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
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
            // TODO: Move players.
        }

        if(time == 5 * 20) {
            this.buildObstacles(world, originX, originY, originZ);
        }

        if(time == 9 * 20) {
            // TODO: Summon Rahovart.
        }
    }


    public void buildArena(World world, int originX, int originY, int originZ) {
        double rubbleChance = 0.01D;
        int radius = 50;
        int height = 120;
        Block primaryBlock = Blocks.obsidian;

        int stripNumber = 1;
        for(int x = originX - radius; x <= originX + radius; x++) {
            float stripNormal = stripNumber / radius;
            int stripRadius = Math.round(radius * stripNormal);
            stripRadius += stripRadius * (world.rand.nextDouble() * 2);
            stripRadius = Math.max(2, Math.min(radius, stripRadius));

            for(int z = originZ - stripRadius; z <= originZ + stripRadius; z++) {
                int y = originY;
                world.setBlock(x, y, z, primaryBlock);
                if(world.rand.nextDouble() <= rubbleChance) {
                    y++;
                    world.setBlock(x, y, z, primaryBlock);
                }
                while(y <= originY + height && y < world.getHeight()) {
                    world.setBlockToAir(x, y, z);
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
            angle += 5 + (10 * world.rand.nextDouble());
            double x = radius * Math.cos(angle) - Math.sin(angle);
            double z = radius * Math.sin(angle) + Math.cos(angle);
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
                    float stripNormal = stripNumber / radius;
                    int stripRadius = Math.round(radius * stripNormal);
                    stripRadius += stripRadius * (world.rand.nextDouble() * 2);
                    stripRadius = Math.max(2, Math.min(radius, stripRadius));

                    for (int z = originZ - stripRadius; z <= originZ + stripRadius; z++) {
                        world.setBlock(x, y, z, primaryBlock);
                    }

                    stripNumber++;
                }
            }
            else {
                world.setBlock(originX, y, originZ, primaryBlock);
                decorationCoord = new int[] {originX, y, originZ};
            }
        }

        return decorationCoord;
    }


    public void buildDecoration(World world, int originX, int originY, int originZ) {
        Block primaryBlock = Blocks.netherrack;
        world.setBlock(originX, originY + 1, originZ, primaryBlock);
        world.setBlock(originX + 1, originY + 1, originZ, primaryBlock);
        world.setBlock(originX - 1, originY + 1, originZ, primaryBlock);
        world.setBlock(originX, originY + 1, originZ + 1, primaryBlock);
        world.setBlock(originX, originY + 1, originZ - 1, primaryBlock);
        world.setBlock(originX, originY + 2, originZ, ObjectManager.getBlock("hellfire"));
    }
}
