package lycanite.lycanitesmobs.api.gui;

import lycanite.lycanitesmobs.*;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.ai.EntityAIFollow;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.api.mobevent.MobEventClient;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

public class GuiOverlay extends Gui {
	public Minecraft mc;
	
	private int mountMessageTimeMax = 10 * 20;
	private int mountMessageTime = 0;
	
	private Field executingTasks;
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public GuiOverlay(Minecraft minecraft) {
		this.mc = minecraft;
		tryAIreflection();
	}

	// Attempt to find the method containing the executing tasks for an entity's AI.
	// The reflection will work on both Sides but the AI only runs on the server.
	private void tryAIreflection()
	{
		try {
			executingTasks = EntityAITasks.class.getDeclaredField("field_75780_b");
			executingTasks.setAccessible(true);		
		} catch (Exception e) {
			try {
				executingTasks = EntityAITasks.class.getDeclaredField("executingTaskEntries");
				executingTasks.setAccessible(true);		
			} catch (Exception e1) {
				FMLLog.getLogger().log(Level.ERROR, "Unable to find \"executingTaskEntries\" field in GuiOverlay. Some debug information will not be available");
			}
		}
	}
	
    // ==================================================
    //                  Draw Game Overlay
    // ==================================================
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
        if(LycanitesMobs.proxy.getClientPlayer() == null)
            return;
        EntityPlayer player = LycanitesMobs.proxy.getClientPlayer();

		if(event.isCancelable() || event.type != ElementType.EXPERIENCE)
	      return;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
		int sWidth = scaledresolution.getScaledWidth();
        int sHeight = scaledresolution.getScaledHeight();

        // ========== Mob/World Events Title ==========
        ExtendedWorld worldExt = ExtendedWorld.getForWorld(player.worldObj);
        if(worldExt != null) {
            for(MobEventClient mobEventClient : worldExt.clientMobEvents.values())
                mobEventClient.onGUIUpdate(this, sWidth, sHeight);
            if(worldExt.clientWorldEvent != null)
                worldExt.clientWorldEvent.onGUIUpdate(this, sWidth, sHeight);
        }
		
		// ========== Summoning Focus Bar ==========
        ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt != null && !this.mc.thePlayer.capabilities.isCreativeMode
				&& this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemStaffSummoning) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(AssetManager.getTexture("GUIInventoryCreature"));
			
			int barYSpace = 10;
			int barXSpace = -1;
			
            int summonBarWidth = 9;
            int summonBarHeight = 9;
            int summonBarX = (sWidth / 2) + 10;
            int summonBarY = sHeight - 30 - summonBarHeight;
            int summonBarU = 256 - summonBarWidth;
            int summonBarV = 256 - summonBarHeight;
            
            summonBarY -= barYSpace;
            if(this.mc.thePlayer.isInsideOfMaterial(Material.water))
            	summonBarY -= barYSpace;
            
            for(int summonBarEnergyN = 0; summonBarEnergyN < 10; summonBarEnergyN++) {
            	this.drawTexturedModalRect(summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU, summonBarV, summonBarWidth, summonBarHeight);
            	if(playerExt.summonFocus >= playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
                	this.drawTexturedModalRect(summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN), summonBarY, summonBarU - summonBarWidth, summonBarV, summonBarWidth, summonBarHeight);
            	}
                else if(playerExt.summonFocus + playerExt.summonFocusCharge > playerExt.summonFocusMax - (summonBarEnergyN * playerExt.summonFocusCharge)) {
            		float summonChargeScale = (float)(playerExt.summonFocus % playerExt.summonFocusCharge) / (float)playerExt.summonFocusCharge;
            		this.drawTexturedModalRect((summonBarX + ((summonBarWidth + barXSpace) * summonBarEnergyN)) + (summonBarWidth - Math.round((float)summonBarWidth * summonChargeScale)), summonBarY, summonBarU - Math.round((float)summonBarWidth * summonChargeScale), summonBarV, Math.round((float)summonBarWidth * summonChargeScale), summonBarHeight);
            	}
            }
		}
		
		// ========== Mount Stamina Bar ==========
		if(this.mc.thePlayer.ridingEntity != null && this.mc.thePlayer.ridingEntity instanceof EntityCreatureRideable) {
			EntityCreatureRideable mount = (EntityCreatureRideable)this.mc.thePlayer.ridingEntity;
            float mountStamina = mount.getStaminaPercent();
            
            // Mount Controls Message:
            if(this.mountMessageTime > 0) {
            	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            	if(this.mountMessageTime < 60)
            		GL11.glColor4f(1.0F, 1.0F, 1.0F, (float)this.mountMessageTime / (float)60);
            	String mountMessage = StatCollector.translateToLocal("gui.mount.controls");
            	mountMessage = mountMessage.replace("%control%", GameSettings.getKeyDisplayString(KeyHandler.instance.mountAbility.getKeyCode()));
            	int stringWidth = this.mc.fontRenderer.getStringWidth(mountMessage);
            	this.mc.fontRenderer.drawString(mountMessage, (sWidth / 2) - (stringWidth / 2), sHeight - 64, 0xFFFFFF);
            }
            
            // Mount Ability Stamina Bar:
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.getTextureManager().bindTexture(Gui.icons);
            int staminaBarWidth = 182;
            int staminaBarHeight = 5;
            int staminaEnergyWidth = (int)((float)(staminaBarWidth + 1) * mountStamina);
            int staminaBarX = (sWidth / 2) - (staminaBarWidth / 2);
            int staminaBarY = sHeight - 32 + 3;
            int staminaTextureY = 84;
            if("toggle".equals(mount.getStaminaType()))
            	staminaTextureY -= staminaBarHeight * 2;
            int staminaEnergyY = staminaTextureY + staminaBarHeight;
            
            this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaTextureY, staminaBarWidth, staminaBarHeight);
            if(staminaEnergyWidth > 0)
                this.drawTexturedModalRect(staminaBarX, staminaBarY, 0, staminaEnergyY, staminaEnergyWidth, staminaBarHeight);
            
            if(this.mountMessageTime > 0)
            	this.mountMessageTime--;
		}
		else
			this.mountMessageTime = this.mountMessageTimeMax;

        GL11.glPopMatrix();
		this.mc.getTextureManager().bindTexture(Gui.icons);
	}
	
	//Last event called before the screen is drawn
	@SubscribeEvent
	public void onRenderDebugInfo(RenderGameOverlayEvent.Text event) 
	{
        if (this.mc.gameSettings.showDebugInfo)
        {
	        event.right.add(null);
	        event.right.add((char)167 +"nLycanitesMobs (" + LycanitesMobs.version + ")");
	        if (LycanitesMobs.debugEntity == null || LycanitesMobs.debugEntity.isDead)
	        {
	            event.right.add("Right click on a mob to view debug info.");
	        }
	        else
	        {
	            EntityLivingBase entity = (EntityLivingBase)LycanitesMobs.debugEntity;	  
	            
	            //Type
	            event.right.add("Entity: " + entity.getCommandSenderName() + " [" + entity.getEntityId() + "]");
	            //Health
	            if (entity instanceof EntityCreatureBase)
	            {
	            	EntityCreatureBase ecb = (EntityCreatureBase)entity;
		            event.right.add("Attack/Defense/Health: " + ecb.getAttackDamage(1.0f) + "/" + ecb.defense +"/" + entity.getHealth());	            	            	
	            }
	            else
	            {
		            event.right.add("Attack/Defense/Health: " + (float)entity.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue()  + entity.getHealth());	            	            		            	
	            }
	            //AI Task
	            if(entity.isClientWorld() && executingTasks != null && entity instanceof EntityLiving)  //isClientWorld should be called isServerWorld
	            {
					try {
		            	ArrayList<EntityAITasks.EntityAITaskEntry> tasks;
						EntityLiving el = (EntityLiving)entity;
						tasks = (ArrayList)executingTasks.get(el.tasks);
		            	String s = "";
		            	if (tasks.size() == 0)
		            	{
				            event.right.add("Main Task: idle");	            		
		            	}
		            	else
		            	{
		            		for (EntityAITasks.EntityAITaskEntry ai : tasks) 
		            		{
					            event.right.add("Main Task: " + ai.action.getClass().getSimpleName());	            		
		            		}		            		
		            	}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }

	            //AI Task
	            if(entity.isClientWorld() && executingTasks != null && entity instanceof EntityLiving)
	            {
	            	ArrayList tasks;
					try {
						EntityLiving el = (EntityLiving)entity;
						tasks = (ArrayList)executingTasks.get(el.targetTasks);
		            	String s = "";
		            	if (tasks.size() == 0)
		            	{
		            		s = "idle";
		            	}
		            	else
		            	{
		            		EntityAITasks.EntityAITaskEntry ai = (EntityAITasks.EntityAITaskEntry)tasks.get(0);
		            		s = ai.action.getClass().getSimpleName();	            	
		            	}
			            event.right.add("Target Task: " + s);	            		
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }

	            int x = MathHelper.floor_double(entity.posX);
	            int y = MathHelper.floor_double(entity.posY);
	            int z = MathHelper.floor_double(entity.posZ);
	            int rot = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
	            int rot1 = MathHelper.floor_double((double)(entity.renderYawOffset * 4.0F / 360.0F) + 0.5D) & 3;

		        event.right.add(String.format("x: %.5f (%d) // c: %d (%d)", new Object[] {Double.valueOf(entity.posX), Integer.valueOf(x), Integer.valueOf(x >> 4), Integer.valueOf(x & 15)}));
		        event.right.add(String.format("y: %.3f (feet pos, %.3f eyes pos)", new Object[] {Double.valueOf(entity.boundingBox.minY), Double.valueOf(entity.posY)}));
		        event.right.add(String.format("z: %.5f (%d) // c: %d (%d)", new Object[] {Double.valueOf(entity.posZ), Integer.valueOf(z), Integer.valueOf(z >> 4), Integer.valueOf(z & 15)}));
		        event.right.add("Yaw: " + rot + " (" + Direction.directions[rot] + ") / " + MathHelper.wrapAngleTo180_float(entity.rotationYaw));
		        event.right.add("YawOffset: " + rot1 + " (" + Direction.directions[rot1] + ") / " + MathHelper.wrapAngleTo180_float(entity.renderYawOffset));
            
//            this.drawRightAlignedString(fontrenderer, String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", new Object[] {Float.valueOf(entity.capabilities.getWalkSpeed()), Float.valueOf(entity.capabilities.getFlySpeed()), Boolean.valueOf(entity.onGround), Integer.valueOf(this.mc.theWorld.getHeightValue(x, z))}), width - 2, 104);
	        }


        }
	}
	
}
