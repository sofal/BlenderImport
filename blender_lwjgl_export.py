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
import os

keyFrames=[1, 6]
fps=60
filepath="/home/executor/dev/blender/LWJGLexport.txt"
doVertCols=True
vCount=0
scene=bpy.context.scene
context=bpy.context
object=context.object


class ProcessVerts():
    def __init__(self, mesh, vert, face, faceIdx, jindex, file):
        def roundVec3(v):
            return round(v[0], 6), round(v[1], 6), round(v[2], 6)

        def roundVec2(v):
            return round(v[0], 6), round(v[1], 6)

        meshVerts=mesh.vertices
        v=roundVec3(tuple(vert.co))

        if face.use_smooth:
            normal=tuple(v.normal)
            normalKey=roundVec3(normal)
        else:
            normal=tuple(face.normal)
            normal=roundVec3(normal)

        if len(mesh.uv_textures)>0:
            uvLayer=mesh.uv_textures.active
            uvLayer=uvLayer.data
            uv=uvLayer[faceIdx]
            uv=uv.uv1, uv.uv2, uv.uv3, uv.uv4
            uvCoord=uv[jindex][0], 1.0-uv[jindex][1]
            uvCoord=roundVec2(uvCoord)
        else:
            uvCoord=v.uvco[0], 1.0-v.uvco[1]
            uvCoord=roundVec2(uvCoord)

        if doVertCols:
            colLayer = mesh.vertex_colors.active
            print(len(colLayer))
            colLayer = colLayer.data
            col=colLayer[faceIdx]
            col = col.color1, col.color2, col.color3, col.color4
            color=col[jindex]
            color = round(color[0], 6), round(color[1], 6), round(color[2], 6)

        file.write('v %.6f %.6f  %.6f \n' % v)
        file.write('n %.6f %.6f  %.6f \n' % normal) # no
        file.write('u %.6f %.6f  \n' %uvCoord) # uv

        if doVertCols:
            file.write('c %f %f  %f \n' % color) # col

        file.write('\n')
        print(v)


class ProcessFace():
    def __init__(self, mesh, face, faceIdx, file):
        meshVerts=mesh.vertices
        quadVert=list()
        quadIdx=list()

        for jindex, vert in enumerate(face.vertices):
            if(len(face.vertices)==3):
                 ProcessVerts(mesh, meshVerts[vert], face, faceIdx, jindex, file)
                 print("Processing tri\n")
            if(len(face.vertices)==4):
                 quadVert.append(meshVerts[vert])
                 quadIdx.append(jindex)
                 if(len(quadVert)==4):
                     ProcessVerts(mesh, quadVert[1], face, faceIdx, quadIdx[1], file)
                     ProcessVerts(mesh, quadVert[3], face, faceIdx, quadIdx[3], file)
                     ProcessVerts(mesh, quadVert[0], face, faceIdx, quadIdx[0], file)
                     ProcessVerts(mesh, quadVert[1], face, faceIdx, quadIdx[1], file)
                     ProcessVerts(mesh, quadVert[3], face, faceIdx, quadIdx[3], file)
                     ProcessVerts(mesh, quadVert[2], face, faceIdx, quadIdx[2], file)


class ProcessMesh():
    def __init__(self, mesh, keyFrame, file):
        for faceIdx, face in enumerate(mesh.faces):
            ProcessFace(mesh, face, faceIdx, file)


class Animate():
    def __init__(self):
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
        file.write('property # means number of vertices per frame\n')

        flipy = mathutils.Matrix(\
                     [1.0, 0.0, 0.0, 0.0],\
                     [0.0, 0.0, 1.0, 0.0],\
                     [0.0, 1.0, 0.0, 0.0],\
                     [0.0, 0.0, 0.0, 1.0],\
                     )

        for frame in keyFrames:
            scene.frame_set(frame)
            scene.update
            mesh=object.create_mesh(scene, True, "PREVIEW")
            mesh.transform(flipy*object.matrix_world)
            file.write('\n\n? %d : Animation keyFrame\n' % frame)
            file.write('$ %d : fps\n' % fps)
            ProcessMesh(mesh, frame, file)
            bpy.data.meshes.remove(mesh)
        file.close()


class Validate():
    def __init__(self):
        flag=True
        meshTest=object.data

        if bpy.ops.object.mode_set.poll():
            bpy.ops.object.mode_set(mode='OBJECT')

        if not object:
            print ("Error: select objects")
            flag=False

        if not object.type=="MESH":
            print("Error: only meshes can be exported")
            flag=False

        if not (len(meshTest.uv_textures)>0) and not (len(meshTest.sticky)>0):
            print ("Error: must have UV map")
            flag=False

        if not (len(meshTest.vertex_colors)>0):
            global doVertCols
            doVertCols=False

        if len(bpy.context.selected_objects)>1:
            print("Error: must only select one object")
            flag=False

        if flag:
            print("Worked, foo")
            doit=Animate()


Validate()
