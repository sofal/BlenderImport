package blender;

/** 
 * The constructor is called before the game loop is entered and provides 
 * opportunity for setting up the game world. It is intended that the Game 
 * class will be a hub for events, collisions and game logic. It will hold 
 * instances of objects in the game world and determine how they respond. 
 * In this simple instance Game sets up a Monkey object and calls its draw 
 * method every game loop. 
 * @author ste3e 
 */ 
public class Game { 
    private Monkey myMonkey; 
    private Monkey two; 
    public Game() { 
        myMonkey=new Monkey("assets/Data.txt"); 
        two=new Monkey("assets/Data.txt"); 
    } 
    public void tick() { 
        myMonkey.draw(); 
    } 
} 
