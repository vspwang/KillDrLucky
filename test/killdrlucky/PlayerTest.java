package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlayerTest {

  @Test
  void testGettersAndStartIndex() {
    List<Item> items = List.of(new Weapon("Knife", 3, 0));
    Player p = new Player("Alice", 2, items);

    assertEquals("Alice", p.getName());
    assertEquals(2, p.getCurrentSpaceIndex());
    assertEquals(1, p.getItems().size());
    assertEquals("Knife", p.getItems().get(0).getName());
  }

  @Test
  void testSetCurrentSpaceIndex() {
    Player p = new Player("Bob", 0, List.of());
    p.setCurrentSpaceIndex(3);
    assertEquals(3, p.getCurrentSpaceIndex());
  }

  @Test
  void testSetCurrentSpaceIndexRejectsNegative() {
    Player p = new Player("Bob", 0, List.of());
    assertThrows(IllegalArgumentException.class, () -> p.setCurrentSpaceIndex(-1));
  }

  @Test
  void testItemsDefensiveCopyUnmodifiableReturn() {
    Player p = new Player("Eve", 1, List.of(new Weapon("Wrench", 2, 1)));
    List<Item> returned = p.getItems();
    assertEquals(1, returned.size());
    assertThrows(UnsupportedOperationException.class,
        () -> returned.add(new Weapon("Pipe", 2, 1)));
  }

  @Test
  void testConstructorCopiesSourceItems() {
    List<Item> src = new ArrayList<>();
    src.add(new Weapon("Knife", 3, 0));
    Player p = new Player("Carl", 0, src);

    // mutate original after construction
    src.add(new Weapon("Wrench", 2, 0));

    // player inventory should not change
    assertEquals(1, p.getItems().size());
    assertEquals("Knife", p.getItems().get(0).getName());
  }

  @Test
  void testAddAndRemoveItem() {
    Player p = new Player("Dora", 0, List.of());
    Weapon knife = new Weapon("Knife", 3, 0);
    Weapon wrench = new Weapon("Wrench", 2, 0);

    p.addItem(knife);
    p.addItem(wrench);
    assertEquals(2, p.getItems().size());
    assertTrue(p.getItems().contains(knife));
    assertTrue(p.getItems().contains(wrench));

    p.removeItem(knife);
    assertEquals(1, p.getItems().size());
    assertFalse(p.getItems().contains(knife));
    assertTrue(p.getItems().contains(wrench));
  }

  @Test
  void testAddRemoveNullThrows() {
    Player p = new Player("Neo", 0, List.of());
    assertThrows(IllegalArgumentException.class, () -> p.addItem(null));
    assertThrows(IllegalArgumentException.class, () -> p.removeItem(null));
  }

  @Test
  void testConstructorValidation() {
    assertThrows(IllegalArgumentException.class, () -> new Player(null, 0, List.of()));
    assertThrows(IllegalArgumentException.class, () -> new Player("   ", 0, List.of()));
    assertThrows(IllegalArgumentException.class, () -> new Player("Alice", -1, List.of()));
    assertThrows(IllegalArgumentException.class, () -> new Player("Alice", 0, null));
  }

  @Test
  void testEqualsAndHashCode() {
    Player p1 = new Player("A", 1, List.of(new Weapon("Knife", 3, 1)));
    Player p2 = new Player("A", 1, List.of(new Weapon("Knife", 3, 1)));
    Player p3 = new Player("B", 2, List.of());

    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
    assertNotEquals(p1, p3);
    assertNotEquals(p1.hashCode(), p3.hashCode());
  }

  @Test
  void testToStringContainsKeyFields() {
    Player p = new Player("Zoe", 4, List.of(new Weapon("Knife", 3, 4)));
    String s = p.toString();
    assertTrue(s.contains("Zoe"));
    assertTrue(s.contains("4"));
    assertTrue(s.contains("Knife"));
  }
}
