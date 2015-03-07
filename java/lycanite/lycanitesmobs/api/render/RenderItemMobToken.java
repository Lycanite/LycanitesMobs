package lycanite.lycanitesmobs.api.render;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.info.MobInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderItemMobToken implements IItemRenderer {

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
            return;
        if(AssetManager.getModel(mobInfo.name) == null)
            return;

        GL11.glPushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(AssetManager.getTexture(mobInfo.name));

        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            float scale = 0.7F;
            GL11.glTranslatef(1.0F, 0.7F, 0.6F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
        }
        else if(type == ItemRenderType.EQUIPPED) {
            float scale = 0.25F;
            GL11.glTranslatef(0F, 0.7F, 0.3F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
        }
        else if(type == ItemRenderType.ENTITY) {
            float scale = 0.5F;
            GL11.glTranslatef(75F, 75F, 0F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
        }
        else {
            float scale = 4F;
            GL11.glTranslatef(5F, 5F, 0F);
            GL11.glScalef(scale, scale, scale);
            //GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
        }

        AssetManager.getModel(mobInfo.name).render(null, 0, 0, 0, 0, 0, 0.0625F);

        GL11.glPopMatrix();
    }
}