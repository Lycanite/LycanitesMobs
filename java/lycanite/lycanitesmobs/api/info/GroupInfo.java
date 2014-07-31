package lycanite.lycanitesmobs.api.info;


import lycanite.lycanitesmobs.api.ILycaniteMod;

public class GroupInfo {

    // ========== Group General ==========
    /** The name of this group, normally displayed in the config. **/
    public String name;

    /** The filename of this group, used for assets, config, etc. This should usually match the sub-mod ID. **/
    public String filename;


    // ==================================================
    //                     Constructor
    // ==================================================
    public GroupInfo(String name) {
        this.name = name;
        this.filename = name.toLowerCase().replace(" ", "");
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    public void loadFromConfig() {

    }
}
