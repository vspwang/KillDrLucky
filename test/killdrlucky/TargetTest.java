package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class TargetTest {

  @Test
  void testGettersAndStart() {
    Target t = new Target("DrLucky", 10, 0);
    assertEquals("DrLucky", t.getName());
    assertEquals(10, t.getHealth());
    assertEquals(0, t.getCurrentSpaceIndex());
    assertTrue(t.isAlive());
  }

  @Test
  void testSetCurrentSpaceIndex() {
    Target t = new Target("DL", 5, 1);
    t.setCurrentSpaceIndex(3);
    assertEquals(3, t.getCurrentSpaceIndex());
  }

  @Test
  void testSetCurrentSpaceIndexRejectsNegative() {
    Target t = new Target("DL", 5, 1);
    assertThrows(IllegalArgumentException.class, () -> t.setCurrentSpaceIndex(-2));
  }

  @Test
  void testSetHealthAndIsAlive() {
    Target t = new Target("DL", 5, 1);
    t.setHealth(2);
    assertEquals(2, t.getHealth());
    assertTrue(t.isAlive());

    t.setHealth(0);
    assertEquals(0, t.getHealth());
    assertFalse(t.isAlive());
  }

  @Test
  void testSetHealthRejectsNegative() {
    Target t = new Target("DL", 5, 1);
    assertThrows(IllegalArgumentException.class, () -> t.setHealth(-1));
  }

  @Test
  void testTakeDamageNormalAndClampToZero() {
    Target t = new Target("DL", 5, 1);
    t.takeDamage(3);
    assertEquals(2, t.getHealth());
    assertTrue(t.isAlive());

    t.takeDamage(10); // overkill clamps to zero
    assertEquals(0, t.getHealth());
    assertFalse(t.isAlive());
  }

  @Test
  void testTakeDamageRejectsNegative() {
    Target t = new Target("DL", 5, 1);
    assertThrows(IllegalArgumentException.class, () -> t.takeDamage(-3));
  }

  @Test
  void testConstructorValidation() {
    assertThrows(IllegalArgumentException.class, () -> new Target(null, 5, 0));
    assertThrows(IllegalArgumentException.class, () -> new Target("   ", 5, 0));
    assertThrows(IllegalArgumentException.class, () -> new Target("DL", -1, 0));
    assertThrows(IllegalArgumentException.class, () -> new Target("DL", 5, -1));
  }

  @Test
  void testEqualsAndHashCode() {
    Target t1 = new Target("DL", 10, 2);
    Target t2 = new Target("DL", 10, 2);
    Target t3 = new Target("DLX", 5, 1);

    assertEquals(t1, t2);
    assertEquals(t1.hashCode(), t2.hashCode());
    assertNotEquals(t1, t3);
    assertNotEquals(t1.hashCode(), t3.hashCode());
  }

  @Test
  void testToStringContainsKeyFields() {
    Target t = new Target("DL", 8, 3);
    String s = t.toString();
    assertTrue(s.contains("DL"));
    assertTrue(s.contains("8"));
    assertTrue(s.contains("3"));
  }
}
