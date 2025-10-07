package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**.
 * Comprehensive unit tests for World:
 * - getters and basic queries
 * - neighbors calculation
 * - visibility delegation
 * - describeSpace content (name, items, visible rooms)
 * - target movement and wrap-around
 * - attack flow (SUCCESS / NOT_SAME_SPACE / NO_SUCH_ITEM / SEEN_BY_OTHERS / TARGET_ALREADY_DEAD)
 * - rendering image dimensions
 */
class WorldTest {

  // Small 3-room world layout:
  // Room 0: A  rows 0..1, cols 0..1
  // Room 1: B  rows 0..1, cols 2..3   (shares vertical edge with A)
  // Room 2: C  rows 2..3, cols 0..1   (shares horizontal edge with A)
  //
  // Items: Knife(3) and Wrench(2) in room 0; room 1/2 have no items.
  // Target: starts in room 0 with health 10.
  private World world;
  private List<Room> rooms;
  private List<Item> items;
  private Target target;

  @BeforeEach
  void setup() {
    rooms = List.of(
        new Room(0, "A", new Rect(new Point(0, 0), new Point(1, 1)), List.of()),
        new Room(1, "B", new Rect(new Point(0, 2), new Point(1, 3)), List.of()),
        new Room(2, "C", new Rect(new Point(2, 0), new Point(3, 1)), List.of())
    );

    // Important: keep the exact instance(s) to reference from player inventory
    items = List.of(
        new Weapon("Knife", 3, 0),
        new Weapon("Wrench", 2, 0)
    );

    target = new Target("DrLucky", 10, 0);

    WorldParser.WorldData data =
        new WorldParser.WorldData("TinyWorld", 6, 6, rooms, items, target);

    world = new World(data, new AxisAlignedVisibility());
  }

  // ---------- Basic queries ----------

  @Test
  void getters_basicWorldInfo() {
    assertEquals("TinyWorld", world.getWorldName());
    assertEquals(6, world.getRows());
    assertEquals(6, world.getCols());
    assertEquals(3, world.getSpaces().size());
    assertEquals(target, world.getTarget());
  }

  // ---------- Neighbors ----------

  @Test
  void neighbors_sharedEdges() {
    // A is adjacent to B (right) and C (down)
    List<Integer> n0 = world.neighborsOf(0);
    assertTrue(n0.contains(1));
    assertTrue(n0.contains(2));
    assertEquals(2, n0.size());

    // B is adjacent only to A
    List<Integer> n1 = world.neighborsOf(1);
    assertEquals(1, n1.size());
    assertTrue(n1.contains(0));

    // C is adjacent only to A
    List<Integer> n2 = world.neighborsOf(2);
    assertEquals(1, n2.size());
    assertTrue(n2.contains(0));
  }

  // ---------- Visibility ----------

  @Test
  void visibleFrom_axisAligned() {
    Set<Integer> fromA = world.visibleFrom(0);
    assertTrue(fromA.contains(1)); // B is in the same row band
    assertTrue(fromA.contains(2)); // C is in the same column band
  }

  // ---------- describeSpace (embedded tests) ----------

  @Test
  void describeSpace_includesRoomName_items_visibleRooms() {
    String desc = world.describeSpace(0);

    // Room name
    assertTrue(desc.contains("Room: A"));

    // Items (name + damage)
    assertTrue(desc.contains("Items:"));
    assertTrue(desc.contains("Knife(3)"));
    assertTrue(desc.contains("Wrench(2)"));

    // Visible rooms (B and C)
    assertTrue(desc.contains("Visible from here"));
    assertTrue(desc.contains("B"));
    assertTrue(desc.contains("C"));
  }

  @Test
  void describeSpace_roomWithNoItems_printsNone() {
    // Room 1 and Room 2 have no items
    String desc1 = world.describeSpace(1);
    String desc2 = world.describeSpace(2);

    assertTrue(desc1.contains("Room: B"));
    assertTrue(desc1.contains("Items: none"));

    assertTrue(desc2.contains("Room: C"));
    assertTrue(desc2.contains("Items: none"));
  }

  @Test
  void describeSpace_invalidIndex_throws() {
    assertThrows(IllegalArgumentException.class, () -> world.describeSpace(-1));
    assertThrows(IllegalArgumentException.class, () -> world.describeSpace(3));
  }

  // ---------- Target movement ----------

  @Test
  void moveTargetNext_wrapsAround() {
    assertEquals(0, world.getTarget().getCurrentSpaceIndex());
    world.moveTargetNext();
    assertEquals(1, world.getTarget().getCurrentSpaceIndex());
    world.moveTargetNext();
    assertEquals(2, world.getTarget().getCurrentSpaceIndex());
    world.moveTargetNext();
    assertEquals(0, world.getTarget().getCurrentSpaceIndex()); // wrap
  }

  // ---------- Attacking logic ----------

  @Test
  void attack_success_sameRoom_hasItem_unseen() {
    // Player 0 in same room as target (room 0), with items.get(0)
    Player p0 = new Player("P0", 0, List.of(items.get(0)));
    world.addPlayer(p0);

    assertEquals(AttackStatus.SUCCESS, world.canAttack(0, 0));
    assertEquals(AttackStatus.SUCCESS, world.attack(0, 0));

    assertEquals(7, world.getTarget().getHealth()); // 10 - 3
  }

  @Test
  void attack_notSameSpace() {
    Player p0 = new Player("P0", 1, List.of(items.get(0)));
    world.addPlayer(p0);
    assertEquals(AttackStatus.NOT_SAME_SPACE, world.canAttack(0, 0));
  }

  @Test
  void attack_noSuchItem_notInInventory() {
    Player p0 = new Player("P0", 0, List.of());
    world.addPlayer(p0);
    assertEquals(AttackStatus.NO_SUCH_ITEM, world.canAttack(0, 0));
  }

  @Test
  void attack_seenByOthers_otherPlayerInVisibleRoom() {
    // Attacker in room 0 with the weapon
    Player attacker = new Player("P0", 0, List.of(items.get(0)));
    // Witness in a visible room (room 1 or 2 are visible from 0)
    Player witness = new Player("P1", 1, List.of());

    world.addPlayer(attacker);
    world.addPlayer(witness);

    assertEquals(AttackStatus.SEEN_BY_OTHERS, world.canAttack(0, 0));
  }

  @Test
  void attack_targetAlreadyDead() {
    target.setHealth(0); // dead
    Player p0 = new Player("P0", 0, List.of(items.get(0)));
    world.addPlayer(p0);

    assertEquals(AttackStatus.TARGET_ALREADY_DEAD, world.canAttack(0, 0));
    assertEquals(0, world.getTarget().getHealth());
  }

  @Test
  void attack_invalidPlayerIndexThrows() {
    assertThrows(IllegalArgumentException.class, () -> world.canAttack(5, 0));
    assertThrows(IllegalArgumentException.class, () -> world.attack(5, 0));
  }

  @Test
  void attack_invalidItemIndex_returnsNoSuchItem() {
    Player p0 = new Player("P0", 0, List.of());
    world.addPlayer(p0);
    assertEquals(AttackStatus.NO_SUCH_ITEM, world.canAttack(0, 99));
  }

  // ---------- Rendering ----------

  @Test
  void renderBufferedImage_dimensions() {
    int cell = 10;
    BufferedImage img = world.renderBufferedImage(cell);
    assertEquals(world.getCols() * cell, img.getWidth());
    assertEquals(world.getRows() * cell, img.getHeight());
  }
}
