package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelVolcan extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelVolcan() {
        this(1.0F);
    }

    public ModelVolcan(float shadowSize) {

		// Load Model:
		this.initModel("volcan", ElementalMobs.instance.group, "entity/volcan");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
		this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		if(partName.contains("hand")) {
			float angleX = 0;
			float angleY = -90f;
			float angleZ = 90f;
			if(partName.contains("right")) {
				angleY = -angleY;
				angleZ = -angleZ;
			}
			this.angle(loop * 10F, angleX, angleY, angleZ);
		}
	}
}
