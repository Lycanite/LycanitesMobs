package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityFear;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class PotionEffects {
    private static final UUID swiftswimmingMoveBoostUUID = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5e");
    private static final AttributeModifier swiftswimmingMoveBoost = (new AttributeModifier(swiftswimmingMoveBoostUUID, "Swiftswimming Speed Boost", 0.6D, 2)).setSaved(false);
	
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
		
		// Night Vision Stops Blindness:
		if(entity.isPotionActive(Potion.blindness.id) && entity.isPotionActive(Potion.nightVision.id))
			entity.removePotionEffect(Potion.blindness.id);
		
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		
		// ========== Paralysis ==========
		if(ObjectManager.getPotionEffect("paralysis") != null) {
			if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("paralysis").getId())) {
				entity.motionX = 0;
				if(entity.motionY > 0)
					entity.motionY = 0;
				entity.motionZ = 0;
				entity.onGround = false;
			}
		}
		
		// ========== Weight ==========
		if(ObjectManager.getPotionEffect("weight") != null) {
			if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("weight").getId())) {
				if(entity.motionY > -0.2D)
					entity.motionY = -0.2D;
			}
		}

        // ========== Swiftswimming ==========
        if(ObjectManager.getPotionEffect("swiftswimming") != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if(entity.isPotionActive(ObjectManager.getPotionEffect("swiftswimming").getId()) && entity.isInWater()) {
                int amplifier = entity.getActivePotionEffect(ObjectManager.getPotionEffect("swiftswimming")).getAmplifier();
                IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                if(movement.getModifier(swiftswimmingMoveBoostUUID) == null) {
                    movement.applyModifier(swiftswimmingMoveBoost);
                }
            }
            else {
                IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                if(movement.getModifier(swiftswimmingMoveBoostUUID) != null) {
                    movement.removeModifier(swiftswimmingMoveBoost);
                }
            }
        }
		
		// ========== Fear ==========
		if(ObjectManager.getPotionEffect("fear") != null) {
			if(!entity.worldObj.isRemote && !invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("fear").getId())) {
				ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
				if(extendedEntity != null) {
					if(extendedEntity.fearEntity == null) {
						EntityFear fearEntity = new EntityFear(entity.worldObj, entity);
						entity.worldObj.spawnEntityInWorld(fearEntity);
						extendedEntity.fearEntity = fearEntity;
					}
				}
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
		if(ObjectManager.getPotionEffect("paralysis") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("paralysis").getId())) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}
		if(ObjectManager.getPotionEffect("weight") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("weight").getId())) {
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
        if(ObjectManager.getPotionEffect("fallresist") != null) {
            if(event.entityLiving.isPotionActive(ObjectManager.getPotionEffect("fallresist").getId())) {
                if("fall".equals(event.source.damageType)) {
                    event.ammount = 0;
                    event.setCanceled(true);
                }
            }
        }

        // ========== Leeching ==========
        if(ObjectManager.getPotionEffect("leech") != null && event.source.getEntity() != null) {
            if(event.source.getEntity() instanceof EntityLivingBase) {
                EntityLivingBase attackingEntity = (EntityLivingBase)(event.source.getEntity());
                if(attackingEntity.isPotionActive(ObjectManager.getPotionEffect("leech").getId())) {
                    float damage = event.ammount;
                    float multiplier = attackingEntity.getActivePotionEffect(ObjectManager.getPotionEffect("leech")).getAmplifier();
                    attackingEntity.heal(damage * multiplier);
                }
            }
        }

        // ========== Penetration ==========
        if(ObjectManager.getPotionEffect("penetration") != null) {
            if(event.entityLiving.isPotionActive(ObjectManager.getPotionEffect("penetration").getId())) {
                float damage = event.ammount;
                float multiplier = event.entityLiving.getActivePotionEffect(ObjectManager.getPotionEffect("penetration")).getAmplifier();
                event.ammount = damage + ((damage * multiplier) / 2);
            }
        }

        // ========== Fear ==========
        if(ObjectManager.getPotionEffect("fear") != null) {
            if(event.entityLiving.isPotionActive(ObjectManager.getPotionEffect("fear").getId())) {
                if("inWall".equals(event.source.damageType)) {
                    event.ammount = 0;
                    event.setCanceled(true);
                }
            }
        }
    }
}
