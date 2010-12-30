package blender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 * A window to display the game in LWJGL.
 * Taken from the Asteroids LWJGL tutorial
 * Created by Kevin Glass
 * 
 * @author Kevin Glass
 */
/**
 * @author xabotage
 *
 */
public class GameWindow {
  /** The list of game states currently registered */
  private HashMap gameStates = new HashMap();
  /** The current state being rendered */
  private GameState currentState;

  /**
   * Create a new game window
   */
  public GameWindow() {
    try {
      // find out what the current bits per pixel of the desktop is
      int currentBpp = Display.getDisplayMode().getBitsPerPixel();
      // find a display mode at 800x600
      DisplayMode mode = findDisplayMode(800, 600, currentBpp);

      // if can't find a mode, notify the user the give up
      if (mode == null) {
        Sys.alert("Error", "800x600x"+currentBpp+" display mode unavailable");
        return;
      }

      // configure and create the LWJGL display
      Display.setTitle("LWJGL Blender Import Test");
      Display.setDisplayMode(mode);
      Display.setFullscreen(false);

      Display.create();

      // initialise the game states
      init();
    } catch (LWJGLException e) {
      e.printStackTrace();
      Sys.alert("Error", "Failed: "+e.getMessage());
    }
  }

  /**
   * Start the game
   */
  public void startGame() {
    // enter the game loop
    gameLoop();
  }	

  /**
   * Get the current time in milliseconds based on the LWJGL
   * high res system clock.
   * 
   * @return The time in milliseconds based on the LWJGL high res clock
   */
  private long getTime() {
    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
  }

  /**
   * Determine an available display that matches the specified 
   * paramaters.
   * 
   * @param width The desired width of the screen
   * @param height The desired height of the screen
   * @param bpp The desired colour depth (bits per pixel) of the screen
   * @return The display mode matching the requirements or null
   * if none could be found
   * @throws LWJGLException Indicates a failure interacting with the LWJGL
   * library.
   */
  private DisplayMode findDisplayMode(int width, int height, int bpp) throws LWJGLException {
    DisplayMode[] modes = Display.getAvailableDisplayModes();
    DisplayMode mode = null;

    for (int i=0;i<modes.length;i++) {
      if ((modes[i].getBitsPerPixel() == bpp) || (mode == null)) {
        if ((modes[i].getWidth() == width) && (modes[i].getHeight() == height)) {
          mode = modes[i];
        }
      }
    }

    return mode;
  }

  /**
   * Add a game state to this window. This state can be used via
   * its unique name.
   * 
   * @param state The state to be added
   * @see GameState.getName()
   */
  public void addState(GameState state) {
    if (currentState == null) {
      currentState = state;
    }

    gameStates.put(state.getName(), state);
  }

  /**
   * Initialise the window and the resources used for the game
   */
  public void init() {
    /*
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
    System.out.println("initialized");
    */


    // initialize our sound loader to determine if we can
    // play sounds on this system
    //SoundLoader.get().init();
    
    // run through some based OpenGL capability settings. Textures
    // enabled, back face culling enabled, depth texting is on,
    GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    // cull face is not enabled because it causes bad results
    //GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDepthFunc(GL11.GL_LEQUAL);
    GL11.glShadeModel(GL11.GL_SMOOTH);  

    // define the properties for the perspective of the scene
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glViewport(0,0,800,600);
    GL11.glLoadIdentity();		
    GLU.gluPerspective(45.0f, ((float) 800) / ((float) 600), 0.1f, 100.0f);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); 

    // add the two game states that build up our game, the menu
    // state allows starting of the game. The ingame state rendered
    // the asteroids and the player
    //addState(new MenuState());
    //addState(new InGameState());

    // add the test state for playing with various opengl drawings
    addState(new TestState());

    try {
      // initialze all the game states we've just created. This allows
      // them to load any resources they require
      Iterator states = gameStates.values().iterator();

      // loop through all the states that have been registered
      // causing them to initialise
      while (states.hasNext()) {
        GameState state = (GameState) states.next();

        state.init(this);
      }
    } catch (IOException e) {
      // if anything goes wrong, show an error message and then exit.
      // This is a bit abrupt but for the sake of this tutorial its
      // enough.
      Sys.alert("Error", "Unable to initialise state: " + e.getMessage());
      System.exit(0);
    }
  }

  /**
   * The main game loop which is cycled rendering and updating the
   * registered game states
   */
  public void gameLoop() {
    boolean gameRunning = true;
    long lastLoop = getTime();

    currentState.enter(this);

    // while the game is running we loop round updating and rendering
    // the current game state
    while (gameRunning) {
      // calculate how long it was since we last came round this loop
      // and hold on to it so we can let the updating/rendering routine
      // know how much time to update by
      int delta = (int) (getTime() - lastLoop);
      lastLoop = getTime();
      //System.out.println(delta);

      // clear the screen and the buffer used to maintain the appearance
      // of depth in the 3D world (the depth buffer)
      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
      GL11.glLoadIdentity();

      // cause the game state that we're currently running to update
      // based on the amount of time passed
      int remainder = delta % 10;
      int step = delta / 10;
      for (int i=0;i<step;i++) {
        currentState.update(this, 10);
      }
      if (remainder != 0) {
        currentState.update(this, remainder);
      }

      // cause the game state that we're currently running to be 
      // render
      currentState.render(this,delta);

      // finally tell the display to cause an update. We've now
      // rendered out scene we just want to get it on the screen
      // As a side effect LWJGL re-checks the keyboard, mouse and
      // controllers for us at this point
      Display.update();

      // if the user has requested that the window be closed, either
      // pressing CTRL-F4 on windows, or clicking the close button
      // on the window - then we want to stop the game
      if (Display.isCloseRequested()) {
        gameRunning = false;
        System.exit(0);
      }
    }
  }

  /**
   * Change the current state being rendered and updated. Note if 
   * no state with the specified name can be found no action is taken.
   * 
   * @param name The name of the state to change to.
   */
  public void changeToState(String name) {
    GameState newState = (GameState) gameStates.get(name);
    if (newState == null) {
      return;
    }

    currentState.leave(this);
    currentState = newState;
    currentState.enter(this);
  }

  /**
   * Enter the orthographic mode by first recording the current state, 
   * next changing us into orthographic projection.
   */
  public void enterOrtho() {
    // store the current state of the renderer
    GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT);
    GL11.glPushMatrix();
    GL11.glLoadIdentity();
    GL11.glMatrixMode(GL11.GL_PROJECTION); 
    GL11.glPushMatrix();	

    // now enter orthographic projection
    GL11.glLoadIdentity();		
    GL11.glOrtho(0, 800, 600, 0, -1, 1);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);  
  }

  /**
   * Leave the orthographic mode by restoring the state we store
   * in enterOrtho()
   * 
   * @see enterOrtho()
   */
  public void leaveOrtho() {
    // restore the state of the renderer
    GL11.glPopMatrix();
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glPopMatrix();
    GL11.glPopAttrib();
  }

  /**
   * The entry point into our game. This method is called when you
   * execute the program. Its simply responsible for creating the
   * game window
   * 
   * @param argv The command line arguments provided to the program
   */
  public static void main(String argv[]) {
    GameWindow g = new GameWindow();
    g.startGame();
  }
}
