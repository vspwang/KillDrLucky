package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for command execution through controller.
 */
public class CommandExecutionTest {
  
  private World world;
  private StringWriter output;
  
  /**
   * setup for command execution tests.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
    output = new StringWriter();
  }
  
  /**
   * Tests move command execution.
   */
  @Test
  public void testMoveCommandExecution() throws IOException {
    String input = """
        add Alice 0 false 3
        start
        move Alice Billiard Room
        quit
        """;
    
    GameController controller = new GameController(
        world, new StringReader(input), output, 50);
    controller.playGame();
    
    String result = output.toString();
    
    assertTrue(result.contains("moved"));
    assertTrue(result.contains("Billiard Room"));
  }
  
  /**
   * Tests pickup command execution.
   */
  @Test
  public void testPickupCommandExecution() throws IOException {
    String input = """
        add Alice 0 false 3
        start
        pickup Alice Revolver
        quit
        """;
    
    GameController controller = new GameController(
        world, new StringReader(input), output, 50);
    controller.playGame();
    
    String result = output.toString();
    
    assertTrue(result.contains("picked up"));
    assertTrue(result.contains("Revolver"));
  }
  
  /**
   * Tests look command execution.
   */
  @Test
  public void testLookCommandExecution() throws IOException {
    String input = """
        add Alice 0 false 3
        start
        look Alice
        quit
        """;
    
    GameController controller = new GameController(
        world, new StringReader(input), output, 50);
    controller.playGame();
    
    String result = output.toString();
    
    assertTrue(result.contains("Looking around"));
    assertTrue(result.contains("Armory"));
  }
  
  /**
   * Tests attack command execution.
   */
  @Test
  public void testAttackCommandExecution() throws IOException {
    String input = """
        add Alice 0 false 3
        start
        attack Alice
        quit
        """;
    
    GameController controller = new GameController(
        world, new StringReader(input), output, 50);
    controller.playGame();
    
    String result = output.toString();
    
    assertTrue(result.contains("attacked") || result.contains("poke"));
  }
  
  /**
   * Tests movepet command execution.
   */
  @Test
  public void testMovePetCommandExecution() throws IOException {
    String input = """
        add Alice 0 false 3
        start
        movepet Kitchen
        quit
        """;
    
    GameController controller = new GameController(
        world, new StringReader(input), output, 50);
    controller.playGame();
    
    String result = output.toString();
    
    assertTrue(result.contains("Moved"));
    assertTrue(result.contains("Fortune") || result.contains("Cat"));
    assertTrue(result.contains("Kitchen"));
  }
  
  /**
   * Tests info commands don't consume turns.
   */
  @Test
  public void testInfoCommandsDontConsumeTurns() throws IOException {
    String input = """
        add Alice 0 false 3
        start
        describe Alice
        space Armory
        look Alice
        quit
        """;
    
    GameController controller = new GameController(
        world, new StringReader(input), output, 50);
    controller.playGame();
    
    String result = output.toString();
    
    // Describe and space shouldn't advance turn
    // Only look should advance to turn 2
    assertTrue(result.contains("Turn 1"));
    assertTrue(result.contains("Turn 2"));
    assertFalse(result.contains("Turn 3")); // Only 1 turn action (look)
  }
}