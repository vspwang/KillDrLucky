package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class RectTest {

  @Test
  void testGetters() {
    Point ul = new Point(0, 0);
    Point lr = new Point(2, 3);
    Rect r = new Rect(ul, lr);

    assertEquals(ul, r.getUpperLeft());
    assertEquals(lr, r.getLowerRight());
  }

  @Test
  void testWidthAndHeight() {
    // Inclusive semantics: (0,0) to (2,3) → width=4, height=3
    Rect r = new Rect(new Point(0, 0), new Point(2, 3));
    assertEquals(4, r.width());
    assertEquals(3, r.height());
  }

  @Test
  void testContains() {
    Rect r = new Rect(new Point(0, 0), new Point(2, 3));

    assertTrue(r.contains(new Point(1, 1)));  // inside
    assertTrue(r.contains(new Point(0, 0)));  // upper-left edge (inclusive)
    assertTrue(r.contains(new Point(2, 3)));  // lower-right edge (inclusive)

    assertFalse(r.contains(new Point(3, 3))); // outside (row too large)
    assertFalse(r.contains(new Point(2, 4))); // outside (col too large)
  }

  @Test
  void testContainsNullThrows() {
    Rect r = new Rect(new Point(0, 0), new Point(2, 3));
    assertThrows(IllegalArgumentException.class, () -> r.contains(null));
  }

  @Test
  void testIntersects_trueWhenOverlap() {
    Rect r1 = new Rect(new Point(0, 0), new Point(2, 3));
    Rect r2 = new Rect(new Point(1, 2), new Point(3, 4)); // genuine area overlap
    assertTrue(r1.intersects(r2));
    assertTrue(r2.intersects(r1));
  }

  @Test
  void testIntersects_falseWhenOnlyTouchingEdge() {
    Rect r1 = new Rect(new Point(0, 0), new Point(2, 3));
    Rect r2 = new Rect(new Point(3, 0), new Point(5, 3)); // touch at bottom edge
    assertFalse(r1.intersects(r2));
    assertFalse(r2.intersects(r1));
  }

  @Test
  void testIntersects_falseWhenSeparate() {
    Rect r1 = new Rect(new Point(0, 0), new Point(2, 3));
    Rect r2 = new Rect(new Point(4, 0), new Point(6, 3));
    assertFalse(r1.intersects(r2));
    assertFalse(r2.intersects(r1));
  }

  @Test
  void testIntersects_trueWhenOneContainsTheOther() {
    Rect big = new Rect(new Point(0, 0), new Point(5, 5));
    Rect small = new Rect(new Point(2, 2), new Point(3, 3));
    assertTrue(big.intersects(small));
    assertTrue(small.intersects(big));
  }

  @Test
  void testEquals() {
    Rect r1 = new Rect(new Point(0, 0), new Point(2, 3));
    Rect r2 = new Rect(new Point(0, 0), new Point(2, 3));
    Rect r3 = new Rect(new Point(1, 1), new Point(3, 4));

    assertEquals(r1, r2);
    assertEquals(r1.hashCode(), r2.hashCode());
    assertNotEquals(r1, r3);
    assertNotEquals(r1.hashCode(), r3.hashCode());
  }

  @Test
  void testToString() {
    Rect r = new Rect(new Point(0, 0), new Point(2, 3));
    assertEquals("[(0, 0) to (2, 3)]", r.toString());
  }

  @Test
  void testConstructorNullsThrow() {
    assertThrows(IllegalArgumentException.class, () -> new Rect(null, new Point(2, 2)));
    assertThrows(IllegalArgumentException.class, () -> new Rect(new Point(0, 0), null));
  }

  @Test
  void testConstructorInvalidOrderThrows() {
    assertThrows(IllegalArgumentException.class, () ->
        new Rect(new Point(5, 5), new Point(3, 3))); // ul is not above/left of lr
  }
}
