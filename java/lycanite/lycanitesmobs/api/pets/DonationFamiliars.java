package lycanite.lycanitesmobs.api.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lycanite.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DonationFamiliars {
    public static DonationFamiliars instance = new DonationFamiliars();
    public Map<String, Map<String, PetEntry>> playerFamiliars = new HashMap<String, Map<String, PetEntry>>();
    public boolean jsonLoaded = false;

    // ==================================================
    //                  Read From JSON
    // ==================================================
    public void readFromJSON() {
        this.jsonLoaded = true;

        // Load JSON File:
        String jsonString = null;
        try {
            URL familiarURL = new URL(LycanitesMobs.websiteAPI + "/familiar");
            URLConnection urlConnection = familiarURL.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
            InputStream inputStream = urlConnection.getInputStream();
            try {
                jsonString = IOUtils.toString(inputStream, (Charset) null);
            } catch (Exception e) {
                throw e;
            } finally {
                inputStream.close();
            }
            LycanitesMobs.printInfo("", "Online donations file read successfully.");
        }
        catch(Exception e) {
            LycanitesMobs.printInfo("", "Unable to access the online donations file, using local copy instead, this might be out of date.");
            e.printStackTrace();
//            try {
//                jsonString = FileUtils.readFileToString(FileUtils.getFile(LycanitesMobs.proxy.getMinecraftDir() + "/assets/lycanitesmobs/familiars.json"));
//            } catch (IOException e1) {
//                LycanitesMobs.printWarning("Donations", "There was a problem reading the local copy of the donations file.");
//                e1.printStackTrace();
//                return;
//            }
            jsonString = this.getLocalJSON();
        }

        // Parse JSON File:
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = jsonParser.parse(jsonString).getAsJsonArray();
        Iterator<JsonElement> jsonIterator = jsonArray.iterator();
        while(jsonIterator.hasNext()) {
            JsonObject familiarJson = jsonIterator.next().getAsJsonObject();
            String minecraft_uuid = familiarJson.get("minecraft_uuid").getAsString();
            String donation_username = familiarJson.get("donation_username").getAsString();
            String familiar_species = familiarJson.get("familiar_species").getAsString();
            int familiar_subspecies = familiarJson.get("familiar_subspecies").getAsInt();
            String familiar_name = familiarJson.get("familiar_name").getAsString();
            String familiar_color = familiarJson.get("familiar_color").getAsString();

            String familiarEntryName = donation_username + familiar_species + familiar_name;
            PetEntryFamiliar familiarEntry = new PetEntryFamiliar(familiarEntryName, null, familiar_species);
            familiarEntry.setEntitySubspeciesID(familiar_subspecies);
            familiarEntry.setEntitySize(familiar_subspecies < 3 ? 0.6D : 0.3D);

            if(!"".equals(familiar_name))
                familiarEntry.setEntityName(familiar_name);
            familiarEntry.setColor(familiar_color);

            // Add Pet Entries or Update Existing Entries:
            if(!this.playerFamiliars.containsKey(minecraft_uuid))
                this.playerFamiliars.put(minecraft_uuid, new HashMap<String, PetEntry>());
            if(!this.playerFamiliars.containsKey(familiarEntryName))
                this.playerFamiliars.get(minecraft_uuid).put(familiarEntryName, familiarEntry);
            else {
                PetEntry existingEntry = this.playerFamiliars.get(minecraft_uuid).get(familiarEntryName);
                existingEntry.copy(familiarEntry);
            }
        }
    }


    // ==================================================
    //              Get Familiars For Player
    // ==================================================
    public Map<String, PetEntry> getFamiliarsForPlayer(EntityPlayer player) {
        if(!this.jsonLoaded)
            this.readFromJSON();
        String playerUUID = player.getUniqueID().toString();
        Map<String, PetEntry> playerFamiliarEntries = this.playerFamiliars.get(playerUUID);
        if(playerFamiliarEntries != null)
            for(PetEntry familiarEntry : playerFamiliarEntries.values()) {
                if(familiarEntry.host == null)
                    familiarEntry.host = player;
            }
        return playerFamiliarEntries;
    }


    // ==================================================
    //                Local JSON Data
    // ==================================================
    private String getLocalJSON() {
        return "[{\"id\":\"1\",\"minecraft_uuid\":\"b1829e52-769d-4296-9733-39654bb0449d\",\"donation_username\":\"Lycanite\",\"familiar_species\":\"Grue\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"Jasper\",\"familiar_color\":\"009955\",\"created_at\":\"2014-12-29 13:52:03\",\"updated_at\":\"2014-12-29 13:52:03\"},{\"id\":\"2\",\"minecraft_uuid\":\"888a5d87-8e7c-4b71-8189-1a343b3e3844\",\"donation_username\":\"Kashiaka\",\"familiar_species\":\"Arix\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"Alix\",\"familiar_color\":\"990099\",\"created_at\":\"2014-12-29 13:56:38\",\"updated_at\":\"2014-12-29 13:56:38\"},{\"id\":\"45\",\"minecraft_uuid\":\"6e18b7ad-c491-4965-b4d8-95d8fd8ddff7\",\"donation_username\":\"Quartzenstein\",\"familiar_species\":\"arix\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"Asterix\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"46\",\"minecraft_uuid\":\"11688ff5-212a-4bf5-bd9f-2cd0d600949d\",\"donation_username\":\"Jbams\",\"familiar_species\":\"phantom\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"Amanda\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"47\",\"minecraft_uuid\":\"2cea0fad-f893-4ea4-ae97-a7b98cb104a6\",\"donation_username\":\"Kehaan\",\"familiar_species\":\"grue\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"Mini Kehaan\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"48\",\"minecraft_uuid\":\"de6721e7-23b4-42ec-95c0-e4c976c7fa85\",\"donation_username\":\"Gooderness\",\"familiar_species\":\"grue\",\"familiar_subspecies\":\"3\",\"familiar_name\":\"Chewie\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"49\",\"minecraft_uuid\":\"539c3716-ce9a-4ba5-9721-310e755abe5c\",\"donation_username\":\"ganymedes01\",\"familiar_species\":\"reiver\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"Bob\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"50\",\"minecraft_uuid\":\"6f50f9a4-4584-4192-966d-23dac2211e18\",\"donation_username\":\"FatherToast\",\"familiar_species\":\"zephyr\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"Toastson\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"51\",\"minecraft_uuid\":\"adbd3196-32f1-4bea-98be-e24915f589a9\",\"donation_username\":\"MotherToast\",\"familiar_species\":\"reiver\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"Toastdottir\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"52\",\"minecraft_uuid\":\"91207de8-ee27-40d1-bacf-e708401a15ad\",\"donation_username\":\"BlackJar72\",\"familiar_species\":\"zephyr\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"Thunnar\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"53\",\"minecraft_uuid\":\"b52c89ad-57ce-403a-95ee-80e724f96fad\",\"donation_username\":\"JarnoVH\",\"familiar_species\":\"chupacabra\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"Face Biter\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"54\",\"minecraft_uuid\":\"572474b4-553f-447a-9547-8b1896325dea\",\"donation_username\":\"Merkaba5\",\"familiar_species\":\"zephyr\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"Xor\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"55\",\"minecraft_uuid\":\"60923a58-481b-475c-91b2-7f63fe4c3122\",\"donation_username\":\"beckyh2112\",\"familiar_species\":\"jengu\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"56\",\"minecraft_uuid\":\"d935130f-35ed-4a15-9614-e13247ca074d\",\"donation_username\":\"darkcallen444nocious\",\"familiar_species\":\"grue\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"57\",\"minecraft_uuid\":\"5fdf7a82-a10b-43fe-9997-fe87782cae40\",\"donation_username\":\"mindbound\",\"familiar_species\":\"cinder\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"58\",\"minecraft_uuid\":\"989915d2-0e48-44e5-a3ba-d0bf51e16675\",\"donation_username\":\"Ashvaela\",\"familiar_species\":\"geonach\",\"familiar_subspecies\":\"3\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"59\",\"minecraft_uuid\":\"6e505713-3239-4214-b116-02f0e23fbbdc\",\"donation_username\":\"Ringowhs\",\"familiar_species\":\"jengu\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"Gooderness Destroyer\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"60\",\"minecraft_uuid\":\"436e2996-cd2d-4e8e-98a3-b59964e9d5b7\",\"donation_username\":\"PunitiveCape7\",\"familiar_species\":\"cinder\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"61\",\"minecraft_uuid\":\"25ea0d58-1a3b-4d6d-a869-ea3311593fe5\",\"donation_username\":\"ApocDev\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"62\",\"minecraft_uuid\":\"aab0fb89-bba2-4694-9aa6-500a2dc63ff7\",\"donation_username\":\"Headwound\",\"familiar_species\":\"cinder\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"63\",\"minecraft_uuid\":\"deanpryzmenski\",\"donation_username\":\"deanpryzmenski\",\"familiar_species\":\"zephyr\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"64\",\"minecraft_uuid\":\"angelofdespair\",\"donation_username\":\"angelofdespair\",\"familiar_species\":\"geonach\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"65\",\"minecraft_uuid\":\"callen444\",\"donation_username\":\"callen444\",\"familiar_species\":\"phantom\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"Fluttershy\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"66\",\"minecraft_uuid\":\"2cc81f14-77a2-4f95-80e5-2e385b371304\",\"donation_username\":\"CONfoundit\",\"familiar_species\":\"geonach\",\"familiar_subspecies\":\"3\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"67\",\"minecraft_uuid\":\"nruffilo\",\"donation_username\":\"nruffilo\",\"familiar_species\":\"uvaraptor\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"68\",\"minecraft_uuid\":\"c7c946e5-b302-4329-ae3b-948f2629e94b\",\"donation_username\":\"AerinNight\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"69\",\"minecraft_uuid\":\"42e496eb-ca14-4c50-97bc-ff85d01fc0c9\",\"donation_username\":\"WillBoy20101\",\"familiar_species\":\"cinder\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"70\",\"minecraft_uuid\":\"9ce9ce59-cf96-4b4a-af30-47c97f696bec\",\"donation_username\":\"q_divi\",\"familiar_species\":\"grue\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"71\",\"minecraft_uuid\":\"faec11e4-a9b0-4714-81f6-bfd26aff3de3\",\"donation_username\":\"Leonzell\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"72\",\"minecraft_uuid\":\"35ab69e3-8e0b-4432-a861-f4e60851b4f0\",\"donation_username\":\"Janadam7\",\"familiar_species\":\"cinder\",\"familiar_subspecies\":\"1\",\"familiar_name\":\"DuckBoy\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"73\",\"minecraft_uuid\":\"d5961478-8be6-4a74-820d-40063bf056fc\",\"donation_username\":\"scottysnyder\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"74\",\"minecraft_uuid\":\"c13f67ad-31aa-4916-bb29-4ba234ff302b\",\"donation_username\":\"Aldaitha\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"Ivy Zealkiller\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"75\",\"minecraft_uuid\":\"f494e55d-1f91-446b-8189-f62ed16e5df2\",\"donation_username\":\"Gtprider_1\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"2\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"76\",\"minecraft_uuid\":\"7af92218-b11c-4eec-a865-4d5e1409aa21\",\"donation_username\":\"gaurdion\",\"familiar_species\":\"spriggan\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"},{\"id\":\"77\",\"minecraft_uuid\":\"838591a8-0e67-4615-96e6-c93070e9110c\",\"donation_username\":\"RoguesDad\",\"familiar_species\":\"phantom\",\"familiar_subspecies\":\"0\",\"familiar_name\":\"\",\"familiar_color\":\"000000\",\"created_at\":\"-0001-11-30 00:00:00\",\"updated_at\":\"-0001-11-30 00:00:00\"}]";
    }
}
