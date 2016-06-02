package lycanite.lycanitesmobs.api.entity.navigate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ArenaNodeNetwork {
    public World world;

    /** A list of all nodes in this network. **/
    public List<ArenaNode> nodes = new ArrayList<ArenaNode>();

    /** The central or starting point of the arena. **/
    public ArenaNode centralNode;

    // ==================================================
    //                    Constructor
    // ==================================================
    public ArenaNodeNetwork(World world) {
        this.world = world;
    }


    // ==================================================
    //                      Nodes
    // ==================================================
    public void addNode(ArenaNode node, ArenaNode... adjacentNodes) {
        if(node == null || this.nodes.contains(node))
            return;
        this.addNode(node);
        for(ArenaNode adjacentNode : adjacentNodes)
            node.addAdjacentNode(adjacentNode);
    }

    public void addNode(ArenaNode node) {
        if(node == null || this.nodes.contains(node))
            return;
        this.nodes.add(node);
    }


    // ==================================================
    //                   Navigation
    // ==================================================
    public ArenaNode getClosestNode(BlockPos targetPos) {
        int nodesSize = this.nodes.size();
        if(nodesSize <= 0)
            return null;
        if(nodesSize == 1)
            return this.nodes.get(0);
        double smallestDistance = 0;
        ArenaNode closestNode = null;
        for(ArenaNode node : this.nodes) {
            double distance = targetPos.distanceSq(node.pos);
            if(closestNode == null || distance < smallestDistance) {
                smallestDistance = distance;
                closestNode = node;
            }
        }
        return closestNode;
    }
}
