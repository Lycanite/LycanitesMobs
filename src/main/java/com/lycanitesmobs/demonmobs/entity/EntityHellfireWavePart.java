package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityHellfireWavePart extends EntityHellfireWall {

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireWavePart(World par1World) {
        super(par1World);
    }

    public EntityHellfireWavePart(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    public EntityHellfireWavePart(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    // ========== Setup Projectile ==========
    @Override
    public void setup() {
        this.entityName = "hellfirewavepart";
        super.setup();
        this.animationFrameMax = 59;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public String getTextureName() {
        return "hellfirewave";
    }
}
