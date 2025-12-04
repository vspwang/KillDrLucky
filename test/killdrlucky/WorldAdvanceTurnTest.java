package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for World.advanceTurn() method.
 */
public class WorldAdvanceTurnTest {
  private World world;
  
  /**
   * Sets up the test environment before each test method.
   * Creates a new game world by parsing the mansion configuration file
   * and initializing the visibility strategy.
   * This method is executed before each test to ensure a consistent starting state.
   *
   * @throws IOException if the mansion.txt file cannot be read or parsed
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of("res/mansion.txt"));
    VisibilityStrategy strategy = new AxisAlignedVisibility();
    world = new World(data, strategy);
  }

  @Test
  public void testAdvanceTurn_twoPlayers() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 1, false, 5);
    
    GameState state1 = world.getGameState();
    assertEquals("Alice", state1.currentPlayerName);
    
    world.advanceTurn();
    
    GameState state2 = world.getGameState();
    assertEquals("Bob", state2.currentPlayerName);
  }

  @Test
  public void testAdvanceTurn_wraparound() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 1, false, 5);
    
    world.advanceTurn(); // Alice -> Bob
    world.advanceTurn(); // Bob -> Alice
    
    GameState state = world.getGameState();
    assertEquals("Alice", state.currentPlayerName);
  }

  @Test
  public void testAdvanceTurn_threePlayers() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 1, false, 5);
    world.addPlayer("Charlie", 2, false, 5);
    
    world.advanceTurn(); // Alice -> Bob
    
    GameState state = world.getGameState();
    assertEquals("Bob", state.currentPlayerName);
    
    world.advanceTurn(); // Bob -> Charlie
    
    GameState state2 = world.getGameState();
    assertEquals("Charlie", state2.currentPlayerName);
  }

  @Test
  public void testAdvanceTurn_singlePlayer() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state1 = world.getGameState();
    assertEquals("Alice", state1.currentPlayerName);
    
    world.advanceTurn();
    
    GameState state2 = world.getGameState();
    assertEquals("Alice", state2.currentPlayerName);
  }

  @Test
  public void testAdvanceTurn_noPlayers() {
    // Should not throw exception
    world.advanceTurn();
    
    GameState state = world.getGameState();
    assertEquals("", state.currentPlayerName);
  }

  @Test
  public void testAdvanceTurn_multipleCalls() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 1, false, 5);
    
    // Advance 5 times: Alice -> Bob -> Alice -> Bob -> Alice
    String[] expected = {"Alice", "Bob", "Alice", "Bob", "Alice"};
    
    for (int i = 0; i < 5; i++) {
      GameState state = world.getGameState();
      assertEquals(expected[i], state.currentPlayerName);
      world.advanceTurn();
    }
  }
}