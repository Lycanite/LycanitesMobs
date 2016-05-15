package lycanite.lycanitesmobs.api.render;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderCreature extends RenderLiving {
	
	/** A color table for mobs that can be dyed or pet collars. Follows the same pattern as the vanilla sheep. */
	public static final float[][] colorTable = new float[][] {{1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F}, {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F}, {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F}, {0.4F, 0.3F, 0.2F}, {0.4F, 0.5F, 0.2F}, {0.6F, 0.2F, 0.2F}, {0.1F, 0.1F, 0.1F}};
    
	/** Enchanted glint effect texture. **/
	private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public RenderCreature(String entityID, RenderManager renderManager) {
    	super(renderManager, AssetManager.getModel(entityID), 0.5F);
    }
    
    
    // ==================================================
 	//                      Render
 	// ==================================================
    /**
    * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
    * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
    * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
    * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
    */
    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks) {
        //if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<T>(entity, this, x, y, z)))
        //    return;
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

    	/*EntityLivingBase par1EntityLivingBase = (EntityLivingBase)entityLiving;
       if(MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(par1EntityLivingBase, this, par2, par4, par6))) return;
       GL11.glPushMatrix();
       GL11.glDisable(GL11.GL_CULL_FACE);
       this.mainModel.onGround = this.renderSwingProgress(par1EntityLivingBase, par9);

       if(this.renderPassModel != null)
           this.renderPassModel.onGround = this.mainModel.onGround;

       this.mainModel.isRiding = par1EntityLivingBase.isRiding();

       if(this.renderPassModel != null)
           this.renderPassModel.isRiding = this.mainModel.isRiding;

       this.mainModel.isChild = par1EntityLivingBase.isChild();

       if(this.renderPassModel != null)
           this.renderPassModel.isChild = this.mainModel.isChild;

       try {
           float f2 = this.interpolateRotation(par1EntityLivingBase.prevRenderYawOffset, par1EntityLivingBase.renderYawOffset, par9);
           float f3 = this.interpolateRotation(par1EntityLivingBase.prevRotationYawHead, par1EntityLivingBase.rotationYawHead, par9);
           float f4;

           if(par1EntityLivingBase.isRiding() && par1EntityLivingBase.ridingEntity instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase1 = (EntityLivingBase)par1EntityLivingBase.ridingEntity;
               f2 = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, par9);
               f4 = MathHelper.wrapAngleTo180_float(f3 - f2);

               if (f4 < -85.0F)
                   f4 = -85.0F;

               if (f4 >= 85.0F)
                   f4 = 85.0F;

               f2 = f3 - f4;

               if (f4 * f4 > 2500.0F)
                   f2 += f4 * 0.2F;
           }

           float f13 = par1EntityLivingBase.prevRotationPitch + (par1EntityLivingBase.rotationPitch - par1EntityLivingBase.prevRotationPitch) * par9;
           this.renderLivingAt(par1EntityLivingBase, par2, par4, par6);
           f4 = this.handleRotationFloat(par1EntityLivingBase, par9);
           this.rotateCorpse(par1EntityLivingBase, f4, f2, par9);
           float f5 = 0.0625F;
           GL11.glEnable(GL12.GL_RESCALE_NORMAL);
           GL11.glScalef(-1.0F, -1.0F, 1.0F);
           this.preRenderCallback(par1EntityLivingBase, par9);
           GL11.glTranslatef(0.0F, -24.0F * f5 - 0.0078125F, 0.0F);
           float f6 = par1EntityLivingBase.prevLimbSwingAmount + (par1EntityLivingBase.limbSwingAmount - par1EntityLivingBase.prevLimbSwingAmount) * par9;
           float f7 = par1EntityLivingBase.limbSwing - par1EntityLivingBase.limbSwingAmount * (1.0F - par9);

           if (par1EntityLivingBase.isChild())
               f7 *= 3.0F;

           if (f6 > 1.0F)
               f6 = 1.0F;

           GL11.glEnable(GL11.GL_ALPHA_TEST);
           this.mainModel.setLivingAnimations(par1EntityLivingBase, f7, f6, par9);
           this.renderModel(par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
           int j;
           float f8;
           float f9;
           float f10;

           for (int i = 0; i < 4; ++i) {
               j = this.shouldRenderPass(par1EntityLivingBase, i, par9);

               if (j > 0) {
                   this.renderPassModel.setLivingAnimations(par1EntityLivingBase, f7, f6, par9);
                   this.renderPassModel(this.renderPassModel, par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);

                   if ((j & 240) == 16) {
                       this.func_82408_c(par1EntityLivingBase, i, par9);
                       this.renderPassModel(this.renderPassModel, par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                   }

                   if ((j & 15) == 15) {
                       f8 = (float)par1EntityLivingBase.ticksExisted + par9;
                       this.bindTexture(RES_ITEM_GLINT);
                       GL11.glEnable(GL11.GL_BLEND);
                       f9 = 0.5F;
                       GL11.glColor4f(f9, f9, f9, 1.0F);
                       GL11.glDepthFunc(GL11.GL_EQUAL);
                       GL11.glDepthMask(false);

                       for (int k = 0; k < 2; ++k) {
                           GL11.glDisable(GL11.GL_LIGHTING);
                           f10 = 0.76F;
                           GL11.glColor4f(0.5F * f10, 0.25F * f10, 0.8F * f10, 1.0F);
                           GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                           GL11.glMatrixMode(GL11.GL_TEXTURE);
                           GL11.glLoadIdentity();
                           float f11 = f8 * (0.001F + (float)k * 0.003F) * 20.0F;
                           float f12 = 0.33333334F;
                           GL11.glScalef(f12, f12, f12);
                           GL11.glRotatef(30.0F - (float)k * 60.0F, 0.0F, 0.0F, 1.0F);
                           GL11.glTranslatef(0.0F, f11, 0.0F);
                           GL11.glMatrixMode(GL11.GL_MODELVIEW);
                           this.renderPassModel(this.renderPassModel, par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                       }

                       GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                       GL11.glMatrixMode(GL11.GL_TEXTURE);
                       GL11.glDepthMask(true);
                       GL11.glLoadIdentity();
                       GL11.glMatrixMode(GL11.GL_MODELVIEW);
                       GL11.glEnable(GL11.GL_LIGHTING);
                       GL11.glDisable(GL11.GL_BLEND);
                       GL11.glDepthFunc(GL11.GL_LEQUAL);
                   }

                   GL11.glDisable(GL11.GL_BLEND);
                   GL11.glEnable(GL11.GL_ALPHA_TEST);
               }
           }

           GL11.glDepthMask(true);
           this.renderEquippedItems(par1EntityLivingBase, par9);
           float f14 = par1EntityLivingBase.getBrightness(par9);
           j = this.getColorMultiplier(par1EntityLivingBase, f14, par9);
           OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
           GL11.glDisable(GL11.GL_TEXTURE_2D);
           OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
           
           
           // Damage Coloring:
           if ((j >> 24 & 255) > 0 || par1EntityLivingBase.hurtTime > 0 || par1EntityLivingBase.deathTime > 0) {
               GL11.glDisable(GL11.GL_TEXTURE_2D);
               GL11.glDisable(GL11.GL_ALPHA_TEST);
               GL11.glEnable(GL11.GL_BLEND);
               GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
               GL11.glDepthFunc(GL11.GL_EQUAL);

               if (par1EntityLivingBase.hurtTime > 0 || par1EntityLivingBase.deathTime > 0) {
                   GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                   if(this.mainModel instanceof ModelCustomObj)
                	   ((ModelCustomObj)this.mainModel).dontColor = true;
                   this.renderModel(par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                   if(this.mainModel instanceof ModelCustomObj)
                	   ((ModelCustomObj)this.mainModel).dontColor = false;

                   for (int l = 0; l < 4; ++l) {
                       if (this.inheritRenderPass(par1EntityLivingBase, l, par9) >= 0) {
                           GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                           if(this.renderPassModel instanceof ModelCustomObj)
                        	   ((ModelCustomObj)this.renderPassModel).dontColor = true;
                           this.renderPassModel(this.renderPassModel, par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                           if(this.renderPassModel instanceof ModelCustomObj)
                        	   ((ModelCustomObj)this.renderPassModel).dontColor = false;
                       }
                   }
               }

               if ((j >> 24 & 255) > 0) {
                   f8 = (float)(j >> 16 & 255) / 255.0F;
                   f9 = (float)(j >> 8 & 255) / 255.0F;
                   float f15 = (float)(j & 255) / 255.0F;
                   f10 = (float)(j >> 24 & 255) / 255.0F;
                   GL11.glColor4f(f8, f9, f15, f10);
                   this.renderModel(par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);

                   for (int i1 = 0; i1 < 4; ++i1) {
                       if (this.inheritRenderPass(par1EntityLivingBase, i1, par9) >= 0) {
                           GL11.glColor4f(f8, f9, f15, f10);
                           this.renderPassModel(this.renderPassModel, par1EntityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                       }
                   }
               }

               GL11.glDepthFunc(GL11.GL_LEQUAL);
               GL11.glDisable(GL11.GL_BLEND);
               GL11.glEnable(GL11.GL_ALPHA_TEST);
               GL11.glEnable(GL11.GL_TEXTURE_2D);
           }

           GL11.glDisable(GL12.GL_RESCALE_NORMAL);
       }
       catch (Exception exception) {
           LycanitesMobs.printWarning("", "A problem occured when rendering the entity " + par1EntityLivingBase + ":");
           exception.printStackTrace();
       }

       OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
       GL11.glEnable(GL11.GL_TEXTURE_2D);
       OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
       GL11.glEnable(GL11.GL_CULL_FACE);
       GL11.glPopMatrix();
       this.passSpecialRender(par1EntityLivingBase, par2, par4, par6);
       MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(par1EntityLivingBase, this, par2, par4, par6));
       
       // Render Leash:
       if(par1EntityLivingBase instanceof EntityLiving)
    	   this.func_110827_b((EntityLiving)par1EntityLivingBase, par2, par4, par6, par8, par9);*/
   }
    
    
    // ==================================================
 	//                  Render Equipment
 	// ==================================================
    @SuppressWarnings("unused") //TODO Collar textures.
	protected int shouldRenderPass(EntityLivingBase entity, int renderPass, float partialTick) {
    	if(!(entity instanceof EntityCreatureBase))
    		return -1;
    	EntityCreatureBase creature = (EntityCreatureBase)entity;
    	
    	// Chest/Body Armor First:
    	if(renderPass == 0 && creature.getEquipmentName("chest") != null) {
			this.bindEquipmentTexture(entity, creature.getEquipmentName("chest"));
    		return 1;
    	}
    	
    	// Saddle Second:
    	if(renderPass == 1 && creature instanceof EntityCreatureRideable)
    		if(((EntityCreatureRideable)creature).hasSaddle()) {
    			this.bindEquipmentTexture(entity, "saddle");
    			return 1;
    		}
    	
    	// Feet/Collar/Color Third:
    	if(renderPass == 2) {
    		if(creature.canBeColored(null) && false) {
    			this.bindEntityTexture(entity);
    			int colorID = 0;
    			if(entity instanceof EntityCreatureBase)
    				colorID = ((EntityCreatureBase)entity).getColor();
    			GL11.glColor3f(colorTable[colorID][0], colorTable[colorID][1], colorTable[colorID][2]);
    			// Future collar texture overlays can be added here and should be colored.
    			return 1;
    		}
    		else if(creature.getEquipmentName("feet") != null) {
				this.bindEquipmentTexture(entity, creature.getEquipmentName("feet"));
	    		return 1;
    		}
    	}
    	
    	// Helm Fourth:
    	if(renderPass == 3 && creature.getEquipmentName("head") != null) {
			this.bindEquipmentTexture(entity, creature.getEquipmentName("head"));
    		return 1;
    	}
    	
    	return -1;
    }
    
    
    /**
     * Renders the model in RenderLiving
     */
    protected void renderPassModel(ModelBase model, EntityLivingBase entityLivingBase, float time, float distance, float loop, float lookX, float lookY, float scale) {
        if (!entityLivingBase.isInvisible())
        	model.render(entityLivingBase, time, distance, loop, lookX, lookY, scale);
        else if (!entityLivingBase.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.15F);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
            model.render(entityLivingBase, time, distance, loop, lookX, lookY, scale);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glPopMatrix();
            GL11.glDepthMask(true);
        }
        else
        	model.setRotationAngles(time, distance, loop, lookX, lookY, scale, entityLivingBase);
    }
    
    
    // ==================================================
 	//                     Visuals
 	// ==================================================
    // ========== Main ==========
    @Override
    protected boolean bindEntityTexture(Entity entity) {
        ResourceLocation texture = this.getEntityTexture(entity);
        if(texture == null)
            return false;
        this.bindTexture(texture);
        return true;
    }
    
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
    	if(entity instanceof EntityCreatureBase)
    		return ((EntityCreatureBase)entity).getTexture();
        return null;
    }
    
    // ========== Equipment ==========
    protected void bindEquipmentTexture(Entity entity, String equipmentName) {
        this.bindTexture(this.getEquipmentTexture(entity, equipmentName));
    }
    
    protected ResourceLocation getEquipmentTexture(Entity entity, String equipmentName) {
    	if(entity instanceof EntityCreatureBase)
    		return ((EntityCreatureBase)entity).getEquipmentTexture(equipmentName);
        return null;
    }
    
    // ========== Bind ==========
    @Override
    public void bindTexture(ResourceLocation texture) {
    	this.renderManager.renderEngine.bindTexture(texture);
    }
    
    
    // ==================================================
  	//                     Effects
  	// ==================================================
    @Override
    protected void preRenderCallback(EntityLivingBase entity, float particleTickTime) {
        // No effects.
    }
    
    /** If true, display the name of the entity above it. **/
    @Override
    protected boolean canRenderName(EntityLiving renderEntity) {
    	if(!Minecraft.isGuiEnabled()) return false;
    	if(renderEntity == this.renderManager.livingPlayer) return false;
    	if(renderEntity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer)) return false;
    	if(renderEntity.getControllingPassenger() != null) return false;
    	
    	if(renderEntity.getAlwaysRenderNameTagForRender()) {
    		if(renderEntity instanceof EntityCreatureTameable)
    			if(((EntityCreatureTameable)renderEntity).isTamed())
    				return renderEntity == this.renderManager.pointedEntity;
    		return true;
    	}
    	
    	return renderEntity.hasCustomName() && renderEntity == this.renderManager.pointedEntity;
    }
    
    
    // ==================================================
  	//                     Tools
  	// ==================================================
    /**
    * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
    * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
    * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
    */
    public float interpolateRotation(float par1, float par2, float par3) {
       float f3;

       for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F) {}

       while (f3 >= 180.0F) {
           f3 -= 360.0F;
       }

       return par1 + par3 * f3;
   }
}
