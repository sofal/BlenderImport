package blender;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
//import blender.MeshData;


public class TestState implements GameState {

  private Mesh _mesh;

  private FPCamera _camera;

  private float _mouseSensitivity;
  private boolean _invertMouse;
  private float _movementSpeed;

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

    _camera = new FPCamera(0, 0, 0);

    Mouse.setGrabbed(true);
    _mouseSensitivity = 0.15f;
    _invertMouse = true; // mwahaha
    _movementSpeed = 0.01f;

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
    _camera.lookThrough();
    _mesh.render(window, delta);
  }
  
  /**
   * Update this game state based on a given amount of time
   * 
   * @param window The window holding this sate
   * @param delta The amount time to update the state by
   */
  public void update(GameWindow window, int delta) {
    if (Mouse.isGrabbed()) {
      float dx = Mouse.getDX();
      float dy = Mouse.getDY();
      _camera.yawInc(dx * _mouseSensitivity);
      _camera.pitchInc((_invertMouse ? dy : -dy) * _mouseSensitivity);
    }

    if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
      _camera.walkForward(_movementSpeed * delta);
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
      _camera.walkBackward(_movementSpeed * delta);
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
      _camera.strafeLeft(_movementSpeed * delta);
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
      _camera.strafeRight(_movementSpeed * delta);
    }

    // TODO: This doesn't work right because it runs many times per second.
    if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
      Mouse.setGrabbed(!Mouse.isGrabbed());
    }

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
