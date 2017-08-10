package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArenaNodeNetworkGrid extends ArenaNodeNetwork {

    // ==================================================
    //                    Constructor
    // ==================================================
    public ArenaNodeNetworkGrid(World world, BlockPos centerPos, int arenaNodeX, int arenaNodeY, int arenaNodeZ, int nodeDistance) {
        super(world);

        // Build Arena Node Layout:
        ArenaNode[][][] arenaNodes = new ArenaNode[arenaNodeX][arenaNodeY][arenaNodeZ];
        for(int x = 0; x < arenaNodeX; x++) {
            int posX = x - ((arenaNodeX % 2 == 0 ? arenaNodeX : arenaNodeX - 1) / 2);
            for(int y = 0; y < arenaNodeY; y++) {
                int posY = y - ((arenaNodeY % 2 == 0 ? arenaNodeY : arenaNodeY - 1) / 2);
                for(int z = 0; z < arenaNodeZ; z++) {
                    int posZ = z - ((arenaNodeZ % 2 == 0 ? arenaNodeZ : arenaNodeZ - 1) / 2);
                    arenaNodes[x][y][z] = new ArenaNode(this.world, centerPos.add(posX * nodeDistance, posY * nodeDistance, posZ * nodeDistance));
                    if(posX == 0 && posY == 0 && posZ == 0) {
                        this.centralNode = arenaNodes[x][y][z];
                    }
                }
            }
        }

        // Add Nodes:
        for(int x = 0; x < arenaNodeX; x++) {
            for (int y = 0; y < arenaNodeY; y++) {
                for (int z = 0; z < arenaNodeZ; z++) {
                    ArenaNode node = arenaNodes[x][y][z];
                    if(node == null) {
                        LycanitesMobs.printWarning("", "A null arena node was encountered when adding grid nodes.");
                        continue;
                    }
                    this.nodes.add(node);

                    // Assign Adjacent Nodes:
                    for(int adjX = -1; adjX <= 1; adjX++) {
                        int adjNodeX = x + adjX;
                        if(adjNodeX < 0 || adjNodeX >= arenaNodeX)
                            continue;
                        for (int adjY = -1; adjY <= 1; adjY++) {
                            int adjNodeY = y + adjY;
                            if(adjNodeY < 0 || adjNodeY >= arenaNodeY)
                                continue;
                            for (int adjZ = -1; adjZ <= 1; adjZ++) {
                                int adjNodeZ = z + adjZ;
                                if(adjNodeZ < 0 || adjNodeZ >= arenaNodeZ)
                                    continue;
                                ArenaNode adjacentNode = arenaNodes[adjNodeX][adjNodeY][adjNodeZ];
                                node.addAdjacentNode(adjacentNode);
                            }
                        }
                    }
                }
            }
        }
    }
}
