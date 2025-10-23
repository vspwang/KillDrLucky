package killdrlucky;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AxisAlignedVisibilityTest {

  private Space room(int idx, String name, int ulr, int ulc, int lrr, int lrc) {
    return new Room(idx, name, new Rect(new Point(ulr, ulc), new Point(lrr, lrc)), List.of());
  }

  @Test
  void testHorizontalVisibility_unblocked() {
    // Two rooms in the same row band, nothing in between.
    Space a = room(0, "a", 0, 0, 1, 2); // columns 0..2
    Space b = room(1, "b", 0, 4, 1, 6); // columns 4..6
    List<Space> spaces = List.of(a, b);

    VisibilityStrategy vis = new AxisAlignedVisibility();
    Set<Integer> fromA = vis.visibleFrom(0, spaces);
    Set<Integer> fromB = vis.visibleFrom(1, spaces);

    assertTrue(fromA.contains(1));
    assertTrue(fromB.contains(0));
  }

  @Test
  void testHorizontalVisibility_blockedByMiddle() {
    // a --- c --- b in the same row band; c blocks line of sight.
    Space a = room(0, "a", 0, 0, 1, 2); // 0..2
    Space c = room(1, "c", 0, 3, 1, 4); // 3..4 (between a and b)
    Space b = room(2, "b", 0, 5, 1, 7); // 5..7
    List<Space> spaces = List.of(a, c, b);

    VisibilityStrategy vis = new AxisAlignedVisibility();
    Set<Integer> fromA = vis.visibleFrom(0, spaces);
    Set<Integer> fromB = vis.visibleFrom(2, spaces);

    assertFalse(fromA.contains(2)); // a cannot see b because c blocks
    assertFalse(fromB.contains(0)); // b cannot see a because c blocks
    // But a and c can see each other:
    assertTrue(vis.visibleFrom(0, spaces).contains(1));
    assertTrue(vis.visibleFrom(1, spaces).contains(0));
  }

  @Test
  void testVerticalVisibility_unblocked() {
    // Two rooms in the same column band, nothing in between.
    Space a = room(0, "a", 0, 0, 1, 1); // rows 0..1, cols 0..1
    Space b = room(1, "b", 3, 0, 4, 1); // rows 3..4, same col band 0..1
    List<Space> spaces = List.of(a, b);

    VisibilityStrategy vis = new AxisAlignedVisibility();
    assertTrue(vis.visibleFrom(0, spaces).contains(1));
    assertTrue(vis.visibleFrom(1, spaces).contains(0));
  }

  @Test
  void testVerticalVisibility_blockedByMiddle() {
    // a above, c in the middle, b below; c blocks.
    Space a = room(0, "a", 0, 0, 1, 1);
    Space c = room(1, "c", 2, 0, 3, 1); // middle blocker
    Space b = room(2, "b", 4, 0, 5, 1);
    List<Space> spaces = List.of(a, c, b);

    VisibilityStrategy vis = new AxisAlignedVisibility();
    assertFalse(vis.visibleFrom(0, spaces).contains(2));
    assertFalse(vis.visibleFrom(2, spaces).contains(0));
    assertTrue(vis.visibleFrom(0, spaces).contains(1));
    assertTrue(vis.visibleFrom(2, spaces).contains(1));
  }

  @Test
  void testDiagonal_notVisible() {
    // Diagonal placement → not visible by axis-aligned rules.
    Space a = room(0, "a", 0, 0, 1, 1);
    Space b = room(1, "b", 2, 2, 3, 3);
    List<Space> spaces = List.of(a, b);

    VisibilityStrategy vis = new AxisAlignedVisibility();
    assertFalse(vis.visibleFrom(0, spaces).contains(1));
    assertFalse(vis.visibleFrom(1, spaces).contains(0));
  }

  @Test
  void testInvalidInputsThrow() {
    VisibilityStrategy vis = new AxisAlignedVisibility();
    assertThrows(IllegalArgumentException.class, () -> vis.visibleFrom(-1, List.of()));
    assertThrows(IllegalArgumentException.class, () -> vis.visibleFrom(0, null));

    Space a = room(0, "a", 0, 0, 1, 1);
    List<Space> spaces = List.of(a);
    assertThrows(IllegalArgumentException.class, () -> vis.visibleFrom(1, spaces));
  }
}
