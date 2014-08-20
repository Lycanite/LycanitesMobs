package lycanite.lycanitesmobs.api;

import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public interface ILycanEventListener {
	public void onEntityConstructing(EntityConstructing event);
	public void onEntityJoinWorld(EntityJoinWorldEvent event);
	public void onLivingDeathEvent(LivingDeathEvent event);
	public void onEntityUpdate(LivingUpdateEvent event);
	public void onEntityInteract(EntityInteractEvent event);
	public void onAttackTarget(LivingSetAttackTargetEvent event);
	public void onLivingHurt(LivingHurtEvent event);
	public void onBucketFill(FillBucketEvent event);
}
