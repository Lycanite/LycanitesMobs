package lycanite.lycanitesmobs.core.renderer;

import lycanite.lycanitesmobs.core.info.MobInfo;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryCreature<T extends Entity> implements IRenderFactory {
    protected String entityID;

    public RenderFactoryCreature(MobInfo mobInfo) {
        this.entityID = mobInfo.getEntityID();
    }

    @Override
    public Render createRenderFor(RenderManager manager) {
        return new RenderCreature(this.entityID, manager);
    }

}
