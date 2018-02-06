package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegister {
    public GroupInfo groupInfo;

    public RenderRegister(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public void registerRenderFactories() {
        // Creatures:
        for(CreatureInfo creatureInfo : CreatureManager.getInstance().creatures.values()) {
            if(creatureInfo.group != this.groupInfo)
                continue;
            RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryCreature<EntityCreatureBase>(creatureInfo));
        }

        // Projectiles:
        for(Class projectileClass : this.groupInfo.projectileClasses)
            RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile<EntityProjectileBase>(projectileClass));

        // Specials:
        for(Class specialClass : this.groupInfo.specialClasses)
            RenderingRegistry.registerEntityRenderingHandler(specialClass, new RenderFactoryNone<EntityProjectileBase>(specialClass));
    }
}
