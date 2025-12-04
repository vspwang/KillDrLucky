package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for winner tracking in World.
 */
public class WorldWinnerTrackingTest {
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
  public void testWinnerTracking_noWinnerInitially() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state = world.getGameState();
    
    assertEquals("", state.winner);
  }

  @Test
  public void testWinnerTracking_afterSuccessfulAttack() {
    world.addPlayer("Alice", 0, false, 5);
    
    int targetSpace = world.getTarget().getCurrentSpaceIndex();
    
    if (targetSpace != 0) {
      world.movePlayer("Alice", world.getSpace(targetSpace).getName());
    }
    
    int initialHealth = world.getTarget().getHealth();
    
    for (int i = 0; i < initialHealth; i++) {
      @SuppressWarnings("unused")
      String result = world.attackTarget("Alice", "");
      if (!world.getTarget().isAlive()) {
        break;
      }
    }
    
    if (!world.getTarget().isAlive()) {
      GameState state = world.getGameState();
      assertTrue(state.gameOver);
      assertEquals("Alice", state.winner);
    }
  }

  @Test
  public void testWinnerTracking_computerPlayerWins() {
    world.addPlayer("AI1", 0, true, 5);
    
    int targetSpace = world.getTarget().getCurrentSpaceIndex();
    
    if (targetSpace != 0) {
      world.movePlayer("AI1", world.getSpace(targetSpace).getName());
    }
    
    int initialHealth = world.getTarget().getHealth();
    
    for (int i = 0; i < initialHealth; i++) {
      @SuppressWarnings("unused")
      String result = world.attackTarget("AI1", "");
      if (!world.getTarget().isAlive()) {
        break;
      }
    }
    
    if (!world.getTarget().isAlive()) {
      GameState state = world.getGameState();
      assertEquals("AI1", state.winner);
    }
  }
}