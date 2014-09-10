package lycanite.lycanitesmobs.api;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundEntity extends MovingSound {
    private final Entity entity;

    public SoundEntity(Entity entity, String sound, Float volume) {
        super(new ResourceLocation(sound));
        this.entity = entity;
        this.repeat = true;
        this.volume = volume;
        this.field_147665_h = 0;
    }

    public void update() {
        if (this.entity == null || this.entity.isDead) {
            this.donePlaying = true;
        }
        else {
            this.xPosF = (float)this.entity.posX;
            this.yPosF = (float)this.entity.posY;
            this.zPosF = (float)this.entity.posZ;
        }
    }
}