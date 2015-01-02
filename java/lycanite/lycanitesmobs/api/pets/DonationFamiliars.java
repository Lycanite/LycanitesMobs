package lycanite.lycanitesmobs.api.pets;

import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonationFamiliars {
    public static DonationFamiliars instance = new DonationFamiliars();

    // ==================================================
    //              Get Familiars For Player
    // ==================================================
    public List<PetEntry> getFamilairsForPlayer(EntityPlayer player) {
        List<PetEntry> familiars = new ArrayList<PetEntry>();

        if("Lycanite".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("grue") != null)
                familiars.add(new PetEntryFamiliar("LycaniteGrueJasper", player, "grue").setEntityName("Jasper").setEntitySubspeciesID(2).setEntitySize(0.85D));
        }

        if("Kashiaka".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("arix") != null)
                familiars.add(new PetEntryFamiliar("KashiakaArixAlix", player, "arix").setEntityName("Alix").setEntitySubspeciesID(2).setEntitySize(0.85D));
        }

        // Celebs:
        if("kehaan".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("grue") != null)
                familiars.add(new PetEntryFamiliar("kehaanGrueMiniKehaan", player, "grue").setEntityName("Mini Kehaan").setEntitySubspeciesID(1).setEntitySize(0.85D));
        }
        if("Augustus1979".equals(player.getCommandSenderName())) { // Gooderness
            if(ObjectManager.getMob("beholder") != null)
                familiars.add(new PetEntryFamiliar("kehaanGrueMiniKehaan", player, "beholder").setEntityName("Eye of the Baminati").setEntitySize(0.4D));
        }
        if("ganymedes01".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("reiver") != null)
                familiars.add(new PetEntryFamiliar("ganymedes01ReiverBob", player, "reiver").setEntityName("Bob").setEntitySize(0.4D));
        }

        // Patreon:
        if("beckyh2112".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("jengu") != null)
                familiars.add(new PetEntryFamiliar("beckyh2112Jengu", player, "jengu").setEntitySubspeciesID(1).setEntitySize(0.85D));
        }
        if("Drcoolpig".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("jengu") != null)
                familiars.add(new PetEntryFamiliar("DrcoolpigJengu", player, "jengu").setEntitySubspeciesID(1).setEntitySize(0.85D));
        }
        if("mindbound".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("cinder") != null)
                familiars.add(new PetEntryFamiliar("mindboundCinder", player, "cinder").setEntitySize(0.85D));
        }

        return familiars;
    }
}
