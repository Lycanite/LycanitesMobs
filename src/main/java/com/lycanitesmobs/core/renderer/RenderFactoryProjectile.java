package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryProjectile<T extends Entity> implements IRenderFactory {
    protected Class entityClass;

	public RenderFactoryProjectile(Class entityClass) {
		this.entityClass = entityClass;
	}

    @Override
    public Render createRenderFor(RenderManager manager) {
        if(ObjectManager.projectileModels.containsKey(this.entityClass)) {
            return new RenderProjectileModel(ObjectManager.projectileModels.get(this.entityClass), manager);
        }
        return new RenderProjectile(manager, this.entityClass);
    }

}
