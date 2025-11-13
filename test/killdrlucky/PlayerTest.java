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
    player = new Player("Alice", 0, 10);
  }

  @Test
  void testConstructor() {
    assertEquals("Alice", player.getName());
    assertEquals(0, player.getCurrentSpaceIndex());
    assertTrue(player.getItems().isEmpty());
    assertFalse(player.isComputerControlled());
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
