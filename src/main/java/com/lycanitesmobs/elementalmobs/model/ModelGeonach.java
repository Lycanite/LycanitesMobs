package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGeonach extends ModelTemplateElemental {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelGeonach() {
        this(1.0F);
    }
    
    public ModelGeonach(float shadowSize) {

		// Load Model:
		this.initModel("geonach", ElementalMobs.instance.group, "entity/geonach");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
		this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }
}
