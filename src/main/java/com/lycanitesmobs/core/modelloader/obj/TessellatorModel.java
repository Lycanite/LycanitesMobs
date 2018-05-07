package com.lycanitesmobs.core.modelloader.obj;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

/**
 * @author jglrxavpok
 */
public class TessellatorModel extends ObjModel
{

    public static final EventBus MODEL_RENDERING_BUS = new EventBus();

    public TessellatorModel(ResourceLocation resourceLocation)
    {
        super(resourceLocation.getResourcePath());
        String path = resourceLocation.toString();
        try
        {
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
            String content = new String(read(inputStream), "UTF-8");
            String startPath = path.substring(0, path.lastIndexOf('/') + 1);
            HashMap<ObjObject, IndexedModel> map = new OBJLoader().loadModel(startPath, content);
            objObjects.clear();
            Set<ObjObject> keys = map.keySet();
            Iterator<ObjObject> it = keys.iterator();
            while(it.hasNext())
            {
                ObjObject object = it.next();
                Mesh mesh = new Mesh();
                object.mesh = mesh;
                objObjects.add(object);
                map.get(object).toMesh(mesh);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void renderImpl()
    {
        Collections.sort(objObjects, (a, b) -> {
			Vec3d v = Minecraft.getMinecraft().getRenderViewEntity().getPositionVector();
			double aDist = v.distanceTo(new Vec3d(a.center.x, a.center.y, a.center.z));
			double bDist = v.distanceTo(new Vec3d(b.center.x, b.center.y, b.center.z));
			return Double.compare(aDist, bDist);
		});
        for(ObjObject object : objObjects)
        {
            renderGroup(object);
        }
    }


    @Override
    public void renderGroupsImpl(String group)
    {
        for(ObjObject object : objObjects)
        {
            if(object.getName().equals(group))
            {
                renderGroup(object);
            }
        }
    }


    @Override
    public void renderGroupImpl(ObjObject obj, Vector4f color, Vector2f textureOffset) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tess.getBuffer();
        if(obj.mesh == null) {
            return;
        }
		int[] indices = obj.mesh.indices;

        // Colors From OBJ:
        //Vector4f color = new Vector4f(1, 1, 1, 1);
        /*if(obj.material != null) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.material.diffuseTexture);
            color = new Vector3f(
                    obj.material.diffuseColor.x * obj.material.ambientColor.x,
                    obj.material.diffuseColor.y * obj.material.ambientColor.y,
                    obj.material.diffuseColor.z * obj.material.ambientColor.z);
            alpha = obj.material.transparency;
        }*/

		// Build Buffer:
        bufferBuilder.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        for(int i = 0; i < indices.length; i += 3) {
            for(int iv = 0; iv < 3; iv++) {
                Vertex v = obj.mesh.vertices[indices[i + iv]];
                bufferBuilder
                        .pos(v.getPos().x, v.getPos().y, v.getPos().z)
                        .tex(v.getTexCoords().x + (textureOffset.getX() * 0.01f), 1f - (v.getTexCoords().y + (textureOffset.getY() * 0.01f)))
                        .color(color.x, color.y, color.z, color.w)
                        .normal(v.getNormal().x, v.getNormal().y, v.getNormal().z)
                        .endVertex();
            }
        }

        // Draw Buffer:
        //tess.draw();
		bufferBuilder.finishDrawing();
		if (bufferBuilder.getVertexCount() > 0) {
			VertexFormat vertexformat = bufferBuilder.getVertexFormat();
			int i = vertexformat.getNextOffset();
			ByteBuffer bytebuffer = bufferBuilder.getByteBuffer();
			List<VertexFormatElement> list = vertexformat.getElements();

			for (int j = 0; j < list.size(); ++j) {
				VertexFormatElement vertexformatelement = list.get(j);
				bytebuffer.position(vertexformat.getOffset(j));
				vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
			}

			GlStateManager.glDrawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
			int i1 = 0;

			for (int j1 = list.size(); i1 < j1; ++i1) {
				VertexFormatElement vertexformatelement1 = list.get(i1);
				vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
			}
		}
		bufferBuilder.reset();

		GL11.glDisable(GL11.GL_BLEND);
    }


    @Override
    public boolean fireEvent(ObjEvent event)
    {
        Event evt = null;
        if(event.type == ObjEvent.EventType.PRE_RENDER_GROUP)
        {
            evt = new TessellatorModelEvent.RenderGroupEvent.Pre(((ObjObject) event.data[1]).getName(), this);
        }
        else if(event.type == ObjEvent.EventType.POST_RENDER_GROUP)
        {
            evt = new TessellatorModelEvent.RenderGroupEvent.Post(((ObjObject) event.data[1]).getName(), this);
        }
        else if(event.type == ObjEvent.EventType.PRE_RENDER_ALL)
        {
            evt = new TessellatorModelEvent.RenderPre(this);
        }
        else if(event.type == ObjEvent.EventType.POST_RENDER_ALL)
        {
            evt = new TessellatorModelEvent.RenderPost(this);
        }
        if(evt != null)
            return !MODEL_RENDERING_BUS.post(evt);
        return true;
    }
}
