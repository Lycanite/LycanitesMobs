package com.lycanitesmobs;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.network.MessageEntityVelocity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
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
		if(entity == null) {
			return;
		}

		// Null Effect Fix:
		for(Object potionEffectObj : entity.getActivePotionEffects()) {
			if(potionEffectObj == null) {
				entity.clearActivePotions();
				LycanitesMobs.printWarning("EffectsSetup", "Found a null potion effect on entity: " + entity + " all effects have been removed from this entity.");
			}
		}
		
		// Night Vision Stops Blindness:
		if(entity.isPotionActive(MobEffects.BLINDNESS) && entity.isPotionActive(MobEffects.NIGHT_VISION)) {
			entity.removePotionEffect(MobEffects.BLINDNESS);
		}


		// Disable Nausea:
		if(LycanitesMobs.disableNausea && event.getEntityLiving() instanceof EntityPlayer) {
			if(entity.isPotionActive(MobEffects.NAUSEA)) {
				entity.removePotionEffect(MobEffects.NAUSEA);
			}
		}

		// Immunity:
		boolean invulnerable = false;
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			invulnerable = player.isCreative() || player.isSpectator();
		}


		// ========== Debuffs ==========
		// Paralysis
		PotionBase paralysis = ObjectManager.getPotionEffect("paralysis");
		if(paralysis != null) {
			if(!invulnerable && entity.isPotionActive(paralysis)) {
				entity.motionX = 0;
				if(entity.motionY > 0)
					entity.motionY = 0;
				entity.motionZ = 0;
				entity.onGround = false;
			}
		}
		
		// Weight
		PotionBase weight = ObjectManager.getPotionEffect("weight");
		if(weight != null) {
			if(!invulnerable && entity.isPotionActive(weight) && !entity.isPotionActive(MobEffects.STRENGTH)) {
				if(entity.motionY > -0.2D)
					entity.motionY = -0.2D;
			}
		}
		
		// Fear
		PotionBase fear = ObjectManager.getPotionEffect("fear");
		if(fear != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(fear)) {
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

		// Instability
		PotionBase instability = ObjectManager.getPotionEffect("instability");
		if(instability != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(instability)) {
				if(entity.getEntityWorld().rand.nextDouble() <= 0.1) {
					double strength = 1 + entity.getActivePotionEffect(instability).getAmplifier();
					entity.motionX += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					entity.motionY += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					entity.motionZ += strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D);
					try {
						if (entity instanceof EntityPlayerMP) {
							EntityPlayerMP player = (EntityPlayerMP) entity;
							player.connection.sendPacket(new SPacketEntityVelocity(entity));
							MessageEntityVelocity messageEntityVelocity = new MessageEntityVelocity(
									player,
									strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D),
									strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D),
									strength * (entity.getEntityWorld().rand.nextDouble() - 0.5D)
							);
							LycanitesMobs.packetHandler.sendToPlayer(messageEntityVelocity, player);
						}
					}
					catch(Exception e) {
						LycanitesMobs.printWarning("", "Failed to create and send a network packet for instability velocity!");
						e.printStackTrace();
					}
				}
			}
		}

		// Plague
		PotionBase plague = ObjectManager.getPotionEffect("plague");
		if(plague != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(plague)) {

				// Poison:
				int poisonAmplifier = entity.getActivePotionEffect(plague).getAmplifier();
				int poisonDuration = entity.getActivePotionEffect(plague).getDuration();
				if(entity.isPotionActive(MobEffects.POISON)) {
					poisonAmplifier = Math.max(poisonAmplifier, entity.getActivePotionEffect(MobEffects.POISON).getAmplifier());
					poisonDuration = Math.max(poisonDuration, entity.getActivePotionEffect(MobEffects.POISON).getDuration());
				}
				entity.addPotionEffect(new PotionEffect(MobEffects.POISON, poisonDuration, poisonAmplifier));

				// Spread:
				if(entity.getEntityWorld().getTotalWorldTime() % 20 == 0) {
					List aoeTargets = this.getNearbyEntities(entity, EntityLivingBase.class, null, 10);
					for(Object entityObj : aoeTargets) {
						EntityLivingBase target = (EntityLivingBase)entityObj;
						if(target != entity && !entity.isOnSameTeam(target)) {
							int amplifier = entity.getActivePotionEffect(plague).getAmplifier();
							int duration = entity.getActivePotionEffect(plague).getDuration();
							if(amplifier > 0) {
								target.addPotionEffect(new PotionEffect(plague, duration, amplifier - 1));
							}
							else {
								target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, amplifier));
							}
						}
					}
				}
			}
		}

		// Smited
		PotionBase smited = ObjectManager.getPotionEffect("smited");
		if(smited != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(smited) && entity.getEntityWorld().getTotalWorldTime() % 20 == 0) {
				float brightness = entity.getBrightness();
				if(brightness > 0.5F && entity.getEntityWorld().canBlockSeeSky(entity.getPosition())) {
					entity.setFire(4);
				}
			}
		}

		// Smouldering
		PotionBase smouldering = ObjectManager.getPotionEffect("smouldering");
		if(smouldering != null && !entity.getEntityWorld().isRemote) {
			if(!invulnerable && entity.isPotionActive(smouldering) && entity.getEntityWorld().getTotalWorldTime() % 20 == 0) {
				entity.setFire(4 + (4 * entity.getActivePotionEffect(smouldering).getAmplifier()));
			}
		}


		// ========== Buffs ==========
		// Swiftswimming
		PotionBase swiftswimming = ObjectManager.getPotionEffect("swiftswimming");
		if(swiftswimming != null && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			if(entity.isPotionActive(swiftswimming) && entity.isInWater()) {
				int amplifier = entity.getActivePotionEffect(swiftswimming).getAmplifier();
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

		// Immunisation
		PotionBase immunization = ObjectManager.getPotionEffect("immunization");
		if(immunization != null && !entity.getEntityWorld().isRemote) {
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

		// Cleansed
		PotionBase cleansed = ObjectManager.getPotionEffect("cleansed");
		if(ObjectManager.getPotionEffect("cleansed") != null && !entity.getEntityWorld().isRemote) {
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
		if(invulnerable) {
			return;
		}
			
		// Anti-Jumping:
		PotionBase paralysis = ObjectManager.getPotionEffect("paralysis");
		if(paralysis != null) {
			if(entity.isPotionActive(paralysis)) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}

		PotionBase weight = ObjectManager.getPotionEffect("weight");
		if(weight != null) {
			if(entity.isPotionActive(weight)) {
				if(event.isCancelable()) event.setCanceled(true);
			}
		}
	}


	// ==================================================
	//               Living Attack Event
	// ==================================================
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event) {
		if(event.isCancelable() && event.isCanceled())
			return;

		if(event.getEntityLiving() == null)
			return;

		EntityLivingBase target = event.getEntityLiving();
		EntityLivingBase attacker = null;
		if(event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
			attacker = (EntityLivingBase) event.getSource().getTrueSource();
		}
		if(attacker == null) {
			return;
		}

		// ========== Debuffs ==========
		// Lifeleak
		PotionBase lifeleak = ObjectManager.getPotionEffect("lifeleak");
		if(lifeleak != null && !event.getEntityLiving().getEntityWorld().isRemote) {
			if(attacker.isPotionActive(lifeleak)) {
				if (event.isCancelable()) {
					event.setCanceled(true);
				}
				target.heal(event.getAmount());
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


		// ========== Debuffs ==========
        // Fall Resistance
		PotionBase fallresist = ObjectManager.getPotionEffect("fallresist");
		if(fallresist != null) {
            if(event.getEntityLiving().isPotionActive(fallresist)) {
                if("fall".equals(event.getSource().damageType)) {
                    event.setAmount(0);
                    event.setCanceled(true);
                }
            }
        }

		// Penetration
		PotionBase penetration = ObjectManager.getPotionEffect("penetration");
		if(penetration != null) {
			if(event.getEntityLiving().isPotionActive(penetration)) {
				float damage = event.getAmount();
				float multiplier = event.getEntityLiving().getActivePotionEffect(penetration).getAmplifier();
				event.setAmount(damage + (damage * multiplier));
			}
		}

		// Fear
		PotionBase fear = ObjectManager.getPotionEffect("fear");
		if(fear != null) {
			if(event.getEntityLiving().isPotionActive(fear)) {
				if("inWall".equals(event.getSource().damageType)) {
					event.setAmount(0);
					event.setCanceled(true);
				}
			}
		}


        // ========== Buffs ==========
        // Leeching
		PotionBase leech = ObjectManager.getPotionEffect("leech");
		if(leech != null && event.getSource().getTrueSource() != null) {
            if(event.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attackingEntity = (EntityLivingBase)(event.getSource().getTrueSource());
                if(attackingEntity.isPotionActive(leech)) {
                    float damage = event.getAmount();
                    float multiplier = attackingEntity.getActivePotionEffect(leech).getAmplifier();
                    attackingEntity.heal(damage * multiplier);
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
				event.setAmount((float)Math.ceil(event.getAmount() * (2 * (1 + entity.getActivePotionEffect(rejuvenation).getAmplifier()))));
			}
		}

		// Decay:
		PotionBase decay = ObjectManager.getPotionEffect("decay");
		if(decay != null) {
			if(entity.isPotionActive(decay)) {
				event.setAmount((float)Math.floor(event.getAmount() / (2 * (1 + entity.getActivePotionEffect(decay).getAmplifier()))));
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
		if(player == null || player.getEntityWorld().isRemote || event.isCanceled())
			return;

		// Insomnia:
		PotionBase insomnia = ObjectManager.getPotionEffect("insomnia");
		if(insomnia != null && player.isPotionActive(insomnia)) {
			event.setResult(EntityPlayer.SleepResult.NOT_SAFE);
		}
	}


	// ==================================================
	//               Item Use Event
	// ==================================================
	@SubscribeEvent
	public void onLivingUseItem(LivingEntityUseItemEvent event) {
		if(event.isCancelable() && event.isCanceled())
			return;

		if(event.getEntityLiving() == null)
			return;

		// ========== Debuffs ==========
		// Aphagia
		PotionBase aphagia = ObjectManager.getPotionEffect("aphagia");
		if(aphagia != null && !event.getEntityLiving().getEntityWorld().isRemote) {
			if(event.getEntityLiving().isPotionActive(aphagia)) {
				if(event.isCancelable()) {
					event.setCanceled(true);
				}
			}
		}
	}


	// ==================================================
	//                     Utility
	// ==================================================
	/** Get entities that are near the provided entity. **/
	public <T extends Entity> List<T> getNearbyEntities(Entity searchEntity, Class <? extends T > clazz, final Class filterClass, double range) {
		return searchEntity.getEntityWorld().getEntitiesWithinAABB(clazz, searchEntity.getEntityBoundingBox().grow(range, range, range), (Predicate<Entity>) entity -> {
			if(filterClass == null)
				return true;
			return filterClass.isAssignableFrom(entity.getClass());
		});
	}
}
