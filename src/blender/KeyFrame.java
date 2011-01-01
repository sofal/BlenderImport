package blender;
import org.lwjgl.BufferUtils; 
import java.nio.FloatBuffer; 

public class KeyFrame { 
  private FloatBuffer verts; 
  private FloatBuffer norms; 
  private FloatBuffer coords; 
  private FloatBuffer cols; 
  private int vertCount;

  public KeyFrame(float[] v, float[] n, float[] u, float[] c){ 
    verts = BufferUtils.createFloatBuffer(v.length); 
    vertCount = v.length/3; // divide by 3 because each vert has xyz
    verts.put(v); 
    verts.rewind(); 

    norms = BufferUtils.createFloatBuffer(n.length); 
    norms.put(n);
    norms.rewind(); 

    coords = BufferUtils.createFloatBuffer(u.length); 
    coords.put(u); 
    coords.rewind(); 

    cols = BufferUtils.createFloatBuffer(c.length); 
    cols.put(c); 
    cols.rewind(); 
  } 

  /* accessor methods */
  public FloatBuffer getVerts() { 
    verts.rewind(); 
    return verts; 
  } 

  public FloatBuffer getNorms() { 
    norms.rewind(); 
    return norms; 
  } 

  public FloatBuffer getCoords() { 
    coords.rewind(); 
    return coords; 
  } 

  public FloatBuffer getCols() {
    cols.rewind(); 
    return cols; 
  } 

  public int getVertCount() {
    return vertCount;
  }
} 

