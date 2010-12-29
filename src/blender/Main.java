package blender;

import org.lwjgl.opengl.Display; 
import org.lwjgl.opengl.DisplayMode; 
import org.lwjgl.opengl.GL11; 
import org.lwjgl.util.glu.GLU; 


/* 
 * Sets up the Display, the GL context, and runs the main game loop. 
 * 
 * @author ste3e 
 */ 
public class Main { 
    private Game game; 
    private boolean done=false;//game runs until done is set to true 
    
    /* 
     * The entire game is prefigured in Main(). init() sets up the 
     * Display and GL context. A Game object is constructed which 
     * will handle setting up the game world. The while loop 
     * is the game loop. 
     * @author Stephen Jones 
     */ 
    public Main(){ 
        init(); 
        game=new Game(); 
        
        /* 
         * game loop is finished when window's close button is clicked. 
         * The Display.update() call draws to the screen. Render 
         * is called every iteration of the loop and is where 
         * the animation will be drawn. Display.update() draws 
         * whatever was specified in render to screen. Once the 
         * loop ends the Display is destroyed. 
         */ 
        while(!done){ 
            if(Display.isCloseRequested()) 
                done=true; 
            render(); 
            Display.update(); 
        } 
        Display.destroy(); 
    } 

    /* 
     * Clear the screen to the clear color. game.tick() passes the 
     * render call through the game. 
     */ 
    private void render(){ 
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 
        GL11.glLoadIdentity(); 
        
        game.tick(); 
    } 
    private void init(){ 
        int w=1024; 
        int h=768; 
        try{ 
            Display.setDisplayMode(new DisplayMode(w, h)); 
            Display.setVSyncEnabled(true); 
            Display.setTitle("Loading Animation from Blender 2.5"); 
            Display.create(); 
        }catch(Exception e){ 
            System.out.println("Error setting up display"); 
            System.exit(0);
        } 
        GL11.glViewport(0,0,w,h); 
        GL11.glMatrixMode(GL11.GL_PROJECTION); 
        GL11.glLoadIdentity(); 
        GLU.gluPerspective(45.0f, ((float)w/(float)h),0.1f,100.0f); 
        GL11.glMatrixMode(GL11.GL_MODELVIEW); 
        GL11.glLoadIdentity(); 
        GL11.glShadeModel(GL11.GL_SMOOTH); 
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); 
        GL11.glClearDepth(1.0f); 
        GL11.glEnable(GL11.GL_DEPTH_TEST); 
        GL11.glDepthFunc(GL11.GL_LEQUAL); 
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); 
    } 
    public static void main(String[] args){ 
        new Main(); 
    } 
} 
