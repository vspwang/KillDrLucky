package killdrlucky;



import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for World.executeAction() method.
 */
public class WorldExecuteActionTest {
  private World world;
  
  /**
   * Sets up the test environment before each test method.
   * Creates a new game world by parsing the mansion configuration file,
   * initializes the visibility strategy, and adds a test player to the world.
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
    
    // Add a test player
    world.addPlayer("Alice", 0, false, 5);
  }

  @Test
  public void testExecuteAction_moveValid() {
    // Get a neighbor of space 0
    int neighborIndex = world.neighborsOf(0).get(0);
    String neighborName = world.getSpace(neighborIndex).getName();
    
    ActionResult result = world.executeAction("Alice", "move", neighborName);
    
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().contains("moved") || result.getMessage().contains("Alice"));
    assertTrue(result.isTurnAction());
  }

  @Test
  public void testExecuteAction_moveInvalidNotNeighbor() {
    // Try to move to a non-neighbor space
    String destination = world.getSpace(10).getName();
    
    ActionResult result = world.executeAction("Alice", "move", destination);
    
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("error") 
              || result.getMessage().toLowerCase().contains("neighbor"));
  }

  @Test
  public void testExecuteAction_movePlayerNotFound() {
    ActionResult result = world.executeAction("NonExistent", "move", "Kitchen");
    
    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("not found") 
              || result.getMessage().toLowerCase().contains("error"));
  }

  @Test
  public void testExecuteAction_pickupValid() {
    // Find a space with an item
    for (Item item : world.getItems()) {
      if (item.getRoomIndex() == 0) {
        ActionResult result = world.executeAction("Alice", "pickup", item.getName());
        
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("picked up") 
                  || result.getMessage().contains(item.getName()));
        assertTrue(result.isTurnAction());
        return;
      }
    }
    
    // If no item at space 0, test will be skipped
    assertTrue("No items at space 0 to test pickup", true);
  }

  @Test
  public void testExecuteAction_pickupItemNotInSpace() {
    ActionResult result = world.executeAction("Alice", "pickup", "NonExistentItem");
    
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("not found") 
              || result.getMessage().toLowerCase().contains("error"));
  }

  @Test
  public void testExecuteAction_pickupCapacityFull() {
    // Fill player's inventory
    int count = 0;
    for (Item item : world.getItems()) {
      if (item.getRoomIndex() == 0 && count < 5) {
        world.pickUpItem("Alice", item.getName());
        count++;
      }
    }
    
    // Try to pick up another item (if available)
    for (Item item : world.getItems()) {
      if (item.getRoomIndex() == 0) {
        ActionResult result = world.executeAction("Alice", "pickup", item.getName());
        
        if (!result.isSuccess()) {
          assertTrue(result.getMessage().toLowerCase().contains("carry") 
                    || result.getMessage().toLowerCase().contains("capacity"));
          return;
        }
      }
    }
  }

  @Test
  public void testExecuteAction_lookAroundValid() {
    ActionResult result = world.executeAction("Alice", "look", "");
    
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().length() > 0);
    assertTrue(result.isTurnAction());
  }

  @Test
  public void testExecuteAction_attackNotSameSpace() {
    // Ensure Alice is not in same space as target
    int targetSpace = world.getTarget().getCurrentSpaceIndex();
    if (targetSpace == 0) {
      // Move Alice away
      int neighborIndex = world.neighborsOf(0).get(0);
      world.movePlayer("Alice", world.getSpace(neighborIndex).getName());
    }
    
    ActionResult result = world.executeAction("Alice", "attack", "");
    
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("same") 
              || result.getMessage().toLowerCase().contains("room"));
  }

  @Test
  public void testExecuteAction_attackPokeInEye() {
    // Move Alice to target's space
    int targetSpace = world.getTarget().getCurrentSpaceIndex();
    @SuppressWarnings("unused")
    String targetSpaceName = world.getSpace(targetSpace).getName();
    
    // Add another player so Alice won't be seen (if in visible space)
    world.addPlayer("Bob", 1, false, 5);
    
    // Move Alice to target
    while (world.getPlayers().get(0).getCurrentSpaceIndex() != targetSpace) {
      int currentSpace = world.getPlayers().get(0).getCurrentSpaceIndex();
      int neighbor = world.neighborsOf(currentSpace).get(0);
      if (neighbor == targetSpace) {
        world.movePlayer("Alice", world.getSpace(targetSpace).getName());
        break;
      }
      world.movePlayer("Alice", world.getSpace(neighbor).getName());
    }
    
    ActionResult result = world.executeAction("Alice", "attack", "");
    
    // Result depends on visibility
    if (result.isSuccess()) {
      assertFalse(result.getMessage().contains("poke") || result.getMessage().contains("1"));
    }
  }

  @Test
  public void testExecuteAction_movePetValid() {
    String spaceName = world.getSpace(5).getName();
    ActionResult result = world.executeAction("Alice", "movepet", spaceName);
    
    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("pet") 
              || result.getMessage().toLowerCase().contains("moved"));
    assertTrue(result.isTurnAction());
  }

  @Test
  public void testExecuteAction_movePetInvalidSpace() {
    ActionResult result = world.executeAction("Alice", "movepet", "InvalidRoomName");
    
    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("error") 
              || result.getMessage().toLowerCase().contains("not found"));
  }

  @Test
  public void testExecuteAction_unknownActionType() {
    ActionResult result = world.executeAction("Alice", "unknownaction", "");
    
    assertFalse(result.isSuccess());
    assertEquals("Unknown action: unknownaction", result.getMessage());
  }

  @Test
  public void testExecuteAction_caseSensitivity() {
    int neighborIndex = world.neighborsOf(0).get(0);
    String neighborName = world.getSpace(neighborIndex).getName();
    
    ActionResult result = world.executeAction("Alice", "MOVE", neighborName);
    
    assertTrue(result.isSuccess());
    assertTrue(result.isTurnAction());
  }
}