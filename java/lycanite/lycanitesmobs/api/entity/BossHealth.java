package lycanite.lycanitesmobs.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.util.IChatComponent;

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
        return this.host.func_145748_c_(); // Get Command Sender Name
    }
}
