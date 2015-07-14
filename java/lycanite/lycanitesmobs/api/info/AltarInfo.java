package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class AltarInfo {
    // ========== Global Settings ==========
    public static boolean altarsEnabled = true;

    /** A static map containing all the global multipliers for each stat applied to rare subspecies spawned via Altars. **/
    public static Map<String, Double> rareSubspeciesMutlipliers = new HashMap<String, Double>();

    /** A list of all Altars, typically cycled through when a Soulkey is used. **/
    public static Map<String, AltarInfo> altars = new HashMap<String, AltarInfo>();


    // ========== Properties ==========
    public String name = "Altar";


    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
    public static void loadGlobalSettings() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "general");
        config.setCategoryComment("Altars", "Altars are block arrangements that can be activated using Soulkeys to summon rare subspecies as mini bosses or trigger events including boss events.");
        altarsEnabled = config.getBool("Altars", "Altars Enabled", altarsEnabled, "Set to false to disable altars, Soulkeys can still be crafted but wont work on Altars.");


        String[] statNames = new String[] {"Health", "Defense", "Speed", "Damage", "Haste", "Effect", "Pierce"};
        rareSubspeciesMutlipliers = new HashMap<String, Double>();
        for(String statName : statNames) {
            double defaultValue = 10.0D;
            if("Speed".equalsIgnoreCase(statName))
                defaultValue = 1.5D;
            rareSubspeciesMutlipliers.put(statName.toUpperCase(), config.getDouble("Altars", statName + " Altar Stat Multiplier", defaultValue));
        }
    }


    // ==================================================
    //                    Enabled
    // ==================================================
    public static boolean checkAltarsEnabled() {
        if(altars.size() < 1)
            return false;
        return altarsEnabled;
    }


    // ==================================================
    //                    Altar List
    // ==================================================
    public static void addAltar(AltarInfo altarInfo) {
        altars.put(altarInfo.name, altarInfo);
    }

    public static AltarInfo getAltar(String name) {
        if(altars.containsKey(name))
            return altars.get(name);
        return null;
    }


    // ==================================================
    //                    Constructor
    // ==================================================
    public AltarInfo(String name) {
        this.name = name;
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
