package lycanite.lycanitesmobs.api;

import net.minecraft.entity.EntityLivingBase;

import com.google.common.io.ByteArrayDataInput;

public interface ICustomMobTest {
	
	// ========== Custom AI ==========
	// Movement:
	public int getPauseChance();
	
	// Get Melee Attack Time:
	public int getAttackTime();
	
	// Stealth:
	public float getStealth();
	public void setStealth(float setStealth);
	public boolean canStealth();
	
	// Attack Phase:
	public byte getAttackPhase();
	public void setAttackPhase(byte setAttackPhase);
	public void nextAttackPhase();
	
	// Alpha Entites:
	public EntityLivingBase getAlphaEntity();
	public void setAlphaEntity(EntityLivingBase setAlpha);
	
	// ========== Animation Tracking ==========
	// Check for an Attack Target:
	public boolean checkAttackTarget();
	
	// Check if just Attacked:
	public boolean checkJustAttacked();
	
	// Set if just Attacked:
	public void setJustAttacked();
}
