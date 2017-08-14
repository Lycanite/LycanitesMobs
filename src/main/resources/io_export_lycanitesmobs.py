bl_info = {
    "name":         "Export Lycanites Mobs",
    "author":       "Lycanite (Richard Nicholson)",
    "blender":      (2,7,8),
    "version":      (0,0,1),
    "location":     "File > Export > Lycanites Mobs (.txt)",
    "description":  "Exports object data into an animation parts json file for loading into the Lycanites Mobs mod for Minecraft.",
    "category":     "Import-Export"
}

import bpy
from bpy.props import *

class ExportLycanitesMobs(bpy.types.Operator):
  bl_idname = "export.lycanitesmobs"
  bl_label = 'Export Lycanites Mobs'
  
  filepath = StringProperty(name="File Path", description="Filepath for exporting", maxlen= 1024, default="")
  
  def execute(self, context):
    out = open(self.properties.filepath, "w")
    objects = bpy.context.scene.objects
    first = True
    out.write('[\n')
    for obj in objects:
      if obj.type == 'MESH':
        if not first:
          out.write(',\n')
        first = False
        out.write('  {\n')
        out.write('    "name": "' + obj.name + '",\n')
        parentName = ''
        if obj.parent != None and obj.parent.type == 'MESH':
          parentName = obj.parent.name
        out.write('    "parent": "' + parentName + '",\n')
        out.write('    "centerX": "' + str(round(obj.matrix_world.to_translation()[0], 3)) + '",\n')
        out.write('    "centerY": "' + str(round(obj.matrix_world.to_translation()[2], 3)) + '",\n')
        out.write('    "centerZ": "' + str(round(-obj.matrix_world.to_translation()[1], 3)) + '"\n')
        out.write('  }')
    out.write('\n]')
    return {'FINISHED'}
  
  def invoke(self, context, event):
    self.filepath = ""
    wm = context.window_manager
    wm.fileselect_add(self)
    return {'RUNNING_MODAL'}




def menu_func(self, context):
  self.layout.operator(ExportLycanitesMobs.bl_idname, text="Lycanites Mobs", icon='EXPORT')
  
def register():
  bpy.utils.register_class(ExportLycanitesMobs)
  bpy.types.INFO_MT_file_export.append(menu_func)

def unregister():
  bpy.utils.unregister_class(ExportLycanitesMobs)
  bpy.types.INFO_MT_file_export.remove(menu_func)

if __name__ == "__main__":
  register()