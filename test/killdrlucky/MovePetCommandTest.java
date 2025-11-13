package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the MovePetCommand class.
 */
public class MovePetCommandTest {
  
  private World world;
  private GameModelApi model;
  
  /**
   * setup for movepetcommandtest.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
    model = world;
  }
  
  /**
   * Tests command creation with valid space name.
   */
  @Test
  public void testCommandCreation() {
    Command cmd = new MovePetCommand(model, "Billiard Room");
    assertNotNull(cmd);
    assertTrue(cmd.isTurnAction());
  }
  
  /**
   * Tests that null model throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullModel() {
    new MovePetCommand(null, "Kitchen");
  }
  
  /**
   * Tests that null space name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullSpaceName() {
    new MovePetCommand(model, null);
  }
  
  /**
   * Tests that empty space name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEmptySpaceName() {
    new MovePetCommand(model, "");
  }
  
  /**
   * Tests moving pet to valid space.
   */
  @Test
  public void testExecuteValidSpace() {
    int initialLocation = model.getPet().getCurrentSpaceIndex();
    Command cmd = new MovePetCommand(model, "Billiard Room");
    String result = cmd.execute();
    
    assertTrue(result.contains("Fortune the Cat"));
    assertTrue(result.contains("Billiard Room"));
    assertNotEquals(initialLocation, model.getPet().getCurrentSpaceIndex());
  }
  
  /**
   * Tests moving pet to invalid space returns error.
   */
  @Test
  public void testExecuteInvalidSpace() {
    Command cmd = new MovePetCommand(model, "Nonexistent Room");
    String result = cmd.execute();
    
    assertTrue(result.contains("Error") || result.contains("not found"));
  }
  
  /**
   * Tests that movepet is a turn action.
   */
  @Test
  public void testIsTurnAction() {
    Command cmd = new MovePetCommand(model, "Kitchen");
    assertTrue(cmd.isTurnAction());
  }
}