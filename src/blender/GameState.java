package blender;
import java.io.IOException;

/**
 * A state in which the game can reside. Game states encapsulate 
 * sections of the game to help modularising everything.
 * Taken from the LWJGL Asteroids tutorial created by Kevin Glass
 * 
 * @author Kevin Glass
 */
public interface GameState {
	/**
	 * Retrieve a name that can be used to identify this state
	 * within the game. This name is used to allow swapping between
	 * states.
	 * 
	 * @return The name of this state
	 */
	public String getName();
	
	/**
	 * Initialise the game state. All texture loading, model loading etc
	 * should be performed here.
	 * 
	 * @param window The game window in which this state is being displayed
	 * @throws IOException Indicates a failure to obtain resources required
	 * for initialisation.
	 */
	public void init(GameWindow window) throws IOException;
	
	/**
	 * Render this game state to the screen.
	 * 
	 * @param window The window in which the state is being rendered
	 * @param delta The amount of time thats passed since last render
	 */
	public void render(GameWindow window, int delta);
	
	/**
	 * Update this game state based on a given amount of time
	 * 
	 * @param window The window holding this sate
	 * @param delta The amount time to update the state by
	 */
	public void update(GameWindow window, int delta);
	
	/**
	 * Notification that this game state is being entered
	 * 
	 * @param window The window holding this state
	 */
	public void enter(GameWindow window);

	/**
	 * Notification that this game state is being left
	 * 
	 * @param window The window holding this state
	 */
	public void leave(GameWindow window);
}
