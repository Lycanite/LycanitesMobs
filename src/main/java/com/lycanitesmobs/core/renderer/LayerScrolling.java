package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class LayerScrolling extends LayerEffect {

	public Vec2f scrollSpeed;

    // ==================================================
    //                   Constructor
    // ==================================================
	public LayerScrolling(RenderCreature renderer, String textureSuffix, boolean glow, int blending, boolean subspecies, Vec2f scrollSpeed) {
		super(renderer, textureSuffix, glow, blending, subspecies);
		this.scrollSpeed = scrollSpeed;
	}


    // ==================================================
    //                      Visuals
    // ==================================================
	@Override
	public Vector2f getTextureOffset(String partName, EntityCreatureBase entity, boolean trophy, float loop) {
		return new Vector2f(loop * this.scrollSpeed.x, loop * this.scrollSpeed.y);
	}
}
