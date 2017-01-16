package lycanite.lycanitesmobs.core.container;

import lycanite.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerBase extends Container {
    public TileEntitySummoningPedestal summoningPedestal;

    // ========================================
    //                Constructor
    // ========================================
    public ContainerBase(TileEntitySummoningPedestal summoningPedestal) {
        super();
        this.summoningPedestal = summoningPedestal;
    }


    // ========================================
    //                  Interact
    // ========================================
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if(this.summoningPedestal == null)
            return false;
        return player == this.summoningPedestal.getPlayer();
    }
}
