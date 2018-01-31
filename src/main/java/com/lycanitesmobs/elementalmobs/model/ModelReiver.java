package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelReiver extends ModelTemplateElemental {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelReiver() {
        this(1.0F);
    }
    
    public ModelReiver(float shadowSize) {

    	// Load Model:
    	this.initModel("reiver", ElementalMobs.instance.group, "entity/reiver");

        // Tropy:
        this.trophyScale = 1.0F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.2F};
    }
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		// Effect:
		if(partName.contains("effect")) {
			this.rotate(25, 0, 0);
		}

    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
    }
}
