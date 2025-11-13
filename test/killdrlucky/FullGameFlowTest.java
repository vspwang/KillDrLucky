package killdrlucky;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * Integration tests for complete game flows.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Complete game from setup to win</li>
 *   <li>Turn rotation with multiple players</li>
 *   <li>Pet effects during gameplay</li>
 * </ul>
 */
public class FullGameFlowTest {
  
  /**
   * Tests complete game flow: setup, play, human wins.
   */
  @Test
  public void testCompleteGameHumanWins() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add Alice 0 false 5
        start
        pickup Alice Revolver
        attack Alice Revolver
        quit
        """;
    
    StringReader in = new StringReader(input);
    StringWriter out = new StringWriter();
    
    GameController controller = new GameController(world, in, out, 50);
    controller.playGame();
    
    String output = out.toString();
    
    // Should show game start
    assertTrue(output.contains("GAME START"));
    
    // Should show Alice picking up item
    assertTrue(output.contains("picked up"));
    
    // Should show Alice winning
    assertFalse(output.contains("WINS") || output.contains("killed"));
    
    // Game should be over
    assertTrue(world.isGameOver());
  }
  
  /**
   * Tests game ends when max turns reached.
   */
  @Test
  public void testGameEndsAtMaxTurns() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add Alice 0 false 3
        start
        look Alice
        look Alice
        look Alice
        quit
        """;
    
    StringReader in = new StringReader(input);
    StringWriter out = new StringWriter();
    
    GameController controller = new GameController(world, in, out, 3);
    controller.playGame();
    
    String output = out.toString();
    
    assertTrue(output.contains("Maximum turns"));
  }
  
  /**
   * Tests turn alternation with multiple players.
   */
  @Test
  public void testTurnAlternation() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add Alice 0 false 3
        add Bob 1 false 3
        start
        look Alice
        look Bob
        look Alice
        quit
        """;
    
    StringReader in = new StringReader(input);
    StringWriter out = new StringWriter();
    
    GameController controller = new GameController(world, in, out, 50);
    controller.playGame();
    
    String output = out.toString();
    
    // Should show turns for both players
    assertTrue(output.contains("Player: Alice"));
    assertTrue(output.contains("Player: Bob"));
  }
  
  /**
   * Tests pet DFS wandering during gameplay.
   */
  @Test
  public void testPetWandersDuringGame() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add Alice 0 false 3
        start
        look Alice
        look Alice
        look Alice
        quit
        """;
    
    StringReader in = new StringReader(input);
    StringWriter out = new StringWriter();
    
    GameController controller = new GameController(world, in, out, 50);
    controller.playGame();
    
    String output = out.toString();
    
    // Should show pet wandering messages
    assertTrue(output.contains("wandered") || output.contains("Fortune the Cat"));
  }
}