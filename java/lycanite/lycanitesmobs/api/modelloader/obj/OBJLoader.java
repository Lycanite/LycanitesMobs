package lycanite.lycanitesmobs.api.modelloader.obj;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class OBJLoader
{

    public final static class OBJIndex
    {
        int positionIndex;
        int texCoordsIndex;
        int normalIndex;

        public boolean equals(Object o)
        {
            if(o instanceof OBJIndex)
            {
                OBJIndex index = (OBJIndex)o;
                return index.normalIndex == normalIndex && index.positionIndex == positionIndex && index.texCoordsIndex == texCoordsIndex;
            }

            return false;
        }

        public int hashCode()
        {
            final int base = 17;
            final int multiplier = 31;

            int result = base;
            result = multiplier * result + positionIndex;
            result = multiplier * result + texCoordsIndex;
            result = multiplier * result + normalIndex;
            return result;
        }
    }

    private static final String COMMENT = "#";
    private static final String FACE = "f";
    private static final String POSITION = "v";
    private static final String TEX_COORDS = "vt";
    private static final String NORMAL = "vn";
    private static final String NEW_OBJECT = "o";
    private static final String NEW_GROUP = "g";
    private static final String USE_MATERIAL = "usemtl";
    private static final String NEW_MATERIAL = "mtllib";

    private boolean hasNormals = false;
    private boolean hasTexCoords = false;

    public HashMap<ObjObject, IndexedModel> loadModel(String startPath, String res) throws Exception
    {
        try
        {
            hasNormals = true;
            hasTexCoords = true;
            IndexedModel result = new IndexedModel();
            IndexedModel normalModel = new IndexedModel();
            String lines[] = res.split("\n|\r");

            int posOffset = 0;
            int indicesOffset = 0;
            int texOffset = 0;
            int normOffset = 0;
            ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
            ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
            ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
            ArrayList<OBJIndex> indices = new ArrayList<OBJIndex>();
            ArrayList<Material> materials = new ArrayList<Material>();
            HashMapWithDefault<OBJIndex, Integer> resultIndexMap = new HashMapWithDefault<OBJIndex, Integer>();
            HashMapWithDefault<Integer, Integer> normalIndexMap = new HashMapWithDefault<Integer, Integer>();
            HashMapWithDefault<Integer, Integer> indexMap = new HashMapWithDefault<Integer, Integer>();
            resultIndexMap.setDefault(-1);
            normalIndexMap.setDefault(-1);
            indexMap.setDefault(-1);
            
            HashMap<ObjObject, IndexedModel> map = new HashMap<ObjObject, IndexedModel>();
            
            ObjObject currentObject = null;
            HashMap<ObjObject, IndexedModel[]> objects = new HashMap<ObjObject, IndexedModel[]>();
            objects.put(currentObject = new ObjObject("main"), new IndexedModel[]{result, normalModel});
            for(String line : lines)
            {
                if(line != null && !line.trim().equals(""))
                {
                    String[] parts = trim(line.split(" "));
                    if(parts.length == 0)
                        continue;
                    if(parts[0].equals(COMMENT))
                    {
                        continue;
                    }
                    else if(parts[0].equals(POSITION))
                    {
                        positions.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                    }
                    else if(parts[0].equals(FACE))
                    {
                        for(int i = 0; i < parts.length - 3; i++)
                        {
                            indices.add(parseOBJIndex(parts[1], posOffset, texOffset, normOffset));
                            indices.add(parseOBJIndex(parts[2 + i], posOffset, texOffset, normOffset));
                            indices.add(parseOBJIndex(parts[3 + i], posOffset, texOffset, normOffset));
                        }
                    }
                    else if(parts[0].equals(NORMAL))
                    {
                        normals.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                    }
                    else if(parts[0].equals(TEX_COORDS))
                    {
                        texCoords.add(new Vector2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                    }
                    else if(parts[0].equals(NEW_MATERIAL))
                    {
                        String path = startPath+parts[1];
                        MtlMaterialLib material = new MtlMaterialLib(path);
                        material.parse(read(OBJLoader.class.getResourceAsStream(path)));
                        materials.addAll(material.getMaterials());
                    }
                    else if(parts[0].equals(USE_MATERIAL))
                    {
                        currentObject.material = getMaterial(materials, parts[1]);
                    }
                    else if(parts[0].equals(NEW_OBJECT) || parts[0].equals(NEW_GROUP))
                    {
                        result.getObjIndices().addAll(indices);
                        normalModel.getObjIndices().addAll(indices);
                        result = new IndexedModel();
                        normalModel = new IndexedModel();
                        indices.clear();
                        objects.put(currentObject = new ObjObject(parts[1]), new IndexedModel[]{result, normalModel});
                    }
                }
            }
            result.getObjIndices().addAll(indices);
            normalModel.getObjIndices().addAll(indices);
            
            Iterator<ObjObject> it = objects.keySet().iterator();
            while(it.hasNext())
            {
                ObjObject object = it.next();
                result = objects.get(object)[0];
                normalModel = objects.get(object)[1];
                indices = result.getObjIndices();
                map.put(object, result);
                object.center = result.computeCenter();
                for(int i = 0; i < indices.size(); i++)
                {
                    OBJIndex current = indices.get(i);
                    Vector3f pos = positions.get(current.positionIndex);
                    Vector2f texCoord;
                    if(hasTexCoords)
                    {
                        texCoord = texCoords.get(current.texCoordsIndex);
                    }
                    else
                    {
                        texCoord = new Vector2f();
                    }
                    Vector3f normal;
                    if(hasNormals)
                    {
                        try
                        {
                            normal = normals.get(current.normalIndex);
                        }
                        catch(Exception e)
                        {
                           normal = new Vector3f();
                        }
                    }
                    else
                    {
                        normal = new Vector3f();
                    }

                    int modelVertexIndex = resultIndexMap.get(current);
                    if(modelVertexIndex == -1)
                    {
                        resultIndexMap.put(current, result.getPositions().size());
                        modelVertexIndex = result.getPositions().size();

                        result.getPositions().add(pos);
                        result.getTexCoords().add(texCoord);
                        if(hasNormals)
                            result.getNormals().add(normal);
                        result.getTangents().add(new Vector3f());
                    }

                    int normalModelIndex = normalIndexMap.get(current.positionIndex);

                    if(normalModelIndex == -1)
                    {
                        normalModelIndex = normalModel.getPositions().size();
                        normalIndexMap.put(current.positionIndex, normalModelIndex);

                        normalModel.getPositions().add(pos);
                        normalModel.getTexCoords().add(texCoord);
                        normalModel.getNormals().add(normal);
                        normalModel.getTangents().add(new Vector3f());
                    }

                    result.getIndices().add(modelVertexIndex);
                    normalModel.getIndices().add(normalModelIndex);
                    indexMap.put(modelVertexIndex, normalModelIndex);
                }

                if(!hasNormals)
                {
                    normalModel.computeNormals();

                    for(int i = 0; i < result.getNormals().size(); i++)
                    {
                        result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
                    }
                }
            }
            return map;
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error while loading model", e);
        }
    }
    
    private Material getMaterial(ArrayList<Material> materials, String id)
    {
        for(Material mat : materials)
        {
            if(mat.getName().equals(id))
                return mat;
        }
        return null;
    }

    protected String read(InputStream resource) throws IOException
    {
        int i;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((i = resource.read(buffer, 0, buffer.length)) != -1)
        {
            out.write(buffer,0,i);
        }
        out.flush();
        out.close();
        return new String(out.toByteArray(), "UTF-8");
    }


    public OBJIndex parseOBJIndex(String token, int posOffset, int texCoordsOffset, int normalOffset)
    {
        OBJIndex index = new OBJIndex();
        String[] values = token.split("/");

        index.positionIndex = Integer.parseInt(values[0]) - 1 - posOffset;
        if(values.length > 1)
        {
            if(values[1] != null && !values[1].equals(""))
            {
                index.texCoordsIndex = Integer.parseInt(values[1]) - 1 - texCoordsOffset;
            }
            hasTexCoords = true;
            if(values.length > 2)
            {
                index.normalIndex = Integer.parseInt(values[2]) - 1 - normalOffset;
                hasNormals = true;
            }
        }
        return index;
    }

    public static String[] trim(String[] split)
    {
        ArrayList<String> strings = new ArrayList<String>();
        for(String s : split)
            if(s != null && !s.trim().equals(""))
                strings.add(s);
        return strings.toArray(new String[0]);
    }

}
