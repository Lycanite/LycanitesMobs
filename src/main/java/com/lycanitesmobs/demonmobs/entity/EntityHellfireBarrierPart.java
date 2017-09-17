package com.lycanitesmobs.demonmobs.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityHellfireBarrierPart extends EntityHellfireWall {

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireBarrierPart(World par1World) {
        super(par1World);
    }

    public EntityHellfireBarrierPart(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityHellfireBarrierPart(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    // ========== Setup Projectile ==========
    @Override
    public void setup() {
        this.entityName = "hellfirebarrierpart";
        super.setup();
        this.animationFrameMax = 19;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public String getTextureName() {
        return "hellfirebarrier";
    }
}
