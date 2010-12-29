package blender;

/** 
 * The idea is that many instances of Monkeys can exist in the 
 * game world but they will all share the same instance of MeshData. 
 * MeshData holds the animations via different meshes 
 * @author ste3e 
 */ 
public class Monkey{ 
    private static MeshData data; 
    /* 
     * The constructor creates a new MeshData object then calls 
     * its parseData() method.
     */ 
    public Monkey(String fileName){ 
        data=new MeshData(); 
        data.parseData(fileName); 
    } 
    /* 
     * This is the draw method initially called via render() in the 
     * main game loop. 
     */ 
    public void draw(){ 
        data.draw(); 
    } 
} 
