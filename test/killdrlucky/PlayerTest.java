package killdrlucky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Player.
 */
public class PlayerTest {

  private World world;
  private Player player;

  @BeforeEach
  void setup() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
    player = new Player("Alice", 0, new ArrayList<>());
  }

  @Test
  void testConstructor() {
    assertEquals("Alice", player.getName());
    assertEquals(0, player.getCurrentSpaceIndex());
    assertTrue(player.getItems().isEmpty());
    assertFalse(player.isComputerControlled());
  }

  @Test
  void testMoveValidDirection() {
    // manually move to neighbor using world API
    String result = player.move(world.getSpaces().get(1).getName(), world);
    assertTrue(result.toLowerCase().contains("moved"));
  }

  @Test
  void testPickUpItemSuccess() {
    Item it = world.getItems().get(0);
    it.setRoomIndex(0);
    String result = player.pickUp(it.getName(), world);
    assertTrue(result.contains("picked up"));
    assertTrue(player.getItems().contains(it));
  }

  @Test
  void testPickUpItemNotInRoom() {
    Item it = world.getItems().get(0);
    it.setRoomIndex(5);
    String result = player.pickUp(it.getName(), world);
    assertTrue(result.toLowerCase().contains("not found"));
  }

  @Test
  void testLookAround() {
    String out = player.lookAround(world);
    assertTrue(out.contains("You are in"));
  }

  @Test
  void testAddItemAndRemoveItem() {
    Item it = new Weapon("Knife", 0, 10);
    player.addItem(it);
    assertTrue(player.getItems().contains(it));
    player.removeItem(it);
    assertFalse(player.getItems().contains(it));
  }

  @Test
  void testSetCurrentSpaceIndex() {
    player.setCurrentSpaceIndex(2);
    assertEquals(2, player.getCurrentSpaceIndex());
  }
}
