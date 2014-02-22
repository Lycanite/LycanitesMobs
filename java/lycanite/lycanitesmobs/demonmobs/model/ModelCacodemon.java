package lycanite.lycanitesmobs.demonmobs.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import lycanite.lycanitesmobs.api.model.ModelBipedCustom;
import lycanite.lycanitesmobs.api.model.ModelCustom;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

@SideOnly(Side.CLIENT)
public class ModelCacodemon extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCacodemon() {
        this(1.0F);
    }
    
    public ModelCacodemon(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("Cacodemon", DemonMobs.domain, "entity/cacodemon");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 1.0F, 0F);
    	setPartCenter("topmouth", 0F, 1.0F, 0F);
    	setPartCenter("bottommouth", 0F, 1.0F, 0F);
    	
    	this.lockHeadX = true;
    	this.lockHeadY = true;
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
    	float pi = (float)Math.PI;
    	float posX = 0F;
    	float posY = 0F;
    	float posZ = 0F;
    	float angleX = 0F;
    	float angleY = 0F;
    	float angleZ = 0F;
    	float rotation = 0F;
    	float rotX = 0F;
    	float rotY = 0F;
    	float rotZ = 0F;
    	
    	// Look:
    	rotX += Math.toDegrees(lookX / (180F / (float)Math.PI));
    	rotY += Math.toDegrees(lookY / (180F / (float)Math.PI));
		
    	// Mouth:
    	if(partName.equals("topmouth"))
			rotX -= 5F;
		if(partName.equals("bottommouth"))
			rotX += 5F;
    	
		// Attack:
    	if((entity instanceof EntityCreatureBase && ((EntityCreatureBase)entity).justAttacked())
    			|| (entity instanceof EntityCreatureAgeable && ((EntityCreatureAgeable)entity).isInLove())) {
			if(partName.equals("topmouth"))
				rotX -= 25F;
			if(partName.equals("bottommouth"))
				rotX += 25F;
			rotX += 25F / 2F;
		}
		
		// Sit:
    	if((entity instanceof EntityCreatureTameable && ((EntityCreatureTameable)entity).isSitting())) {
			if(partName.equals("topmouth"))
				rotX += 5F;
			if(partName.equals("bottommouth"))
				rotX -= 5F;
		}
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
