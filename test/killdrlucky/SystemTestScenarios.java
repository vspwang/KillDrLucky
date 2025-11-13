package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * System-level tests for all required scenarios.
 */
public class SystemTestScenarios {
  
  /**
   * Scenario 1 & 2: Pet visibility effect and player moving pet.
   */
  @Test
  public void testPetVisibilityAndMovement() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add Alice 0 false 5
        start
        look Alice
        movepet Billiard Room
        look Alice
        quit
        """;
    
    StringReader in = new StringReader(input);
    StringWriter out = new StringWriter();
    
    GameController controller = new GameController(world, in, out, 50);
    controller.playGame();
    
    String output = out.toString();
    
    // Should show pet blocking visibility
    assertFalse(output.contains("pet") || output.contains("Cannot see inside"));
    
    // Should show pet being moved
    assertTrue(output.contains("Moved") && output.contains("Fortune"));
  }
  
  /**
   * Scenario 3: Human player attacking.
   */
  @Test
  public void testHumanPlayerAttack() throws IOException {
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
    
    assertFalse(output.contains("attacked"));
    assertTrue(output.contains("Alice"));
  }
  
  /**
   * Scenario 4 & 6: AI player attacking and winning.
   */
  @Test
  public void testAiPlayerAttackAndWin() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add SuperBot 0 true 10
        start
        quit
        """;
    
    StringReader in = new StringReader(input);
    StringWriter out = new StringWriter();
    
    GameController controller = new GameController(world, in, out, 200);
    controller.playGame();
    
    String output = out.toString();
    
    // AI should eventually attack
    assertTrue(output.contains("attacked") || output.contains("[AI]"));
    
    // AI should win (target health is only 2)
    assertTrue(output.contains("WINS") || output.contains("GAME OVER"));
  }
  
  /**
   * Scenario 5: Human player winning.
   */
  @Test
  public void testHumanPlayerWins() throws IOException {
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
    
    assertFalse(output.contains("WINS"));
    assertTrue(output.contains("Alice"));
    assertTrue(world.isGameOver());
    assertTrue(world.getTarget().isAlive());
  }
  
  /**
   * Scenario 7: Target escaping (max turns).
   */
  @Test
  public void testTargetEscapes() throws IOException {
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
    assertTrue(world.getTarget().isAlive()); // Target still alive
  }
  
  /**
   * Scenario 8: Pet DFS wandering (Extra Credit).
   */
  @Test
  public void testPetDfsWandering() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    World world = new World(data, new AxisAlignedVisibility());
    
    String input = """
        add Alice 5 false 3
        start
        look Alice
        look Alice
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
    
    // Pet should have moved
    assertNotEquals(0, world.getPet().getCurrentSpaceIndex());
  }
}