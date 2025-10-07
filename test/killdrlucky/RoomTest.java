package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoomTest {

  @Test
  void testGetters() {
    Rect area = new Rect(new Point(0, 0), new Point(2, 3));
    List<Item> items = Arrays.asList(new Weapon("Knife", 3, 0), new Weapon("Wrench", 2, 0));
    Room room = new Room(0, "Kitchen", area, items);

    assertEquals(0, room.getIndex());
    assertEquals("Kitchen", room.getName());
    assertEquals(area, room.getArea());
    assertEquals(items, room.getItems());
  }

  @Test
  void testItemsAreDefensiveCopy_unmodifiableReturn() {
    Rect area = new Rect(new Point(0, 0), new Point(1, 1));
    List<Item> src = new ArrayList<>();
    src.add(new Weapon("Knife", 3, 0));
    Room room = new Room(1, "Hallway", area, src);

    // returned list should be unmodifiable
    List<Item> got = room.getItems();
    assertEquals(1, got.size());
    assertThrows(UnsupportedOperationException.class, () -> got.add(new Weapon("Wrench", 2, 0)));
  }

  @Test
  void testItemsAreDefensiveCopy_sourceMutationDoesNotAffectRoom() {
    Rect area = new Rect(new Point(0, 0), new Point(1, 1));
    List<Item> src = new ArrayList<>();
    src.add(new Weapon("Knife", 3, 0));
    Room room = new Room(2, "Study", area, src);

    // mutate original source list after construction
    src.add(new Weapon("Pipe", 2, 0));

    // room's items should remain unchanged
    assertEquals(1, room.getItems().size());
    assertEquals("Knife", room.getItems().get(0).getName());
  }

  @Test
  void testConstructorRejectsNegativeIndex() {
    Rect area = new Rect(new Point(0, 0), new Point(1, 1));
    assertThrows(IllegalArgumentException.class, () -> new Room(-1, "X", area, List.of()));
  }

  @Test
  void testConstructorRejectsNullOrBlankName() {
    Rect area = new Rect(new Point(0, 0), new Point(1, 1));
    assertThrows(IllegalArgumentException.class, () -> new Room(0, null, area, List.of()));
    assertThrows(IllegalArgumentException.class, () -> new Room(0, "   ", area, List.of()));
  }

  @Test
  void testConstructorRejectsNullArea() {
    assertThrows(IllegalArgumentException.class, () -> new Room(0, "Kitchen", null, List.of()));
  }

  @Test
  void testConstructorRejectsNullItemList() {
    Rect area = new Rect(new Point(0, 0), new Point(1, 1));
    assertThrows(IllegalArgumentException.class, () -> new Room(0, "Kitchen", area, null));
  }
}
