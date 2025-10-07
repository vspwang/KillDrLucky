package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

class WeaponTest {

  @Test
  void testGetters() {
    Weapon w = new Weapon("Knife", 3, 2);
    assertEquals("Knife", w.getName());
    assertEquals(3, w.getDamage());
    assertEquals(2, w.getRoomIndex());
  }

  @Test
  void testEqualsAndHashCode() {
    Weapon w1 = new Weapon("Knife", 3, 2);
    Weapon w2 = new Weapon("Knife", 3, 2);
    Weapon w3 = new Weapon("Wrench", 2, 2);

    assertEquals(w1, w2);
    assertEquals(w1.hashCode(), w2.hashCode());
    assertNotEquals(w1, w3);
    assertNotEquals(w1.hashCode(), w3.hashCode());
  }

  @Test
  void testToString() {
    Weapon w = new Weapon("Knife", 3, 2);
    String s = w.toString();
    assertTrue(s.contains("Knife"));
    assertTrue(s.contains("3"));
    assertTrue(s.contains("2"));
  }

  @Test
  void testConstructorRejectsBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new Weapon("   ", 1, 0));
    assertThrows(IllegalArgumentException.class, () -> new Weapon(null, 1, 0));
  }

  @Test
  void testConstructorRejectsNegativeDamage() {
    assertThrows(IllegalArgumentException.class, () -> new Weapon("Knife", -1, 0));
  }

  @Test
  void testConstructorRejectsNegativeRoomIndex() {
    assertThrows(IllegalArgumentException.class, () -> new Weapon("Knife", 1, -2));
  }
}
