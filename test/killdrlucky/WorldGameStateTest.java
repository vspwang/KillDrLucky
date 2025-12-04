package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for World.getGameState() method.
 */
public class WorldGameStateTest {
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
  public void testGetGameState_noPlayers() {
    GameState state = world.getGameState();
    
    assertEquals("", state.currentPlayerName);
    assertFalse(state.isCurrentPlayerAi);
    assertEquals(0, state.currentPlayerSpace);
    assertFalse(state.gameOver);
  }

  @Test
  public void testGetGameState_firstPlayer() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 1, false, 5);
    
    GameState state = world.getGameState();
    
    assertEquals("Alice", state.currentPlayerName);
  }

  @Test
  public void testGetGameState_secondPlayer() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 1, false, 5);
    
    world.advanceTurn();
    GameState state = world.getGameState();
    
    assertEquals("Bob", state.currentPlayerName);
  }

  @Test
  public void testGetGameState_humanPlayer() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state = world.getGameState();
    
    assertFalse(state.isCurrentPlayerAi);
  }

  @Test
  public void testGetGameState_computerPlayer() {
    world.addPlayer("AI1", 0, true, 5);
    
    GameState state = world.getGameState();
    
    assertTrue(state.isCurrentPlayerAi);
  }

  @Test
  public void testGetGameState_playerLocation() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state = world.getGameState();
    
    assertEquals(0, state.currentPlayerSpace);
    assertEquals(world.getSpace(0).getName(), state.currentPlayerLocation);
  }

  @Test
  public void testGetGameState_targetInfo() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state = world.getGameState();
    
    assertEquals(world.getTarget().getCurrentSpaceIndex(), state.targetSpace);
    assertEquals(world.getTarget().getHealth(), state.targetHealth);
  }

  @Test
  public void testGetGameState_petInfo() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state = world.getGameState();
    
    assertEquals(world.getPet().getCurrentSpaceIndex(), state.petSpace);
  }

  @Test
  public void testGetGameState_gameNotOver() {
    world.addPlayer("Alice", 0, false, 5);
    
    GameState state = world.getGameState();
    
    assertFalse(state.gameOver);
    assertEquals("", state.winner);
  }
}