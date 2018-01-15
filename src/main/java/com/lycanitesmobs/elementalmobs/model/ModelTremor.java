package com.lycanitesmobs.elementalmobs.model;

import com.lycanitesmobs.core.model.template.ModelTemplateElemental;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTremor extends ModelTemplateElemental {

	// ==================================================
  	//                  Constructors
  	// ==================================================
    public ModelTremor() {
        this(1.0F);
    }

    public ModelTremor(float shadowSize) {

		// Load Model:
		this.initModel("tremor", ElementalMobs.instance.group, "entity/tremor");

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

		if(!"body".equals(partName) && !"mouth".equals(partName)) {
			float vibration = loop * 2;
			if(partName.contains("right")) {
				vibration = -vibration;
			}
			this.translate(MathHelper.cos(vibration) * 0.01f, MathHelper.cos(vibration) * 0.01f, MathHelper.cos(vibration) * 0.01f);
		}

		if(partName.contains("rib") && !partName.contains("07")) {
			float angleX = 0;
			float angleY = 1;
			float angleZ = 0;
			if(!partName.contains("arm")) {
				if (partName.contains("04")) {
					angleY = 180f;
					angleZ = 70f;
				}
				else if (partName.contains("05")) {
					angleY = 180f;
					angleZ = 180f;
				}
				else if (partName.contains("06")) {
					angleY = 180f;
					angleZ = 60f;
				}
			}
			else {
				if (partName.contains("01")) {
					angleX = -180f;
					angleY = 180f;
					angleZ = -60f;
				}
				else if (partName.contains("02")) {
					angleX = -60f;
					angleY = 180f;
					angleZ = -60f;
				}
				else if (partName.contains("03")) {
					angleX = -35f;
					angleY = 180f;
					angleZ = -60f;
				}
				if(partName.contains("armright")) {
					angleX = -angleX;
				}
			}
			this.angle(loop * 50F, angleX, angleY, angleZ);
		}
	}
}
