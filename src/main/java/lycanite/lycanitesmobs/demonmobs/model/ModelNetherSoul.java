package lycanite.lycanitesmobs.demonmobs.model;

import lycanite.lycanitesmobs.core.model.ModelCustomObj;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    	this.initModel("NetherSoul", DemonMobs.group, "entity/nethersoul");
    	


    	
    	// Set Rotation Centers:
    	setPartCenter("head", 0F, 0.5F, 0F);

        // Trophy:
        this.trophyScale = 0.8F;
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
