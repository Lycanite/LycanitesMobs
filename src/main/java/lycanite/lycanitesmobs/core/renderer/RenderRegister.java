package lycanite.lycanitesmobs.core.renderer;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityProjectileBase;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.info.MobInfo;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegister {
    public GroupInfo groupInfo;

    public RenderRegister(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public void registerRenderFactories() {
        // Creatures:
        for(MobInfo mobInfo : this.groupInfo.mobInfos)
            RenderingRegistry.registerEntityRenderingHandler(mobInfo.entityClass, new RenderFactoryCreature<EntityCreatureBase>(mobInfo));

        // Projectiles:
        for(Class projectileClass : this.groupInfo.projectileClasses)
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<EntityProjectileBase>(projectileClass));

        // Specials:
        for(Class specialClass : this.groupInfo.specialClasses)
            RenderingRegistry.registerEntityRenderingHandler(specialClass, new RenderFactoryNone<EntityProjectileBase>(specialClass));
    }
}
