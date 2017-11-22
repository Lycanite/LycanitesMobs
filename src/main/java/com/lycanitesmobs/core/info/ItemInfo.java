package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.LycanitesMobs;

public class ItemInfo {
    // ========== Global Settings ==========
    /** The duration (in seconds) of the effects caused by various foods. Use the get functions to get these in ticks. **/
    public static int durationRaw = 10;
    public static int durationCooked = 20;
    public static int durationMeal = 60;
    public static int durationFeast = 10 * 60;

    public static int healingCooked = 2;
    public static int healingMeal = 4;
    public static int healingFeast = 8;

    public static double seasonalItemDropChance = 0.1F;

    public static boolean enableWeaponRecipes = true;

    public static boolean removeOnNoFireTick = true;

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

		healingCooked = config.getInt("Food Healing", "Cooked Healing", healingCooked, "The instant healing received from cooked foods such as Cooked Joust Meat.");
		healingMeal = config.getInt("Food Healing", "Meal Healing", healingMeal, "The instant healing received from crafted meal class foods such as Moss Pie.");
		healingFeast = config.getInt("Food Healing", "Feast Healing", healingFeast, "The instant healing received from the harder to craft feast class foods such as Battle Burrito.");

        seasonalItemDropChance = config.getDouble("Seasonal Item Drop Chance", "Seasonal", seasonalItemDropChance, "The chance of seasonal items dropping such as Winter Gifts. Can be 0-1, 0.25 would be 25%. Set to 0 to disable these drops all together.");

        config.setCategoryComment("Items Enabled", "Here you can enable and disable various crafting recipes for items.");
        enableWeaponRecipes = config.getBool("Items Enabled", "Weapon Recipes Enabled", enableWeaponRecipes, "Set to false to disable the crafting recipes for all weapon items.");

        config.setCategoryComment("Fire", "Special settings for fire blocks, etc.");
        removeOnNoFireTick = config.getBool("Fire", "Remove On No Fire Tick", removeOnNoFireTick, "If set to false, when the doFireTick gamerule is set to false, instead of removing all custom fire such as Hellfire, the fire simply stops spreading instead, this is useful for decorative fire on adventure maps and servers.");
}
}