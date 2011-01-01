package blender;
import java.io.*;
import java.util.*;

/**  Read Binary files exported by a complementary Blender python
 * script. 
 * @author xabotage **/
public class ActionLoader {

  public static void importAction(String filename, String actionName,
                                  Mesh meshClient) {
    File _file = null;
    int _fps = 0;
    int _keyframeCount = 0; // keyframe index keeper
    int _keyframes = 0; // stores total keyframes
    int _vertCount = 0;

    // Get the file from the argument line.
    _file = new File (filename);
    if (_file == null) {
      System.out.printf("Error: File \"%s\" not found", filename);
    }

    try {

      /* we will have to read the file twice, once to obtain the
       * number of vertices in each keyframe, and once to save the data.
       * Luckily, we can assume that the first keyframe has all vertices
       * accounted for.*/
      // TODO: There must be a better way...

      /* FIRST PASS ----- */
      // Wrap the FileInputStream with a DataInputStream
      FileInputStream fin = new FileInputStream(_file);
      DataInputStream data_in = new DataInputStream(fin);

      System.out.printf("%n---First Pass---%n%n");
      boolean first_frame = true;
      char token;

      while (first_frame) {
        try {
          token = (char)data_in.readByte();
          switch (token) {
            case '$': // found fps indicator
              _fps = data_in.readInt(); // we can go ahead and grab this
              break;
            case '#': // found number of frames indicator
              _keyframes = data_in.readInt(); // grab this too
              break;
            case '?': // found keyframe indicator
              if(_keyframeCount >= 1) {
                // one frame has already been cycled and we should now
                //  have the number of vertices
                first_frame = false;
                break;
              }
              _keyframeCount++;
              data_in.readInt();
              break;
            case 'v': // found vertex position
              _vertCount++; // this is what we're really looking for
              // just to make up for the next 3 vertex positions
              data_in.readFloat();
              data_in.readFloat();
              data_in.readFloat();
              break;
            case 'n': // found normal values (not important right now)
              data_in.readFloat();
              data_in.readFloat();
              data_in.readFloat();
              break;
            case 'u': // found uv texture coordinates (not important now)
              data_in.readFloat();
              data_in.readFloat();
              break;
            case 'c': // found color values (not important now)
              data_in.readFloat();
              data_in.readFloat();
              data_in.readFloat();
              break;
          }
        }
        catch (EOFException eof) {
          System.out.println ("End of File");
          break;
        }
      }
      System.out.printf("Vert Count = %d %nFPS = %d %nKeyframes = %d%n", 
                        _vertCount, _fps, _keyframes);
      data_in.close ();

      /* SECOND PASS --- */

      System.out.printf("%n---Second Pass---%n%n");
      // reset input streams
      fin = new FileInputStream(_file);
      data_in = new DataInputStream(fin);

      _keyframeCount = 0; // reset keyframe count from first pass
      // make a new array to store the action KeyFrames
      KeyFrame[] actionFrames = new KeyFrame[_keyframes];
      float[] verts = new float[_vertCount * 3]; // stores vertex coords
      float[] norms = new float[_vertCount * 3]; // stores vertex normals
      float[] cols = new float[_vertCount * 3]; // stores vertex colors
      float[] uvs = new float[_vertCount * 2]; // stores vertex UV coords
      int vi = 0; // vertex index
      int ni = 0; // normal index, etc
      int ci = 0;
      int ui = 0;

      while (true) { // just loop until the end of the file
        try {
          token = (char)data_in.readByte();
          switch (token) {
            case '$': // found fps indicator
              _fps = data_in.readInt(); // we can go ahead and grab this
              break;
            case '?': // found keyframe indicator
              if(_keyframeCount >= 1) {
                actionFrames[_keyframeCount-1] = new KeyFrame(verts, norms,
                    uvs, cols);
                vi = 0; ni = 0; ci = 0; ui = 0;
                verts = new float[_vertCount * 3]; 
                norms = new float[_vertCount * 3]; 
                cols = new float[_vertCount * 3]; 
                uvs = new float[_vertCount * 2]; 
              }
              _keyframeCount++;
              data_in.readInt(); // might not need this...
              break;
            case '#': // found number of frames indicator
              _keyframes = data_in.readInt(); // grab this too
              break;
            case 'v': // found vertex position
              // Store the next 3 floats as xyz vertex coords
              verts[vi++] = data_in.readFloat();
              verts[vi++] = data_in.readFloat();
              verts[vi++] = data_in.readFloat();
              break;
            case 'n': // found normal values (not important right now)
              // Store the next 3 floats as xyz vertex values
              norms[ni++] = data_in.readFloat();
              norms[ni++] = data_in.readFloat();
              norms[ni++] = data_in.readFloat();
              break;
            case 'u': // found uv texture coordinates (not important now)
              // Store the next 2 floats as uv texture coords
              uvs[ui++] = data_in.readFloat();
              uvs[ui++] = data_in.readFloat();
              break;
            case 'c': // found color values (not important now)
              // Store the next 3 floats as color values
              cols[ci++] = data_in.readFloat();
              cols[ci++] = data_in.readFloat();
              cols[ci++] = data_in.readFloat();
              break;
          }
        }
        catch (EOFException eof) {
          System.out.println ("End of File");
          break;
        }
      }
      // save that last keyframe:
      actionFrames[_keyframeCount-1] = new KeyFrame(verts, norms,
          uvs, cols);

      // now give the KeyFrames array to the meshClient as a new action
      meshClient.addAction(actionName, _fps, actionFrames);
      System.out.println("Action Loaded Successfully");
      // and we're done

    } catch  (IOException e) {
      System.out.println ( "IO Exception =: " + e );
    }

  } 
} 
