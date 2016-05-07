package lycanite.lycanitesmobs.api.render;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.model.ModelCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RenderItemMobToken extends ItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
        if(itemStack.getTagCompound() == null)
            return false;
        if(!itemStack.getTagCompound().hasKey("Mob"))
            return false;
        MobInfo mobInfo = ObjectManager.getMobInfo(itemStack.getTagCompound().getString("Mob"));
        if(mobInfo == null)
            return false;
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
        if(itemStack.getTagCompound() == null)
            return;
        if(!itemStack.getTagCompound().hasKey("Mob"))
            return;
        MobInfo mobInfo = ObjectManager.getMobInfo(itemStack.getTagCompound().getString("Mob"));
        if(mobInfo == null)
            return;
        if(AssetManager.getTexture(mobInfo.name) == null)
            AssetManager.addTexture(mobInfo.name, mobInfo.group, "textures/entity/" + mobInfo.name.toLowerCase() + ".png");
        if(AssetManager.getModel(mobInfo.name) == null)
            return;

        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(AssetManager.getTexture(mobInfo.name));

        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            float scale = 0.7F;
            GL11.glTranslatef(1.0F, 0.35F, 0.6F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
        }
        else if(type == ItemRenderType.EQUIPPED) {
            float scale = 0.5F;
            GL11.glTranslatef(0F, 0.35F, 0.15F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
        }
        else if(type == ItemRenderType.INVENTORY) {
            float scale = 12F;
            GL11.glTranslatef(8F, -8F, -40F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
        }
        else {
            float scale = 1F;
            GL11.glTranslatef(0F, 1F, 0F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        }

        float modelScale = -1F;
        ModelBase model = AssetManager.getModel(mobInfo.name);
        if(model instanceof ModelCustom)
            modelScale = 0.0625F;
        model.render(null, 0, 0, 0, 0, 0, modelScale);

        GL11.glPopMatrix();
    }
}