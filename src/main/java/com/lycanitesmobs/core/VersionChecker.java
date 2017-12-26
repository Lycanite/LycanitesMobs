package com.lycanitesmobs.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.LycanitesMobs;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class VersionChecker {

	public static class VersionInfo {
		public VersionInfo(String number, String mc) {
			this.versionNumber = number;
			this.versionMC = mc;
		}

		public String versionNumber;
		public String versionMC;
		public boolean isNewer = false;

		/** Sets isNewer to true if this VersionInfo is newer than compareVersion. **/
		public void checkIfNewer(VersionInfo compareVersion) {
			this.isNewer = false;
			String[] versions = this.versionNumber.split(".");
			String[] compareVersions = compareVersion.versionNumber.split(".");
			for (int i = 0; i < 4; i++) {
				int versionNumber = NumberUtils.isNumber(versions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(versions[i].replaceAll("[^\\d.]", "")) : 0;
				int compareVersionNumber = NumberUtils.isNumber(compareVersions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(compareVersions[i].replaceAll("[^\\d.]", "")) : 0;
				if (versionNumber > compareVersionNumber) {
					this.isNewer = true;
					return;
				}
			}
		}
	}

	public static VersionInfo getLatestVersion() {
		VersionInfo currentVersion = new VersionInfo(LycanitesMobs.versionNumber, LycanitesMobs.versionMC);
		VersionInfo latestVersion = null;

		try {
			URL url = new URL(LycanitesMobs.websiteAPI + "/latest");
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
			InputStream inputStream = urlConnection.getInputStream();
			String jsonString = null;
			try {
				jsonString = IOUtils.toString(inputStream, (Charset)null);
			} catch (Exception e) {
				throw e;
			} finally {
				inputStream.close();
			}

			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(jsonString);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String latestVersionNumber = jsonObject.get("version").getAsString();
			String latestVersionMC = jsonObject.get("minecraftVersion").getAsString();
			latestVersion = new VersionInfo(latestVersionNumber, latestVersionMC);
			latestVersion.checkIfNewer(currentVersion);
			LycanitesMobs.printInfo("", "Latest mod version from website is: " + latestVersionNumber);
		}
		catch(Exception e) {}

		if(latestVersion == null) {
			return currentVersion;
		}
		return latestVersion;
	}
}
