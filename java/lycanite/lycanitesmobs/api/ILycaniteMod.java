package lycanite.lycanitesmobs.api;

import lycanite.lycanitesmobs.OldConfig;

public interface ILycaniteMod {
	public ILycaniteMod getInstance();
	public String getModID();
	public String getDomain();
	public OldConfig getConfig();
	public int getNextMobID();
	public int getNextProjectileID();
}
