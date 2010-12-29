package blender;

import org.lwjgl.opengl.GL11; 
import java.io.FileReader; 
import java.io.BufferedReader; 
import java.util.StringTokenizer; 
import org.lwjgl.BufferUtils; 
import java.nio.FloatBuffer; 
/** 
 * parseData() reads the exported Blender file and extracts the data 
 * for each key frame into a FrameData object. FrameData is a private 
 * inner class. The draw() method uses this data to draw the model exported. 
 * @author ste3e 
 */ 
public class MeshData { 
  private int[] keyframes;//array of key frame numbers 
  /* 
   * The FrameData object holds all the vertex data. The 
   * animations array holds a FrameData object for each 
   * key frame 
   */ 
  private FrameData[] animations; 
  private int currentFrame=0;//current frame of animation 
  private int animCount=0;//number of frames in the animation 
  private int vertCount=0;//number of vertices per frame 
  private int fps=0; 
  private int idx=-1;//used for parsing the data file 

  private int loopCounter=0;//used to play the animation 
  private int rot=10;//ditto 

  public MeshData(){} 
  /* 
   * The data is parsed over twice. First to get the size of arrays 
   * The arrays are initialized and filled in the second pass. 
   */ 
  public void parseData(String fileName){ 
    try{ 
      BufferedReader reader=new BufferedReader(new FileReader(fileName)); 
      String line=reader.readLine(); 
      StringTokenizer tok;
      /* 
       * First pass. 
       */ 
      while(line!=null){ 
        if(line.startsWith("?")){ 
          animCount++;//animCount = number of "?" in file 
        } 
        if(line.startsWith("$") && fps==0){ 
          tok=new StringTokenizer(line); 
          tok.nextToken(); 
          fps=Integer.parseInt(tok.nextToken()); 
        } 
        if(line.startsWith("v")){ 
          vertCount++; 
        } 
        line=reader.readLine(); 
      } 
    }catch(Exception e){ 
      System.out.println("Fail setting up counters"); 
    } 
    /* 
     * initialize arrays and declare a set of local arrays. Possibly 
     * this is not the most economic way of proceeding. But parseData 
     * should only need to be read once, if at all, as once the data 
     * is gathered from the text file it can be saved as a Java binary. 
     * I have not implemented this 
     */ 
    keyframes=new int[animCount]; 
    animations=new FrameData[animCount]; 
    float[] v=null; 
    float[] n=null; 
    float[] u=null; 
    float[] c=null; 
    /* 
     * Second pass. 
     */ 
    try{ 
      BufferedReader reader=new BufferedReader(new FileReader(fileName)); 
      String line=reader.readLine(); 
      StringTokenizer tok; 
      boolean virgin=true; 
      int i=0; 
      int ni=0; 
      int ui=0; 
      int ci=0; 

      /* 
       * Loop through lines of the file 
       */ 
      while(line!=null){ 
        /* 
         * The ? token occurs at the head of every animation frame 
         * of data. idx is initialized to -1 and increments to 0 
         * before any vertex data is read, and thereon increments 
         * every time we hit a new frame of animation data. For 
         * every frame of animation data we clear the float arrays 
         * ready to accept the data. If the loop has cycled once 
         * or more, there will be data in the float arrays, in 
         * which case we append a new FrameData object to the 
         * animations array. We hand the full float arrays 
         * to the FrameData object.                 
         */
        if (line.startsWith("?")){ 
          idx++; 
          if(idx>0){ 
            animations[idx-1]=new FrameData(v, n, u, c); 
          } 
          v=new float[vertCount*3]; 
          n=new float[vertCount*3]; 
          u=new float[vertCount*2]; 
          c=new float[vertCount*3]; 

          i=0; 
          ni=0; 
          ui=0; 
          ci=0; 
        } 
        /* 
         * v, n, u, c prefix vertex, normal, texture coord, and 
         * vertex color data. For each encounter with a prefix 
         * the tokenizer steps through the items on the line, 
         * converts them into float values, and appends them to 
         * their respective float arrays. 
         */ 
        if(line.startsWith("v")){ 
          tok=new StringTokenizer(line); 
          tok.nextToken(); 
          while(tok.hasMoreTokens()) 
            v[i++]=Float.parseFloat(tok.nextToken()); 
        } 
        if(line.startsWith("n")){ 
          tok=new StringTokenizer(line); 
          tok.nextToken(); 
          while(tok.hasMoreTokens()) 
            n[ni++]=Float.parseFloat(tok.nextToken()); 
        } 
        if(line.startsWith("u")){ 
          tok=new StringTokenizer(line); 
          tok.nextToken(); 
          while(tok.hasMoreTokens()){ 
            u[ui++]=Float.parseFloat(tok.nextToken()); 
          } 
        } 
        if(line.startsWith("c")){ 
          tok=new StringTokenizer(line); 
          tok.nextToken(); 
          while(tok.hasMoreTokens()){ 
            c[ci++]=Float.parseFloat(tok.nextToken()); 
          } 
        } 

        line=reader.readLine(); 
      } 
    }catch(Exception e){ 
      System.out.println("Fail setting up arrays"); 
    } 
    /* 
     * The loop will terminate once the file has been read. This 
     * will leave data in the float arrays that has not been appended 
     * to the animations array. So we append it. 
     */ 
    if(animCount==1) 
      animations[0]=new FrameData(v, n, u, c); 
    else 
      animations[idx]=new FrameData(v, n, u, c); 
  }
  /* 
   * We use glDrawArrays to draw the monkey. 
   */ 
  public void draw(){ 
    loopCounter++;//control animation change 
    GL11.glTranslatef(0.0f, -2.0f, -10.0f);//position in front of camera 
    GL11.glRotatef(rot++, 0.0f, 1.0f, 0.0f);//rotate 
    /* 
     * Enable gl arrays and set the pointer states with the FloatBuffers 
     * We change the state of the pointers as we proceed through the 
     * animation... that is, we get them to point to the data stored 
     * in the next animations array. 
     */ 
    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY); 
    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY); 
    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY); 
    GL11.glVertexPointer(3, 0, animations[currentFrame].getVerts()); 
    GL11.glNormalPointer(0, animations[currentFrame].getNorms()); 
    GL11.glTexCoordPointer(2, 0, animations[currentFrame].getCoords()); 
    GL11.glColorPointer(3, 0, animations[currentFrame].getCols()); 
    /* 
     * This single line of code does the work of all the 
     * glBegin(TRIANGLES)... glEnd() function calls normally used. 
     * It works because the state of the pointers has been pointed to 
     * the correct arrays. Given this state, glDrawArrays steps 
     * through the arrays using pointer math. 
     */ 
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertCount); 
    //a loop to handle animation change 
    if(loopCounter>24){ 
      loopCounter=0; 
      currentFrame++; 

    } 
    if(currentFrame==animCount){ 
      currentFrame=0; 

    } 
  } 


  /* 
   * FloatBuffers seem to be required to use glDrawArrays. The private 
   * inner class FrameData is basically a structure holding the 
   * required FloatBuffers. The animations array holds a new 
   * FrameData object for each frame in the animation. The class 
   * is final as the data is read from the file and remains immutable. 
   */ 
  private final class FrameData { 
    private FloatBuffer verts; 
    private FloatBuffer norms; 
    private FloatBuffer coords; 
    private FloatBuffer disp; 
    private FloatBuffer cols; 
    public FrameData(float[] v, float[] n, float[] u, float[] c){ 
      verts=BufferUtils.createFloatBuffer(v.length); 
      verts.put(v); 
      verts.rewind(); 
      norms=BufferUtils.createFloatBuffer(n.length); 
      norms.put(n);
      norms.rewind(); 
      coords=BufferUtils.createFloatBuffer(u.length); 
      coords.put(u); 
      coords.rewind(); 
      cols=BufferUtils.createFloatBuffer(c.length); 
      cols.put(c); 
      cols.rewind(); 
    } 
    //accessor methods 
    public FloatBuffer getVerts(){ 
      verts.rewind(); 
      return verts; 
    } 
    public FloatBuffer getNorms(){ 
      norms.rewind(); 
      return norms; 
    } 
    public FloatBuffer getCoords(){ 
      coords.rewind(); 
      return coords; 
    } 
    public FloatBuffer getDisp(){ 
      disp.rewind(); 
      return disp; 
    } 
    public FloatBuffer getCols(){ 
      cols.rewind(); 
      return cols; 
    } 
  } 
}
