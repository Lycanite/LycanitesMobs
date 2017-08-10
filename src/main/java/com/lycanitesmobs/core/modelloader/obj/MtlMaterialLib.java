package com.lycanitesmobs.core.modelloader.obj;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class MtlMaterialLib
{

    public static final String COMMENT = "#";
    public static final String NEW_MATERIAL = "newmtl";
    public static final String AMBIENT_COLOR = "Ka";
    public static final String DIFFUSE_COLOR = "Kd";
    public static final String SPECULAR_COLOR = "Ks";
    public static final String TRANSPARENCY_D = "d";
    public static final String TRANSPARENCY_TR = "Tr";
    public static final String ILLUMINATION = "illum";

    public static final String TEXTURE_AMBIENT = "map_Ka";
    public static final String TEXTURE_DIFFUSE = "map_Kd";
    public static final String TEXTURE_SPECULAR = "map_Ks";
    public static final String TEXTURE_TRANSPARENCY = "map_d";
    private ArrayList<Material> materials;
    private String path;
    private String startPath;

    public MtlMaterialLib(String path)
    {
        this.path = path;
        this.startPath = path.substring(0, path.lastIndexOf('/')+1);
        materials = new ArrayList<Material>();
    }

    public void parse(String content)
    {
        String[] lines = content.split("\n");
        Material current = null;
        for(int i = 0; i < lines.length; i++)
        {
            String line = lines[i].trim();
            String[] parts = line.split(" ");
            if(parts[0].equals(COMMENT))
            {
                ;
            }
            else if(parts[0].equals(NEW_MATERIAL))
            {
                Material material = new Material(parts[1]);
                materials.add(material);
                current = material;
            }
            else if(parts[0].equals(AMBIENT_COLOR))
            {
                current.ambientColor = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
            }
            else if(parts[0].equals(DIFFUSE_COLOR))
            {
                current.diffuseColor = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
            }
            else if(parts[0].equals(TEXTURE_DIFFUSE))
            {
                current.diffuseTexture = loadTexture(startPath+parts[1]);
            }
            else if(parts[0].equals(TEXTURE_AMBIENT))
            {
                current.ambientTexture = loadTexture(startPath+parts[1]);
            }
            else if(parts[0].equals(TRANSPARENCY_D) || parts[0].equals(TRANSPARENCY_TR))
            {
                current.transparency = (float)Double.parseDouble(parts[1]);
            }
        }
    }

    private int loadTexture(String string)
    {
        try
        {
            string = string.replace("models/", "textures/"); // Search for textures in textures directory instead of models.
            return loadTexture(ImageIO.read(MtlMaterialLib.class.getResource(string)));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static ByteBuffer imageToByteBuffer(BufferedImage img)
    {
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());

        int bufLen = pixels.length * 4;
        ByteBuffer oglPixelBuf = BufferUtils.createByteBuffer(bufLen);

        for(int y = 0; y < img.getHeight(); y++ )
        {
            for(int x = 0; x < img.getWidth(); x++ )
            {
                int rgb = pixels[y * img.getWidth() + x];
                float a = ((rgb >> 24) & 0xFF) / 255f;
                float r = ((rgb >> 16) & 0xFF) / 255f;
                float g = ((rgb >> 8) & 0xFF) / 255f;
                float b = ((rgb >> 0) & 0xFF) / 255f;
                oglPixelBuf.put((byte)(r * 255f));
                oglPixelBuf.put((byte)(g * 255f));
                oglPixelBuf.put((byte)(b * 255f));
                oglPixelBuf.put((byte)(a * 255f));
            }
        }

        oglPixelBuf.flip();
        return oglPixelBuf;
    }
    
    public static int loadTexture(BufferedImage img)
    {
        ByteBuffer oglPixelBuf = imageToByteBuffer(img);
        int id = glGenTextures();
        int target = GL_TEXTURE_2D;
        glBindTexture(target, id);
        glTexParameterf(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glTexParameteri(target, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        glTexParameteri(target, GL12.GL_TEXTURE_MAX_LEVEL, 0);

        glTexImage2D(target, 0, GL_RGBA8, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, oglPixelBuf);
        GL11.glBindTexture(target, 0);

        return id;
    }

    public List<Material> getMaterials()
    {
        return materials;
    }

}
