package lycanite.lycanitesmobs.api;

import net.minecraft.util.ResourceLocation;

public interface ICustomProjectile {
	
	// Damage:
	public float getDamage();
	public void setDamage(int newDamage);
	
	// Scale:
	public float getProjectileScale();
	public void setProjectileScale(float newScale);
	
	// Visuals:
	public ResourceLocation getTexture();
	
	// Sounds:
	public String getLaunchSound();
}
