package com.lycanitesmobs.core.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryNone<T extends Entity> implements IRenderFactory {
    protected Class entityClass;

    public RenderFactoryNone(Class entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Render createRenderFor(RenderManager manager) {
        return new RenderNone(manager);
    }

}
