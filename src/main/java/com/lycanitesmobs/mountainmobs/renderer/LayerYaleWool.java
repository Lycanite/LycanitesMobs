package com.lycanitesmobs.mountainmobs.renderer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.LayerBase;
import com.lycanitesmobs.mountainmobs.entity.EntityYale;
import com.lycanitesmobs.core.renderer.RenderCreature;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerYaleWool extends LayerBase {

    // ==================================================
    //                   Constructor
    // ==================================================
    public LayerYaleWool(RenderCreature renderer) {
        super(renderer);
    }


    // ==================================================
    //                  Render Layer
    // ==================================================
    @Override
    public boolean canRenderLayer(EntityCreatureBase entity, float scale) {
        if(!super.canRenderLayer(entity, scale))
            return false;
        if(!(entity instanceof EntityYale))
            return false;
        return ((EntityYale)entity).hasFur();
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public boolean canRenderPart(String partName, EntityCreatureBase entity, boolean trophy) {
        return "fur".equals(partName);
    }

    @Override
    public Vector4f getPartColor(String partName, EntityCreatureBase entity, boolean trophy) {
        int colorID = entity.getColor();
        return new Vector4f(RenderCreature.colorTable[colorID][0], RenderCreature.colorTable[colorID][1], RenderCreature.colorTable[colorID][2], 1.0F);
    }
}
