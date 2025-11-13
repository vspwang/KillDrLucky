package killdrlucky;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for command factory in GameController.
 * 
 * <p>Verifies that commands are correctly created from user input.
 */
public class CommandFactoryTest {
  
  private GameController controller;
  private StringWriter output;
  private World world;
  
  /**
   * setup for command factory tests.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
    
    output = new StringWriter();
    StringReader input = new StringReader("quit\n");
    controller = new GameController(world, input, output, 10);
  }
  
  /**
   * Tests controller creation with valid parameters.
   */
  @Test
  public void testControllerCreation() {
    assertNotNull(controller);
  }
  
  /**
   * Tests null model throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullModel() {
    new GameController(null, new StringReader(""), new StringWriter(), 10);
  }
  
  /**
   * Tests negative max turns throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeMaxTurns() {
    new GameController(world, new StringReader(""), new StringWriter(), -1);
  }
  
  /**
   * Tests zero max turns throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testZeroMaxTurns() {
    new GameController(world, new StringReader(""), new StringWriter(), 0);
  }
}