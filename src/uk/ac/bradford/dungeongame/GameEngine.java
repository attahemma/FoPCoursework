package uk.ac.bradford.dungeongame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * The GameEngine class is responsible for managing information about the game,
 * creating levels, the player and monsters, as well as updating information
 * when a key is pressed while the game is running.
 * @author prtrundl
 */
public class GameEngine {
    
    public Point playerPosition;
    public Point monsterOnePosition;
    public Point monsterTwoPosition;
    public Point monsterThreePosition;
    public Point monsterFourPosition;
    
    
    int[][] tileMap;
    public void generateMap(int row, int col){
        tileMap = new int[row][col];
        Random r = new Random();
        int randInt = 0;
        for (int i = 0; i<tileMap.length; i++){
            for (int j=0; j<tileMap[i].length; j++){
                randInt = r.nextInt(3);
                tileMap[j][i] += randInt;
                System.out.print(tileMap[j][i]);
            }
            System.out.println();
        }
    }

    int[][] map = {
        {1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,1,1,0,0,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };
    
    /**
     * An enumeration type to represent different types of tiles that make up
     * a dungeon level. Each type has a corresponding image file that is used
     * to draw the right tile to the screen for each tile in a level. Floors are
     * open for monsters and the player to move into, walls should be impassable,
     * stairs allow the player to progress to the next level of the dungeon, and
     * chests can yield a reward when moved over.
     */
    public enum TileType {
        WALL, FLOOR, CHEST, STAIRS
    }

    /**
     * The width of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_WIDTH = 25;
    
    /**
     * The height of the dungeon level, measured in tiles. Changing this may
     * cause the display to draw incorrectly, and as a minimum the size of the
     * GUI would need to be adjusted.
     */
    public static final int DUNGEON_HEIGHT = 18;
    
    /**
     * The maximum number of monsters that can be generated on a single level
     * of the dungeon. This attribute can be used to fix the size of an array
     * (or similar) that will store monsters.
     */
    public static final int MAX_MONSTERS = 40;
    
    /**
     * The chance of a wall being generated instead of a floor when generating
     * the level. 1.0 is 100% chance, 0.0 is 0% chance.
     */
    public static final double WALL_CHANCE = 0.05;

    /**
     * A random number generator that can be used to include randomised choices
     * in the creation of levels, in choosing places to spawn the player and
     * monsters, and to randomise movement and damage. This currently uses a seed
     * value of 123 to generate random numbers - this helps you find bugs by
     * giving you the same numbers each time you run the program. Remove
     * the seed value if you want different results each game.
     */
    private Random rng = new Random(123);

    /**
     * The current level number for the dungeon. As the player moves down stairs
     * the level number should be increased and can be used to increase the
     * difficulty e.g. by creating additional monsters with more health.
     */
    private int depth = 1;  //current dunegeon level

    /**
     * The GUI associated with a GameEngine object. THis link allows the engine
     * to pass level (tiles) and entity information to the GUI to be drawn.
     */
    private GameGUI gui;

    /**
     * The 2 dimensional array of tiles the represent the current dungeon level.
     * The size of this array should use the DUNGEON_HEIGHT and DUNGEON_WIDTH
     * attributes when it is created.
     */
    private TileType[][] tiles;
    
    /**
     * An ArrayList of Point objects used to create and track possible locations
     * to spawn the player and monsters.
     */
    private ArrayList<Point> spawns;

    /**
     * An Entity object that is the current player. This object stores the state
     * information for the player, including health and the current position (which
     * is a pair of co-ordinates that corresponds to a tile in the current level)
     */
    private Entity player;
    
    /**
     * An array of Entity objects that represents the monsters in the current
     * level of the dungeon. Elements in this array should be of the type Entity,
     * meaning that a monster is alive and needs to be drawn or moved, or should
     * be null which means nothing is drawn or processed for movement.
     * Null values in this array are skipped during drawing and movement processing.
     * Monsters (Entity objects) that die due to player attacks can be replaced
     * with the value null in this array which removes them from the game.
     */
    private Entity[] monsters;

    /**
     * Constructor that creates a GameEngine object and connects it with a GameGUI
     * object.
     * @param gui The GameGUI object that this engine will pass information to in
     * order to draw levels and entities to the screen.
     */
    public GameEngine(GameGUI gui) {
        this.gui = gui;
        startGame();
    }

    /**
     * Generates a new dungeon level. The method builds a 2D array of TileType values
     * that will be used to draw tiles to the screen and to add a variety of
     * elements into each level. Tiles can be floors, walls, stairs (to progress
     * to the next level of the dungeon) or chests. The method should contain
     * the implementation of an algorithm to create an interesting and varied
     * level each time it is called.
     * @return A 2D array of TileTypes representing the tiles in the current
     * level of the dungeon. The size of this array should use the width and
     * height of the dungeon.
     */
    private TileType[][] generateLevel() {
        //Add your code here to build a 2D array containing TileType values to create a level
        //return the 2D array
        //generateMap(DUNGEON_HEIGHT,DUNGEON_WIDTH);
        TileType[][] tiles = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];
        for(int row = 0; row < tiles.length; row++){
            for (int col = 0; col<tiles[row].length; col++){
                /*switch(tileMap[col][row]){
                    case 0:
                        tiles[row][col] = TileType.FLOOR;
                        break;
                    case 1:
                        tiles[row][col] = TileType.WALL;
                        break;
                    case 2:
                        tiles[row][col] = TileType.CHEST;
                        break;
                    default:
                        tiles[row][col]=TileType.STAIRS;
                        break;
                }*/
            }
        }
        return tiles;
    }
    
    private TileType[][] generateLevel(int[][] newMap){
        TileType[][] tiles = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];
        for(int row = 0; row < tiles.length; row++){
            for (int col = 0; col<tiles[row].length; col++){
                switch(newMap[col][row]){
                    case 0:
                        tiles[row][col] = TileType.FLOOR;
                        break;
                    case 1:
                        tiles[row][col] = TileType.WALL;
                        break;
                    case 2:
                        tiles[row][col] = TileType.CHEST;
                        break;
                    default:
                        tiles[row][col]=TileType.STAIRS;
                        break;
                }
            }
        }
        return tiles;
    }
    
    /**
     * Generates spawn points for the player and monsters. The method processes
     * the tiles array and finds tiles that are suitable for spawning, i.e.
     * tiles that are not walls or stairs. Suitable tiles should be added
     * to the ArrayList that will contain Point objects - Points are a
     * simple kind of object that contain an X and a Y co-ordinate stored using
     * the int primitive type and are part of the Java language (search for the
     * Point API documentation and examples of their use)
     * @return An ArrayList containing Point objects representing suitable X and
     * Y co-ordinates in the current level that the player or monsters can be
     * spawned in
     */
    private ArrayList<Point> getSpawns() {
        ArrayList<Point> s = new ArrayList<Point>();
        s.add(playerPosition);
        //Add code here to find tiles in the level array that are suitable spawn points
        //Add these points to the ArrayList s
        return s;   
    }

    /**
     * Spawns monsters in suitable locations in the current level. The method
     * uses the spawns ArrayList to pick suitable positions to add monsters,
     * removing these positions from the spawns ArrayList as they are used
     * (using the remove() method) to avoid multiple monsters spawning in the
     * same location. The method creates monsters by instantiating the Entity
     * class, setting health, and setting the X and Y position for the monster
     * using the X and Y values in the Point object removed from the spawns ArrayList.
     * @return A array of Entity objects representing the monsters for the current
     * level of the dungeon
     */
    private Entity[] spawnMonsters() {
        Entity[] monsters = new Entity[4];
        monsterOnePosition = new Point(5,4); 
        monsters[0] = new Entity(100, monsterOnePosition.x, monsterOnePosition.y, Entity.EntityType.MONSTER);
        monsterTwoPosition = new Point(5,4); 
        monsters[1] = new Entity(100, monsterTwoPosition.x, monsterTwoPosition.y, Entity.EntityType.MONSTER);
        monsterThreePosition = new Point(12,3); 
        monsters[2] = new Entity(100, monsterThreePosition.x, monsterThreePosition.y, Entity.EntityType.MONSTER);
        monsterFourPosition = new Point(5,4); 
        monsters[3] = new Entity(100, monsterFourPosition.x, monsterFourPosition.y, Entity.EntityType.MONSTER);
        return null;    //Should be changed to return an array of monsters instead of null
    }

    /**
     * Spawns a player entity in the game. The method uses the spawns ArrayList
     * to select a suitable location to spawn the player and removes the Point
     * from the spawns ArrayList. The method instantiates the Entity class and
     * assigns values for the health, position and type of Entity.
     * @return An Entity object representing the player in the game
     */
    private Entity spawnPlayer() {
        //return null;    //Should be changed to return an Entity (the player) instead of null
        playerPosition = new Point(3,1);
        return new Entity(12, playerPosition.x, playerPosition.y, Entity.EntityType.PLAYER);
    }

    /**
     * Handles the movement of the player when attempting to move left in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the left arrow key on the keyboard. The method checks
     * whether the tile to the left of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile to the left of the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile to the left, or breaking a wall etc.
     */
    public void movePlayerLeft() {
        
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the right arrow key on the keyboard. The method checks
     * whether the tile to the right of the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile to the right of the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile to the right, or breaking a wall etc.
     */
    public void movePlayerRight() {
        
    }

    /**
     * Handles the movement of the player when attempting to move up in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the up arrow key on the keyboard. The method checks
     * whether the tile above the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile above the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile above the player, or breaking a wall etc.
     */
    public void movePlayerUp() {
        
    }

    /**
     * Handles the movement of the player when attempting to move right in the
     * game. This method is called by the DungeonInputHandler class when the
     * user has pressed the down arrow key on the keyboard. The method checks
     * whether the tile below the player is empty for movement and if
     * it is updates the player object's X and Y locations with the new position.
     * If the tile below the player is not empty the method will not
     * update the player position, but may make other changes to the game, such
     * as damaging a monster in the tile below the player, or breaking a wall etc.
     */
    public void movePlayerDown() {
        
    }

    /**
     * Reduces a monster's health in response to the player attempting to move
     * into the same square as the monster (attacking the monster).
     * @param m The Entity which is the monster that the player is attacking
     */
    private void hitMonster(Entity m) {
        
    }

    /**
     * Moves all monsters on the current level. The method processes all non-null
     * elements in the monsters array and calls the moveMonster method for each one.
     */
    private void moveMonsters() {
        
    }

    /**
     * Moves a specific monster in the game. The method updates the X and Y
     * attributes of the monster Entity to reflect its new position.
     * @param m The Entity (monster) that needs to be moved
     */
    private void moveMonster(Entity m) {
        
    }

    /**
     * Reduces the health of the player when hit by a monster - a monster next
     * to the player can attack it instead of moving and should call this method
     * to reduce the player's health
     */
    private void hitPlayer() {
        
    }

    /**
     * Processes the monsters array to find any Entity in the array with 0 or
     * less health. Any Entity in the array with 0 or less health should be
     * set to null; when drawing or moving monsters the null elements in the
     * monsters array are skipped.
     */
    private void cleanDeadMonsters() {
        
    }

    /**
     * Called in response to the player moving into a Stair tile in the game.
     * The method increases the dungeon depth, generates a new level by calling
     * the generateLevel method, fills the spawns ArrayList with suitable spawn
     * locations and spawns monsters. Finally it places the player in the new
     * level by calling the placePlayer() method. Note that a new player object
     * should not be created here unless the health of the player should be reset.
     */
    private void descendLevel() {
        
    }

    /**
     * Places the player in a dungeon level by choosing a spawn location from the
     * spawns ArrayList, removing the spawn position as it is used. The method sets
     * the players position in the level by calling its setPosition method with the
     * x and y values of the Point taken from the spawns ArrayList.
     */
    private void placePlayer() {
        
    }

    /**
     * Performs a single turn of the game when the user presses a key on the
     * keyboard. The method cleans dead monsters, moves any monsters still alive
     * and then checks if the player is dead, exiting the game or resetting it
     * after an appropriate output to the user is given. It checks if the player
     * moved into a stair tile and calls the descendLevel method if it does.
     * Finally it requests the GUI to redraw the game level by passing it the
     * tiles, player and monsters for the current level.
     */
    public void doTurn() {
        cleanDeadMonsters();
        moveMonsters();
        if (player != null) {       //checks a player object exists
            if (player.getHealth() < 1) {
                System.exit(0);     //exits the game when player is dead
            }
            if (tiles[player.getX()][player.getY()] == TileType.STAIRS) {
                descendLevel();     //moves to next level if the player is on Stairs
            }
        }
        gui.updateDisplay(tiles, player, monsters);     //updates GUI
    }

    /**
     * Starts a game. This method generates a level, finds spawn positions in
     * the level, spawns monsters and the player and then requests the GUI to
     * update the level on screen using the information on tiles, player and
     * monsters.
     */
    public void startGame() {
        //tiles = generateLevel();
        generateMap(DUNGEON_WIDTH,DUNGEON_HEIGHT);
        tiles = generateLevel(tileMap);
        spawns = getSpawns();
        monsters = spawnMonsters();
        player = spawnPlayer();
        gui.updateDisplay(tiles, player, monsters);
    }
}
