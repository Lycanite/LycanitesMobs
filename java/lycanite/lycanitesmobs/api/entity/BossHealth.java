package lycanite.lycanitesmobs.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class BossHealth implements IBossDisplayData {
    EntityLivingBase host;

    // ==================================================
    //                    Constructor
    // ==================================================
    public BossHealth(EntityLivingBase host) {
        this.host = host;
    }


    // ==================================================
    //                    Boss Health
    // ==================================================
    @Override
    public float getMaxHealth() {
        return this.host.getMaxHealth();
    }

    @Override
    public float getHealth() {
        return this.host.getHealth();
    }

    @Override
    public IChatComponent func_145748_c_() {
        if(this.host instanceof EntityCreatureBase) {
            EntityCreatureBase creature = (EntityCreatureBase)this.host;
            if(creature.isBoss())
                return new ChatComponentText(this.host.getCommandSenderName() + " (" + StatCollector.translateToLocal("event.boss.phase") + ": " + (creature.getBattlePhase() + 1) + ")");
        }
        return this.host.func_145748_c_(); // Get Command Sender Name
    }
}
