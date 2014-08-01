package lycanite.lycanitesmobs.demonmobs.model;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.model.obj.WavefrontObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelNetherSoul extends ModelCustomObj {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelNetherSoul() {
        this(1.0F);
    }
    
    public ModelNetherSoul(float shadowSize) {
    	// Load Model:
    	model = (WavefrontObject)AssetManager.getObjModel("NetherSoul", DemonMobs.group, "entity/nethersoul");
    	
    	// Get Parts:
    	parts = model.groupObjects;
    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.5F, 0F);
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
		
    	// Apply Animations:
    	rotate(rotation, angleX, angleY, angleZ);
    	rotate(rotX, rotY, rotZ);
    	translate(posX, posY, posZ);
    }
}
