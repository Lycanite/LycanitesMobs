package lycanite.lycanitesmobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class PotionEffects {
	
	// ==================================================
	//                    Entity Update
	// ==================================================
	@ForgeSubscribe
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null)
			return;
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		
		// ========== Paralysis ==========
		if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("Paralysis").getId())) {
			entity.motionX = 0;
			if(entity.motionY > 0)
				entity.motionY = 0;
			entity.motionZ = 0;
			entity.onGround = false;
		}
		
		// ========== Weight ==========
		if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("Weight").getId())) {
			if(entity.motionY > -0.2D)
				entity.motionY = -0.2D;
		}
	}
	
	
	// ==================================================
	//                    Entity Jump
	// ==================================================
	@ForgeSubscribe
	public void onEntityJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null)
			return;
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		
		// ========== Anti-Jumping ==========
		if(!invulnerable && (
				entity.isPotionActive(ObjectManager.getPotionEffect("Paralysis").getId()) ||
				entity.isPotionActive(ObjectManager.getPotionEffect("Weight").getId())
			)) {
			if(event.isCancelable())
				event.setCanceled(true);
		}
	}
}
