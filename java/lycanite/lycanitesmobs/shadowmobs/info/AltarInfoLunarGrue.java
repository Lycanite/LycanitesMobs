package lycanite.lycanitesmobs.shadowmobs.info;

import lycanite.lycanitesmobs.api.info.AltarInfo;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class AltarInfoLunarGrue extends AltarInfo {

    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfoLunarGrue(String name) {
        super(name);
    }


    // ==================================================
    //                     Checking
    // ==================================================
    /** Called first when checking for a valid altar, this should be fairly lightweight such as just checking if the first block checked is valid, a more in depth check if then done after. **/
    public boolean quickCheck(Entity entity, World world, int x, int y, int z) {
        return false;
    }

    /** Called if the QuickCheck() is passed, this should check the entire altar structure and if true is returned, the altar will activate. **/
    public boolean fullCheck(Entity entity, World world, int x, int y, int z) {
        return false;
    }


    // ==================================================
    //                     Activate
    // ==================================================
    /** Called when this Altar should activate. This will typically destroy the Altar and summon a rare mob or activate an event such as a boss event. **/
    public void activate(Entity entity, World world, int x, int y, int z) {

    }
}
