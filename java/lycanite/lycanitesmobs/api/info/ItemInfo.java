package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;

public class ItemInfo {
    // ========== Global Settings ==========
    /** The duration (in seconds) of the effects caused by various foods. Use the get functions to get these in ticks. **/
    public static int durationRaw = 10;
    public static int durationCooked = 10;
    public static int durationMeal = 60;
    public static int durationFeast = 10 * 60;

    public static double seasonalItemDropChance = 0.1F;

    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
    public static void loadGlobalSettings() {
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "general");
        config.setCategoryComment("Food Effect Durations", "Here you can set the durations in seconds for each of the food effects.");
        durationRaw = config.getInt("Food Effect Durations", "Raw Debuffs", durationRaw, "The negative effects from raw foods such as Raw Maka Meat.");
        durationCooked = config.getInt("Food Effect Durations", "Cooked Buffs", durationCooked, "The positive effects from cooked foods such as Cooked Joust Meat.");
        durationMeal = config.getInt("Food Effect Durations", "Meal Buffs", durationMeal, "The positive effects from crafted meal class foods such as Moss Pie.");
        durationFeast = config.getInt("Food Effect Durations", "Feast Buffs", durationFeast, "The positive effects from the harder to craft feast class foods such as Battle Burrito.");
        seasonalItemDropChance = config.getDouble("Seasonal Item Drop Chance", "Seasonal", seasonalItemDropChance, "The chance of seasonal items dropping such as Winter Gifts. Can be 0-1, 0.25 would be 25%. Set to 0 to disable these drops all together.");
}
}