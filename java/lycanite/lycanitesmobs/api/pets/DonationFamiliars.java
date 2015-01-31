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
                familiars.add(new PetEntryFamiliar("LycaniteGrueJasper", player, "grue").setEntityName("Jasper").setEntitySubspeciesID(2).setEntitySize(0.6D));
        }
        if("Kashiaka".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("arix") != null)
                familiars.add(new PetEntryFamiliar("KashiakaArixAlix", player, "arix").setEntityName("Alix").setEntitySubspeciesID(2).setEntitySize(0.6D));
        }
        if("quartzenstein".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("arix") != null)
                familiars.add(new PetEntryFamiliar("quartzensteinArixAsterix", player, "arix").setEntityName("Asterix").setEntitySize(0.6D));
        }

        // Celebs:
        if("Jbams".equals(player.getCommandSenderName())) { // JonBams
            if(ObjectManager.getMob("phantom") != null)
                familiars.add(new PetEntryFamiliar("JbamsPhantomAmanda", player, "phantom").setEntityName("Amanda").setEntitySubspeciesID(1).setEntitySize(0.6D));
        }
        if("kehaan".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("grue") != null)
                familiars.add(new PetEntryFamiliar("kehaanGrueMiniKehaan", player, "grue").setEntityName("Mini Kehaan").setEntitySubspeciesID(1).setEntitySize(0.6D));
        }
        if("Augustus1979".equals(player.getCommandSenderName())) { // Gooderness
            if(ObjectManager.getMob("beholder") != null)
                familiars.add(new PetEntryFamiliar("kehaanGrueMiniKehaan", player, "beholder").setEntityName("Eye of the Baminati").setEntitySize(0.4D));
        }
        if("ganymedes01".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("reiver") != null)
                familiars.add(new PetEntryFamiliar("ganymedes01ReiverBob", player, "reiver").setEntityName("Bob").setEntitySize(0.6D));
        }
        if("FatherToast".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("zephyr") != null)
                familiars.add(new PetEntryFamiliar("FatherToastZephyrToastson", player, "zephyr").setEntityName("Toastson").setEntitySubspeciesID(1).setEntitySize(0.6D));
        }
        if("MotherToast".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("reiver") != null)
                familiars.add(new PetEntryFamiliar("MotherToastReiverToastdottir", player, "reiver").setEntityName("Toastdottir").setEntitySize(0.6D));
        }
        if("JaredBGreat".equals(player.getCommandSenderName())) { // BlackJar72
            if(ObjectManager.getMob("zephyr") != null)
                familiars.add(new PetEntryFamiliar("JaredBGreatZephyrThunnar", player, "zephyr").setEntityName("Thunnar").setEntitySize(0.6D));
        }
        if("JarnoVH".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("chupacabra") != null)
                familiars.add(new PetEntryFamiliar("JarnoVHChupacabraTimmy", player, "chupacabra").setEntityName("Face Biter").setEntitySize(0.8D));
        }
        if("grimuars".equals(player.getCommandSenderName())) { // Merkaba5
            if(ObjectManager.getMob("zephyr") != null)
                familiars.add(new PetEntryFamiliar("grimuarsZephyrXor", player, "zephyr").setEntityName("Xor").setEntitySubspeciesID(2).setEntitySize(0.6D));
        }

        // Patreon:
        if("beckyh2112".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("jengu") != null)
                familiars.add(new PetEntryFamiliar("beckyh2112Jengu", player, "jengu").setEntitySubspeciesID(1).setEntitySize(0.6D));
        }
        if("Drcoolpig".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("jengu") != null)
                familiars.add(new PetEntryFamiliar("DrcoolpigJengu", player, "jengu").setEntitySubspeciesID(1).setEntitySize(0.6D));
        }
        if("mindbound".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("cinder") != null)
                familiars.add(new PetEntryFamiliar("mindboundCinder", player, "cinder").setEntitySize(0.6D));
        }
        if("Ashvaela".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("geonach") != null)
                familiars.add(new PetEntryFamiliar("AshvaelaGeonach", player, "geonach").setEntitySubspeciesID(3).setEntitySize(0.15D));
        }
        if("Ringowhs".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("jengu") != null)
                familiars.add(new PetEntryFamiliar("RingowhsJengu", player, "jengu").setEntityName("Gooderness Destroyer").setEntitySize(0.6D).setEntitySubspeciesID(1));
        }
        if("PunitiveCape7".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("cinder") != null)
                familiars.add(new PetEntryFamiliar("PunitiveCape7Cinder", player, "cinder").setEntitySize(0.6D).setEntitySubspeciesID(1));
        }
        if("ApocDev".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("spriggan") != null)
                familiars.add(new PetEntryFamiliar("ApocDevSprigganObliterator", player, "spriggan").setEntityName("Obliterator").setEntitySize(0.6D).setEntitySubspeciesID(1));
        }
        if("Headwound_".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("cinder") != null)
                familiars.add(new PetEntryFamiliar("Headwound_Cinder", player, "cinder").setEntitySize(0.6D).setEntitySubspeciesID(2));
        }
        if("deanpryzmenski".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("zephyr") != null)
                familiars.add(new PetEntryFamiliar("deanpryzmenskiZephyr", player, "zephyr").setEntitySize(0.6D));
        }
        if("angelofdespair".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("geonach") != null)
                familiars.add(new PetEntryFamiliar("angelofdespairGeonach", player, "geonach").setEntitySize(0.6D).setEntitySubspeciesID(1));
        }
        if("callen444".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("phantom") != null)
                familiars.add(new PetEntryFamiliar("callen444PhantomFluttershy", player, "phantom").setEntityName("Fluttershy").setEntitySize(0.6D));
        }
        if("CONfoundit".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("geonach") != null)
                familiars.add(new PetEntryFamiliar("CONfoundit", player, "geonach").setEntitySubspeciesID(3).setEntitySize(0.15D));
        }
        if("nruffilo".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("uvaraptor") != null)
                familiars.add(new PetEntryFamiliar("nruffiloUvaraptor", player, "uvaraptor").setEntitySize(1.1D));
        }
        if("AerinNight".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("spriggan") != null)
                familiars.add(new PetEntryFamiliar("AerinNightSpriggan", player, "spriggan").setEntitySize(0.6D).setEntitySubspeciesID(2));
        }
        if("WillBoy20101".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("cinder") != null)
                familiars.add(new PetEntryFamiliar("WillBoy20101Cinder", player, "cinder").setEntitySize(0.6D).setEntitySubspeciesID(1));
        }
        if("q_divi".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("grue") != null)
                familiars.add(new PetEntryFamiliar("q_diviGrue", player, "grue").setEntitySize(0.6D));
        }
        if("Leonzell".equals(player.getCommandSenderName())) {
            if(ObjectManager.getMob("spriggan") != null)
                familiars.add(new PetEntryFamiliar("LeonzellSpriggan", player, "spriggan").setEntitySize(0.6D));
        }

        return familiars;
    }
}
