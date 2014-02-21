package lycanite.lycanitesmobs.api;

import lycanite.lycanitesmobs.Config;

public interface ILycaniteMod {
	public ILycaniteMod getInstance();
	public String getModID();
	public String getDomain();
	public Config getConfig();
	public int getNextMobID();
	public int getNextProjectileID();
}
