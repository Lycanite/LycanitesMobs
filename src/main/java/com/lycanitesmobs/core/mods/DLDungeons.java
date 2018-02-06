package com.lycanitesmobs.core.mods;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

/** This is a wrapper class for the DLDungeons API, uses reflection for now until I find a better way. **/
public class DLDungeons {
	
	// ========== Initialize ==========
	/** Called if the DLDungeons API is found, this then asks the API if DLDungeons is ready to go, if so, it updates MobInfo so that mobs will register their themes. **/
	public static void init() {
		if(!Loader.isModLoaded("DLDungeonsJBG") && !Loader.isModLoaded("dldungeonsjbg"))
			return;
		
		LycanitesMobs.printInfo("", "Doomlike Dungeons Mod Detected...");
		
		Class dlDungeonsAPI = null;
		Method isLoaded = null;
		try {
			dlDungeonsAPI = Class.forName("jaredbgreat.dldungeons.api.DLDungeonsAPI");
			isLoaded = dlDungeonsAPI.getMethod("isLoaded");
		} catch (Exception e) {
			LycanitesMobs.printWarning("", "Unable to find DLDungeons API Class/Method via reflection:");
			e.printStackTrace();
		}
		
		if(dlDungeonsAPI == null || isLoaded == null)
			return;
		
		try {
			if(((Boolean)isLoaded.invoke(null)).booleanValue())
				CreatureManager.getInstance().dlDungeonsLoaded = true;
		} catch (Exception e) {
			LycanitesMobs.printWarning("", "Unable to invoke DLDungeons API method isLoaded():");
			e.printStackTrace();
		}
	}
	
	
	// ========== Add Mob ==========
	/**
	 * Adds a mob entry to the DLDungeon Themes.
	 * @param creatureInfo The MobInfo class of the mob, this will be used to get the theme amongst other things.
	 */
	public static void addMob(CreatureInfo creatureInfo) {
		String mobName = creatureInfo.getEntityId();
		String themes = creatureInfo.group.dungeonThemes;
		if(!"".equalsIgnoreCase(themes) && creatureInfo.dungeonLevel > 0) {
			try {
				Class dlDungeonsAPI = Class.forName("jaredbgreat.dldungeons.api.DLDungeonsAPI");
				Method addMob = dlDungeonsAPI.getMethod("addMob", String.class, int.class, String[].class);
				addMob.invoke(null, mobName, creatureInfo.dungeonLevel, themes.split(","));
				//DLDungeonsAPI.addMob(mobName, creatureInfo.dungeonLevel, themes.split(","));
				LycanitesMobs.printDebug("MobSetup", "[DLDungeons] Added " + mobName + " with the level: " + creatureInfo.dungeonLevel + " and themes: " + themes);
			} catch(Exception e) {
				LycanitesMobs.printWarning("", "Unable to add " + mobName + " to DLDungeons API:");
			}
		}
	}
}
