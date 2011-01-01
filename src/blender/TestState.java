package blender;

import java.io.IOException;
import org.lwjgl.opengl.GL11;
//import blender.MeshData;


public class TestState implements GameState {

  private Mesh _mesh;

  /**
   * Retrieve a name that can be used to identify this state
   * within the game. This name is used to allow swapping between
   * states.
   * 
   * @return The name of this state
   */
  public String getName() {
    return "Blender Import Test";
  }
  
  /**
   * Initialise the game state. All texture loading, model loading etc
   * should be performed here.
   * 
   * @param window The game window in which this state is being displayed
   * @throws IOException Indicates a failure to obtain resources required
   * for initialisation.
   */
  public void init(GameWindow window) throws IOException {
    //_mesh = new MeshData(); // quick-load a mesh
    //_mesh.parseData("assets/Data.txt");


    // testing binary loader
    _mesh = new Mesh("test"); // quick-load new mesh class
    ActionLoader.importAction("assets/BinData.txt", "test_action", _mesh); 
  }
  
  /**
   * Render this game state to the screen.
   * 
   * @param window The window in which the state is being rendered
   * @param delta The amount of time thats passed since last render
   */
  public void render(GameWindow window, int delta) {
    _mesh.render(window, delta);
    //_mesh.draw();
  }
  
  /**
   * Update this game state based on a given amount of time
   * 
   * @param window The window holding this sate
   * @param delta The amount time to update the state by
   */
  public void update(GameWindow window, int delta) {
    _mesh.update(window, delta);
  }
  
  /**
   * Notification that this game state is being entered
   * 
   * @param window The window holding this state
   */
  public void enter(GameWindow window) {
  }

  /**
   * Notification that this game state is being left
   * 
   * @param window The window holding this state
   */
  public void leave(GameWindow window) {
  }
}
