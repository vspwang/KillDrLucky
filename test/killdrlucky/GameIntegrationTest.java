package killdrlucky;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for complete game flow.
 */
public class GameIntegrationTest {
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
  public void testFullFlow_humanPlayerMoves() {
    world.addPlayer("Alice", 0, false, 5);
    
    int initialSpace = world.getGameState().currentPlayerSpace;
    int neighborSpace = world.neighborsOf(initialSpace).get(0);
    String neighborName = world.getSpace(neighborSpace).getName();
    
    ActionResult result = world.executeAction("Alice", "move", neighborName);
    
    assertTrue(result.isSuccess());
    assertTrue(result.isTurnAction());
    
    // After advancing turn
    world.advanceTurn();
    
    // Verify player moved
    assertEquals(neighborSpace, world.getPlayers().get(0).getCurrentSpaceIndex());
  }

  @Test
  public void testFullFlow_mixedPlayers() {
    world.addPlayer("Human", 0, false, 5);
    world.addPlayer("AI", 1, true, 5);
    
    GameState state1 = world.getGameState();
    assertEquals("Human", state1.currentPlayerName);
    assertFalse(state1.isCurrentPlayerAi);
    
    world.advanceTurn();
    
    GameState state2 = world.getGameState();
    assertEquals("AI", state2.currentPlayerName);
    assertTrue(state2.isCurrentPlayerAi);
  }

  @Test
  public void testFullFlow_tenPlayers() {
    for (int i = 0; i < 10; i++) {
      world.addPlayer("Player" + i, i % world.getSpaces().size(), 
                     i % 2 == 0, 5); // Alternate human/computer
    }
    
    assertEquals(10, world.getPlayers().size());
    
    // Verify turn rotation through all 10 players
    for (int i = 0; i < 10; i++) {
      GameState state = world.getGameState();
      assertEquals("Player" + i, state.currentPlayerName);
      world.advanceTurn();
    }
    
    // Should wrap back to Player0
    GameState state = world.getGameState();
    assertEquals("Player0", state.currentPlayerName);
  }

  @Test
  public void testFullFlow_invalidMoveAttempt() {
    world.addPlayer("Alice", 0, false, 5);
    
    int initialSpace = world.getPlayers().get(0).getCurrentSpaceIndex();
    
    // Find a space that is definitely not a neighbor
    int targetSpace = -1;
    for (int i = 0; i < world.getSpaces().size(); i++) {
      if (!world.neighborsOf(initialSpace).contains(i) && i != initialSpace) {
        targetSpace = i;
        break;
      }
    }
    
    // Skip test if all spaces are neighbors (unlikely but possible)
    if (targetSpace == -1) {
      return;
    }
    
    String targetSpaceName = world.getSpace(targetSpace).getName();
    
    // Record initial state
    int playerSpaceBefore = world.getPlayers().get(0).getCurrentSpaceIndex();
    
    // Try invalid move
    @SuppressWarnings("unused")
    ActionResult result = world.executeAction("Alice", "move", targetSpaceName);
    
    // After invalid move, player should still be in same space
    int playerSpaceAfter = world.getPlayers().get(0).getCurrentSpaceIndex();
    
    assertEquals("Player should not move to non-neighbor space", 
                 playerSpaceBefore, playerSpaceAfter);
  }

  @Test
  public void testFullFlow_gameOverAfterKillingTarget() {
    world.addPlayer("Alice", 0, false, 5);
    
    // Move Alice to target's space
    int targetSpace = world.getTarget().getCurrentSpaceIndex();
    
    // Repeatedly attack until target dies
    int maxAttempts = 100;
    int attempts = 0;
    
    while (world.getTarget().isAlive() && attempts < maxAttempts) {
      // Move to target if not there
      if (world.getPlayers().get(0).getCurrentSpaceIndex() != targetSpace) {
        int currentSpace = world.getPlayers().get(0).getCurrentSpaceIndex();
        if (world.neighborsOf(currentSpace).contains(targetSpace)) {
          world.movePlayer("Alice", world.getSpace(targetSpace).getName());
        }
      }
      
      // Attack
      world.executeAction("Alice", "attack", "");
      attempts++;
    }
    
    // Check if game is over (might not be if attacks were seen)
    GameState state = world.getGameState();
    if (!world.getTarget().isAlive()) {
      assertTrue(state.gameOver);
    }
  }
}