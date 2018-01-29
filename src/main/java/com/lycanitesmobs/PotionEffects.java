package com.lycanitesmobs;

import com.lycanitesmobs.core.entity.EntityFear;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class PotionEffects {
    private static final UUID swiftswimmingMoveBoostUUID = UUID.fromString("6d4fe17f-06eb-4ebc-a573-364b79faed5e");
    private static final AttributeModifier swiftswimmingMoveBoost = (new AttributeModifier(swiftswimmingMoveBoostUUID, "Swiftswimming Speed Boost", 0.6D, 2)).setSaved(false);
	
	// ==================================================
	//                    Entity Update
	// ==================================================
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null) return;
		
		for(Object potionEffectObj : entity.getActivePotionEffects()) {
			if(potionEffectObj == null) {
				entity.clearActivePotions();
				LycanitesMobs.printWarning("EffectsSetup", "Found a null potion effect on entity: " + entity + " all effects have been removed from this entity.");
			}
		}
		
		// Night Vision Stops Blindness:
		if(entity.isPotionActive(MobEffects.BLINDNESS) && entity.isPotionActive(MobEffects.NIGHT_VISION))
			entity.removePotionEffect(MobEffects.BLINDNESS);
		
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.capabilities.isCreativeMode;
		}
		
		// ========== Paralysis ==========
		if(ObjectManager.getPotionEffect("paralysis") != null) {
			if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("paralysis"))) {
				entity.motionX = 0;
				if(entity.motionY > 0)
					entity.motionY = 0;
				entity.motionZ = 0;
				entity.onGround = false;
			}
		}
		
		// ========== Weight ==========
		if(ObjectManager.getPotionEffect("weight") != null) {
			if(!invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("weight")) && !entity.isPotionActive(MobEffects.STRENGTH)) {
				if(entity.motionY > -0.2D)
					entity.motionY = -0.2D;
			}
		}

        // ========== Swiftswimming ==========
        if(ObjectManager.getPotionEffect("swiftswimming") != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if(entity.isPotionActive(ObjectManager.getPotionEffect("swiftswimming")) && entity.isInWater()) {
                int amplifier = entity.getActivePotionEffect(ObjectManager.getPotionEffect("swiftswimming")).getAmplifier();
                IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if(movement.getModifier(swiftswimmingMoveBoostUUID) == null) {
                    movement.applyModifier(swiftswimmingMoveBoost);
                }
            }
            else {
                IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if(movement.getModifier(swiftswimmingMoveBoostUUID) != null) {
                    movement.removeModifier(swiftswimmingMoveBoost);
                }
            }
        }
		
		// ========== Fear ==========
		if(ObjectManager.getPotionEffect("fear") != null) {
			if(!entity.getEntityWorld().isRemote && !invulnerable && entity.isPotionActive(ObjectManager.getPotionEffect("fear"))) {
				ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(entity);
				if(extendedEntity != null) {
					if(extendedEntity.fearEntity == null) {
						EntityFear fearEntity = new EntityFear(entity.getEntityWorld(), entity);
						entity.getEntityWorld().spawnEntity(fearEntity);
						extendedEntity.fearEntity = fearEntity;
					}
				}
			}
		}

		// ========== Immunisation ==========
		if(ObjectManager.getPotionEffect("immunization") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("immunization"))) {
				if(entity.isPotionActive(MobEffects.POISON)) {
					entity.removePotionEffect(MobEffects.POISON);
				}
				if(entity.isPotionActive(MobEffects.HUNGER)) {
					entity.removePotionEffect(MobEffects.HUNGER);
				}
				if(entity.isPotionActive(MobEffects.WEAKNESS)) {
					entity.removePotionEffect(MobEffects.WEAKNESS);
				}
				if(entity.isPotionActive(MobEffects.NAUSEA)) {
					entity.removePotionEffect(MobEffects.NAUSEA);
				}
				if(ObjectManager.getPotionEffect("paralysis") != null) {
					if(entity.isPotionActive(ObjectManager.getPotionEffect("paralysis"))) {
						entity.removePotionEffect(ObjectManager.getPotionEffect("paralysis"));
					}
				}
			}
		}

		// ========== Cleansed ==========
		if(ObjectManager.getPotionEffect("cleansed") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("cleansed"))) {
				if(entity.isPotionActive(MobEffects.WITHER)) {
					entity.removePotionEffect(MobEffects.WITHER);
				}
				if(entity.isPotionActive(MobEffects.UNLUCK)) {
					entity.removePotionEffect(MobEffects.UNLUCK);
				}
				if(ObjectManager.getPotionEffect("fear") != null) {
					if(entity.isPotionActive(ObjectManager.getPotionEffect("fear"))) {
						entity.removePotionEffect(ObjectManager.getPotionEffect("fear"));
					}
				}
				if(ObjectManager.getPotionEffect("insomnia") != null) {
					if(entity.isPotionActive(ObjectManager.getPotionEffect("insomnia"))) {
						entity.removePotionEffect(ObjectManager.getPotionEffect("insomnia"));
					}
				}
			}
		}

		// ========== Disable Nausea ==========
		if(LycanitesMobs.disableNausea && event.getEntityLiving() instanceof EntityPlayer) {
			if(entity.isPotionActive(MobEffects.NAUSEA)) {
				entity.removePotionEffect(MobEffects.NAUSEA);
			}
		}
	}
	
	
	// ==================================================
	//                    Entity Jump
	// ==================================================
	@SubscribeEvent
	public void onEntityJump(LivingJumpEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
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
			if(entity.isPotionActive(ObjectManager.getPotionEffect("paralysis"))) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}
		if(ObjectManager.getPotionEffect("weight") != null) {
			if(entity.isPotionActive(ObjectManager.getPotionEffect("weight"))) {
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

        if(event.getEntityLiving() == null)
            return;

        // ========== Penetration ==========
        if(ObjectManager.getPotionEffect("fallresist") != null) {
            if(event.getEntityLiving().isPotionActive(ObjectManager.getPotionEffect("fallresist"))) {
                if("fall".equals(event.getSource().damageType)) {
                    event.setAmount(0);
                    event.setCanceled(true);
                }
            }
        }

        // ========== Leeching ==========
        if(ObjectManager.getPotionEffect("leech") != null && event.getSource().getTrueSource() != null) {
            if(event.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attackingEntity = (EntityLivingBase)(event.getSource().getTrueSource());
                if(attackingEntity.isPotionActive(ObjectManager.getPotionEffect("leech"))) {
                    float damage = event.getAmount();
                    float multiplier = attackingEntity.getActivePotionEffect(ObjectManager.getPotionEffect("leech")).getAmplifier();
                    attackingEntity.heal(damage * multiplier);
                }
            }
        }

        // ========== Penetration ==========
        if(ObjectManager.getPotionEffect("penetration") != null) {
            if(event.getEntityLiving().isPotionActive(ObjectManager.getPotionEffect("penetration"))) {
                float damage = event.getAmount();
                float multiplier = event.getEntityLiving().getActivePotionEffect(ObjectManager.getPotionEffect("penetration")).getAmplifier();
                event.setAmount(damage + ((damage * multiplier) / 2));
            }
        }

        // ========== Fear ==========
        if(ObjectManager.getPotionEffect("fear") != null) {
            if(event.getEntityLiving().isPotionActive(ObjectManager.getPotionEffect("fear"))) {
                if("inWall".equals(event.getSource().damageType)) {
                    event.setAmount(0);
                    event.setCanceled(true);
                }
            }
        }
    }


	// ==================================================
	//                    Entity Heal
	// ==================================================
	@SubscribeEvent
	public void onEntityHeal(LivingHealEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if(entity == null)
			return;

		// Rejuvenation:
		PotionBase rejuvenation = ObjectManager.getPotionEffect("rejuvenation");
		if(rejuvenation != null) {
			if(entity.isPotionActive(rejuvenation)) {
				event.setAmount(event.getAmount() * (2 * (1 + entity.getActivePotionEffect(rejuvenation).getAmplifier())));
			}
		}

		// Decay:
		PotionBase decay = ObjectManager.getPotionEffect("decay");
		if(decay != null) {
			if(entity.isPotionActive(decay)) {
				event.setAmount(event.getAmount() / (2 * (1 + entity.getActivePotionEffect(decay).getAmplifier())));
			}
		}
	}


	// ==================================================
	//                Player Use Bed Event
	// ==================================================
	/** This uses the player sleep in bed event to spawn mobs. **/
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player == null || player.getEntityWorld() == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;

		// Insomnia:
		PotionBase insomnia = ObjectManager.getPotionEffect("insomnia");
		if(insomnia != null && player.isPotionActive(insomnia)) {
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
		}
	}
}
