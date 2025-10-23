package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for World.
 * 
 * <p>These tests verify key world behaviors including space descriptions,
 * player management, movement, visibility, attacks, and AI actions.
 */
public class WorldTest {

  private World world;

  @BeforeEach
  void setup() throws IOException {
    // Build world from sample parser data (using a small test map)
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }

  // ---------- Basic Structure ----------

  @Test
  void testWorldProperties() {
    assertNotNull(world.getWorldName());
    assertTrue(world.getRows() > 0);
    assertTrue(world.getCols() > 0);
    assertFalse(world.getSpaces().isEmpty());
    assertNotNull(world.getTarget());
  }

  @Test
  void testDescribeSpaceByIndex() {
    String desc = world.describeSpace(0);
    assertTrue(desc.contains("Room:"));
    assertTrue(desc.contains("Neighbors:"));
  }

  @Test
  void testDescribeSpaceByName() {
    String name = world.getSpaces().get(0).getName();
    String desc = world.describeSpace(name);
    assertTrue(desc.contains(name));
  }

  // ---------- Player Management ----------

  @Test
  void testAddAndListPlayers() {
    world.addPlayer("Alice", 0, false, 3);
    world.addPlayer("Bob", 1, true, 2);
    List<Iplayer> players = world.getPlayers();
    assertEquals(2, players.size());
    assertEquals("Alice", players.get(0).getName());
    assertTrue(players.get(1).isComputerControlled());
  }

  @Test
  void testAddDuplicatePlayerThrows() {
    world.addPlayer("Alice", 0, false, 3);
    assertThrows(IllegalArgumentException.class, () -> {
      world.addPlayer("Alice", 1, false, 3);
    });
  }

  @Test
  void testAddPlayerInvalidIndexThrows() {
    assertThrows(IllegalArgumentException.class, () -> {
      world.addPlayer("Invalid", 99, false, 3);
    });
  }

  // ---------- Movement ----------

  @Test
  void testMovePlayerValid() {
    world.addPlayer("Alice", 0, false, 3);
    String result = world.movePlayer("Alice", world.getSpaces().get(1).getName());
    assertTrue(result.contains("moved"));
  }

  @Test
  void testMovePlayerInvalidDirection() {
    world.addPlayer("Alice", 0, false, 3);
    String result = world.movePlayer("Alice", "NonexistentRoom");
    assertTrue(result.contains("No neighboring space"));
  }

  // ---------- Item Pickup ----------

  @Test
  void testPickUpItem() {
    world.addPlayer("Alice", 0, false, 3);
    // find an item in the same space as 0
    String itemName = world.getSpaces().get(0).getName();
    String result = world.pickUpItem("Alice", itemName);
    assertNotNull(result);
  }

  // ---------- Look / Describe ----------

  @Test
  void testLookAround() {
    world.addPlayer("Alice", 0, false, 3);
    String result = world.lookAround("Alice");
    assertTrue(result.contains("You are in"));
  }

  @Test
  void testDescribePlayer() {
    world.addPlayer("Alice", 0, false, 3);
    String desc = world.describePlayer("Alice");
    assertTrue(desc.contains("Player:"));
    assertTrue(desc.contains("Current Space:"));
  }

  // ---------- Target Movement ----------

  @Test
  void testMoveTargetCycles() {
    int initial = world.getTarget().getCurrentSpaceIndex();
    world.moveTarget();
    int newIndex = world.getTarget().getCurrentSpaceIndex();
    assertNotEquals(initial, newIndex);
  }

  // ---------- Attack Logic ----------

  @Test
  void testAttackNotSameSpace() {
    world.addPlayer("Alice", 1, false, 3);
    // ensure target is in another space (usually 0)
    AttackStatus status = world.canAttack(0, 0);
    assertEquals(AttackStatus.NOT_SAME_SPACE, status);
  }

  @Test
  void testAttackTargetAlreadyDead() {
    world.addPlayer("Alice", 0, false, 3);
    world.getTarget().takeDamage(999);
    AttackStatus status = world.canAttack(0, 0);
    assertEquals(AttackStatus.TARGET_ALREADY_DEAD, status);
  }

  // ---------- AI ----------

  @Test
  void testAutoActionForAi() {
    world.addPlayer("Bot", 0, true, 3);
    String result = world.autoAction("Bot");
    assertTrue(result.contains("AI"));
  }

  // ---------- Graphics ----------

  @Test
  void testRenderBufferedImage() {
    BufferedImage img = world.renderBufferedImage(30);
    assertNotNull(img);
    assertTrue(img.getWidth() > 0);
    assertTrue(img.getHeight() > 0);
  }

  // ---------- Game Control ----------

  @Test
  void testEndGameFlag() {
    world.endGame();
    assertTrue(world.isGameOver());
  }

  @Test
  void testGameInitiallyNotOver() {
    assertFalse(world.isGameOver());
  }
}
