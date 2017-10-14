package com.lycanitesmobs.core.model;

import org.lwjgl.opengl.GL11;

public class Animator {

	// ==================================================
	//                  GLL Actions
	// ==================================================
	public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
		GL11.glRotatef(rotation, angleX, angleY, angleZ);
	}
	public void doRotate(float rotX, float rotY, float rotZ) {
		GL11.glRotatef(rotX, 1F, 0F, 0F);
		GL11.glRotatef(rotY, 0F, 1F, 0F);
		GL11.glRotatef(rotZ, 0F, 0F, 1F);
	}
	public void doTranslate(float posX, float posY, float posZ) {
		GL11.glTranslatef(posX, posY, posZ);
	}
	public void doScale(float scaleX, float scaleY, float scaleZ) {
		GL11.glScalef(scaleX, scaleY, scaleZ);
	}
}
