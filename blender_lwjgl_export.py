# ##### BEGIN GPL LICENSE BLOCK #####
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software Foundation,
# Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
#
# ##### END GPL LICENSE BLOCK #####
#
# Contributors: Stephen Jones
import bpy
import mathutils
import struct # helps with writing binary files
import os

# TODO: set keyframe range dynamically
_keyFrames = range(1, 7) # note that ranges exclude last element
# grab the animation frames per second dynamically
# TODO: allow dynamic specification of filepath
_filepath="./assets/BinData.txt"
doVertCols = True
vertexCount = 0
_scene = bpy.context.scene
_object = bpy.context.object
_fps = _scene.render.fps


def ProcessVerts(mesh, vert, face, jindex, file):
  # mesh = the mesh object
  # vert = the vertex object being processed
  # face = the face object that this vertex emerges from
  # jindex = the vertex's index with respect to face
  # file = duh

  # methods for rounding off tuples
  def roundVec3(v):
    return round(v[0], 6), round(v[1], 6), round(v[2], 6)

  def roundVec2(v):
    return round(v[0], 6), round(v[1], 6)

  # set v to the vertex positions
  v = roundVec3(tuple(vert.co))

  # Determine, based on Smooth or Flat shading, which normal to use
  # NOTE: experimentation has proved this to be useless so far.
  # If you really want to control smooth/flat shading, use the
  # GL11.glModelShading(GL11.<GL_FLAT/GL_SMOOTH>) command in your
  # LWJGL game
  if face.use_smooth: # smooth shading, use the vertex normal
    normal = tuple(vert.normal)
    normalKey = roundVec3(normal)
  else: # flat shading, use the face normal
    normal = tuple(face.normal)
    normal = roundVec3(normal)

  # Use a UV texture if one exists
  if len(mesh.uv_textures) > 0:
    # obtain the UV layer
    uvLayer = mesh.uv_textures.active.data
    # obtain the uv coord struct for this face
    uv = uvLayer[face.index]
    # split that struct into a tuple
    uv = uv.uv1, uv.uv2, uv.uv3, uv.uv4
    # set uvCoord to the x and y values
    # (using 1.0 - uv[jindex][1] because of y-axis flip from blender
    #   interface to LWJGL gamescreen
    uvCoord = uv[jindex][0], 1.0 - uv[jindex][1]
    #round it off
    uvCoord = roundVec2(uvCoord)
  else: 
    # TODO: this looks like BS right here, so currently UV textures
    # aren't actually optional
    uvCoord = vert.uvco[0], 1.0 - vert.uvco[1]
    uvCoord = roundVec2(uvCoord)

  if doVertCols:
    # obtain the color layer
    colLayer = mesh.vertex_colors.active.data
    # obtain the color struct for this face
    col = colLayer[face.index]
    # split that struct into a tuple
    col = col.color1, col.color2, col.color3, col.color4
    # obtain the vertex's color
    color = col[jindex]
    # round it off
    color = roundVec3(color)

  # now that's over with, write it all to binary
  # 'v' indicates the next 3 floats are vertex coords
  file.write(struct.pack('<c', 'v')) 
  file.write(struct.pack('>3f', v[0], v[1], v[2])) 

  # 'n' indicates the next 3 floats are normal values
  file.write(struct.pack('<c', 'n')) 
  file.write(struct.pack('>3f', normal[0], normal[1], normal[2])) 

  # 'u' indicates the next 2 floats are UV coords
  file.write(struct.pack('<c', 'u')) 
  file.write(struct.pack('>2f', uvCoord[0], uvCoord[1])) 

  # and last of all add the vertex colors
  # yes, this one is genuinely optional
  if doVertCols:
    # 'c' indicates the next 3 floats are color values
    file.write(struct.pack('<c', 'c')) 
    file.write(struct.pack('>3f', color[0], color[1], color[2]))


def ProcessFace(mesh, face, file):
  quadVert = list() # initialize new lists to allow appending
  quadIdx = list()

  for jindex, vert in enumerate(face.vertices):
    if(len(face.vertices) == 3): # triangle
      ProcessVerts(mesh, mesh.vertices[vert], face, jindex, file)
      print("Processed triangle at index: " + str(face.index))
    if(len(face.vertices) == 4): # quad
      quadVert.append(mesh.vertices[vert])
      quadIdx.append(jindex)
      # process the quads as triangles instead of quads
      if(len(quadVert)==4):
        ProcessVerts(mesh, quadVert[1], face, quadIdx[1], file)
        ProcessVerts(mesh, quadVert[3], face, quadIdx[3], file)
        ProcessVerts(mesh, quadVert[0], face, quadIdx[0], file)
        ProcessVerts(mesh, quadVert[1], face, quadIdx[1], file)
        ProcessVerts(mesh, quadVert[3], face, quadIdx[3], file)
        ProcessVerts(mesh, quadVert[2], face, quadIdx[2], file)
        print("Processed quad at index: " + str(face.index))


def Animate():
  """
  file = open(filepath, 'w')
  file.write('\n') # apparently macosx needs some data in a blank file?
  file.close()
  file = open(filepath, 'w')
  file.write('Blender %s - www.blender.org, source file: %r\n' %
(bpy.app.version_string, os.path.basename(bpy.data.filepath)))
  file.write('property v means vertex {x, y, z}\n')
  file.write('property n means normals {x, y, z}\n')
  file.write('property u means uv coords {u, v}\n')
  file.write('property d obsolete for now\n')
  file.write('property c means vertex colors\n')
  file.write(' \n')
  file.write('property ? means animation key frame\n')
  file.write('property $ means fps\n')
  file.write('property # means number of frames\n')
  """
  file = open(_filepath, 'wb') # open the file for binary writing
# We will be using the struct module to help us write the binary file.
# NOTICE about the endian-ness: Java uses big-endian for numbers (>) and
#   little endian for chars (<). Yeah, idk why  :\   Additionally,
#   Python writes chars as single bytes but Java reads them as two
#   Bytes and therefore each char must be read as a byte and cast as
#   a char (EX in java: char mychar = (char)data_input_stream.readByte();)
#   If for some reason the endian-ness is causing you problems,
#   it may be because your processor architecture reads them reversed.
  file.write(struct.pack('<c', '$')) # '$' signals fps
  file.write(struct.pack('>i', _fps)) # set the next int as fps
  file.write(struct.pack('<c', '#')) # '$' signals fps
  file.write(struct.pack('>i', len(_keyFrames))) # set the number of frames
  flipy = mathutils.Matrix(   # create a matrix for adjusting axes
               [1.0, 0.0, 0.0, 0.0],\
               [0.0, 0.0, 1.0, 0.0],\
               [0.0, 1.0, 0.0, 0.0],\
               [0.0, 0.0, 0.0, 1.0],\
               )

  # now loop through each keyframe and write the mesh
  for frame in _keyFrames:
    print("---Keyframe " + str(frame))
    # position the scene at the current frame
    _scene.frame_set(frame)
    # update the scene to account for changes
    _scene.update
    # create a  new mesh datablock with modifiers applied
    mesh = _object.create_mesh(_scene, True, "PREVIEW")
    # flip axes because 'up' in blender (z axis) is not 'up' in the
    #   game window (y axis)
    mesh.transform(flipy * _object.matrix_world)
    # now begin writing the info for this keyframe
    file.write(struct.pack('<c', '?')) # '?' signals specify keyframe
    file.write(struct.pack('>i', frame))
    # now loop through each face and save it's vertices
    for face in mesh.faces:
      ProcessFace(mesh, face, file)
    # as cleanup, remove the new datablock from blender
    bpy.data.meshes.remove(mesh)
  file.close() # the end


def Validate():
  flag = True # if the flag remains true, the mesh will be processed

  # 'poll()' checks if the mode_set operator can be called
  if bpy.ops.object.mode_set.poll(): 
    # if so, put us into object mode for good measure
    bpy.ops.object.mode_set(mode='OBJECT')

  # ensure _object exists
  if not _object:
    print ("Error: select object")
    flag = False

  # ensure only one object is selected
  if len(bpy.context.selected_objects)>1:
    print("Error: must only select one object")
    flag=False

  # ensure the object is a mesh (not a NURBS, camera, lamp, hairbrush...)
  if not _object.type == "MESH":
    print("Error: only meshes can be exported")
    flag = False

  # ensure the object has UV texture or Sticky texture coords
  # TODO: This may not be an absolute necessity
  if not (len(_object.data.uv_textures) > 0) \
  and not (len(_object.data.sticky) > 0):
    print ("Error: must have UV map")
    flag=False

  # check if the object has vertex colors. Note: not required
  if not (len(_object.data.vertex_colors) > 0):
    # if they're not there, we just won't use em
    global doVertCols
    doVertCols=False

  if flag:
    print("\n\nValidated, begin export\n\n")
    Animate()


# main
Validate()
