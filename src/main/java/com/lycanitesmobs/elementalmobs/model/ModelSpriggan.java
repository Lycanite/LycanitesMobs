package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSpriggan extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelSpriggan() {
        this(1.0F);
    }

    public ModelSpriggan(float shadowSize) {
    	// Load Model:
    	this.initModel("spriggan", ElementalMobs.instance.group, "entity/spriggan");
    	
    	// Trophy:
        this.trophyScale = 1.2F;
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		float rotX = 0F;
		float rotY = 0F;
		float rotZ = 0F;

		// Effects:
		if(partName.equals("effectouter"))
			rotX = 20F;
		if(partName.equals("effectinner"))
			rotX = -20F;

		// Apply Animations:
		this.rotate(rotX, rotY, rotZ);

    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
    }
}
