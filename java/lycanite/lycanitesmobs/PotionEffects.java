package lycanite.lycanitesmobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class PotionEffects {
	
	// ==================================================
	//                    Entity Update
	// ==================================================
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null) return;
		
		for(Object potionEffectObj : entity.getActivePotionEffects()) {
			if(potionEffectObj == null) {
				entity.clearActivePotions();
				LycanitesMobs.printWarning("EffectsSetup", "Found a null potion effect on entity: " + entity + " all effects have been removed from this entity.");
			}
		}
		
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		
		// ========== Paralysis ==========
		if(ObjectManager.getPotionEffect("Paralysis") != null) {
			if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("Paralysis").getId())) {
				entity.motionX = 0;
				if(entity.motionY > 0)
					entity.motionY = 0;
				entity.motionZ = 0;
				entity.onGround = false;
			}
		}
		
		// ========== Weight ==========
		if(ObjectManager.getPotionEffect("Weight") != null) {
			if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("Weight").getId())) {
				if(entity.motionY > -0.2D)
					entity.motionY = -0.2D;
			}
		}
	}
	
	
	// ==================================================
	//                    Entity Jump
	// ==================================================
	@SubscribeEvent
	public void onEntityJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity == null)
			return;
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		if(invulnerable) return;
			
		// ========== Anti-Jumping ==========
		if(ObjectManager.getPotionEffect("Paralysis") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("Paralysis").getId())) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}
		if(ObjectManager.getPotionEffect("Weight") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("Weight").getId())) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}
	}


    // ==================================================
    //                 Living Hurt Event
    // ==================================================
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if(event.isCancelable() && event.isCanceled())
            return;

        if(event.entityLiving == null)
            return;

        // ========== Penetration ==========
        if(ObjectManager.getPotionEffect("Penetration") != null) {
            if(event.entityLiving.isPotionActive(ObjectManager.getPotionEffect("Penetration").getId())) {
                float damage = event.ammount;
                float multiplier = event.entityLiving.getActivePotionEffect(ObjectManager.getPotionEffect("Penetration")).getAmplifier();
                event.ammount = damage + ((damage * multiplier) / 2);
            }
        }
    }
}
