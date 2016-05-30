package lycanite.lycanitesmobs.demonmobs.model;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.model.ModelCustomObj;
import lycanite.lycanitesmobs.api.renderer.LayerFire;
import lycanite.lycanitesmobs.api.renderer.LayerShield;
import lycanite.lycanitesmobs.api.renderer.RenderCreature;
import lycanite.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAsmodeus extends ModelCustomObj {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelAsmodeus() {
        this(1.0F);
    }

    public ModelAsmodeus(float shadowSize) {
    	// Load Model:
    	this.initModel("asmodeus", DemonMobs.group, "entity/asmodeus");

    	// Set Rotation Centers:
        // Blender: Y = Z
    	setPartCenter("head", 0F, 20F, 0F);
        setPartCenter("shield", 0F, 20F, 0F);
    	setPartCenter("body", 0F, 20F, 0F);
        setPartCenter("turret", 0F, 15.44F, -1.99F);
        setPartCenter("weapon", 0F, 9.2F, -14.05F);

    	setPartCenter("armleft", 13F, 31.2F, -4F);
    	setPartCenter("armright", -13F, 31.2F, -4F);

    	setPartCenter("legleftfront", 10.28513F, 18.4F, -6.4F);
    	setPartCenter("legleftmiddle", 12.8F, 18.4F, 0F);
    	setPartCenter("legleftback", 10.28513F, 18.4F, 6.4F);

    	setPartCenter("legrightfront", -10.28513F, 18.4F, -6.4F);
    	setPartCenter("legrightmiddle", -12.8F, 18.4F, 0F);
    	setPartCenter("legrightback", -10.28513F, 18.4F, 6.4F);
    	
    	lockHeadX = true;
    	lockHeadY = true;

        // Trophy:
        this.trophyScale = 0.1F;
        this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
    }


    // ==================================================
    //             Add Custom Render Layers
    // ==================================================
    @Override
    public void addCustomLayers(RenderCreature renderer) {
        super.addCustomLayers(renderer);
        renderer.addLayer(new LayerFire(renderer));
        renderer.addLayer(new LayerShield(renderer));
    }


    // ==================================================
    //                Can Render Part
    // ==================================================
    /** Returns true if the part can be rendered on the base layer. **/
    public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
        return !"shield".equals(partName);
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
    	
    	// Leg Angles:
    	if(partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightmiddle")
    			|| partName.equals("legleftmiddle") || partName.equals("legrightfront") || partName.equals("legrightback"))
    		angleZ = 1F;
    	if(partName.equals("legleftfront")) angleY = 30F / 360F;
    	if(partName.equals("legleftmiddle")) angleY = 0F;
    	if(partName.equals("legleftback")) angleY = -30F / 360F;
    	if(partName.equals("legrightfront")) angleY = -30F / 360F;
    	if(partName.equals("legrightmiddle")) angleY = 0F;
    	if(partName.equals("legrightback")) angleY = 30F / 360F;
    	
    	// Idle - Arms:
    	float armSwing = 0.3F;
    	if(partName.equals("armleft")) {
    		rotZ += -Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F);
    		rotX += -Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	if(partName.equals("armright")) {
    		rotZ += Math.toDegrees(MathHelper.cos(loop * 0.09F) * 0.05F + 0.05F);
    		rotX += Math.toDegrees(MathHelper.sin(loop * 0.067F) * 0.05F);
    	}
    	
    	// Walking - Arms:
    	if(partName.equals("armleft")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F) * 2.0F * distance * armSwing);
    	}
    	if(partName.equals("armright")) {
    		rotX += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * 2.0F * distance * armSwing);
        }
    	
    	// Walking - Legs:
    	float walkSwing = 0.3F;
    	if(partName.equals("legleftfront") || partName.equals("legleftback") || partName.equals("legrightmiddle"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance);
    	if(partName.equals("legleftmiddle") || partName.equals("legrightfront") || partName.equals("legrightback"))
    		rotation += Math.toDegrees(MathHelper.cos(time * 0.6662F) * walkSwing * distance);
    	
    	// Walking - Bobbing:
		float bob = MathHelper.cos(time * 0.6662F + (float)Math.PI) * walkSwing * distance;
		if(bob < 0) bob += -bob * 2;
		posY += bob;

        // Turret:
        if(partName.contains("turret") || partName.contains("weapon")) {
            if(partName.contains("weapon"))
                this.centerPartToPart("weapon", "turret");
            this.rotate((float)Math.toDegrees(lookX / (180F / Math.PI)) - 10F, (float)Math.toDegrees(lookY / (180F / Math.PI)), 0);
            if(partName.contains("weapon"))
                this.uncenterPartToPart("weapon", "turret");
        }

        // Spinning Weapon:
        if(entity instanceof EntityCreatureBase) {
            if (partName.contains("weapon") && ((EntityCreatureBase)entity).justAttacked()) {
                rotZ += time;
            }
        }
		
    	// Apply Animations:
    	this.rotate(rotation, angleX, angleY, angleZ);
        this.rotate(rotX, rotY, rotZ);
        this.translate(posX, posY, posZ);
    }
}
