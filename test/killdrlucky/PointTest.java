package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.Test;

class PointTest {

  @Test
  void testGetters() {
    Point p = new Point(1, 2);
    assertEquals(1, p.getRow());
    assertEquals(2, p.getCol());
  }
  
  @Test
  void testEquals() {
    Point p1 = new Point(1, 2);
    Point p2 = new Point(2, 3);
    Point p3 = new Point(1, 2);
    assertEquals(p1, p3);
    assertEquals(p1.hashCode(), p3.hashCode());
    assertNotEquals(p1, p2);
    assertNotEquals(p1.hashCode(), p2.hashCode());
  }
  
  @Test
  void testToString() {
    Point p = new Point(1, 2);
    assertEquals("(1, 2)", p.toString());
  }
  
  @Test
  void testNoNegativeRow() {
    assertThrows(IllegalArgumentException.class, () -> new Point(-1, 2));
  }
  
  @Test
  void testNoNegativeCol() {
    assertThrows(IllegalArgumentException.class, () -> new Point(1, -2));
  }
  

}
