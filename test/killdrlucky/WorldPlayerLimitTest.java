package killdrlucky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for World player limit (max 10 players).
 */
public class WorldPlayerLimitTest {
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
  public void testAddPlayer_first() {
    world.addPlayer("Alice", 0, false, 5);
    
    assertEquals(1, world.getPlayers().size());
    assertEquals("Alice", world.getPlayers().get(0).getName());
  }

  @Test
  public void testAddPlayer_tenth() {
    // Add 9 players
    for (int i = 0; i < 9; i++) {
      world.addPlayer("Player" + i, i % world.getSpaces().size(), false, 5);
    }
    
    assertEquals(9, world.getPlayers().size());
    
    // Add 10th player
    world.addPlayer("Player9", 0, false, 5);
    
    assertEquals(10, world.getPlayers().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPlayer_eleventh() {
    // Add 10 players
    for (int i = 0; i < 10; i++) {
      world.addPlayer("Player" + i, i % world.getSpaces().size(), false, 5);
    }
    
    assertEquals(10, world.getPlayers().size());
    
    // Try to add 11th player - should throw exception
    world.addPlayer("Player10", 0, false, 5);
  }

  @Test
  public void testAddPlayer_eleventhMessageCheck() {
    // Add 10 players
    for (int i = 0; i < 10; i++) {
      world.addPlayer("Player" + i, i % world.getSpaces().size(), false, 5);
    }
    
    try {
      world.addPlayer("Player10", 0, false, 5);
      fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().toLowerCase().contains("maximum"));
      assertTrue(e.getMessage().contains("10"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPlayer_duplicateNameWithMax() {
    // Add 10 players
    for (int i = 0; i < 10; i++) {
      world.addPlayer("Player" + i, i % world.getSpaces().size(), false, 5);
    }
    
    // Try to add duplicate name (should fail for duplicate, not max)
    world.addPlayer("Player0", 0, false, 5);
  }
}