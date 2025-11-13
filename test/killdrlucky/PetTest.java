package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Pet class.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Constructor validation</li>
 *   <li>Position tracking</li>
 *   <li>Character interface implementation</li>
 * </ul>
 */
public class PetTest {
  
  private Pet pet;
  
  /**
   * Sets up test fixtures before each test.
   */
  @Before
  public void setUp() {
    pet = new Pet("Fortune the Cat", 0);
  }
  
  /**
   * Tests successful pet creation with valid parameters.
   */
  @Test
  public void testPetCreation() {
    assertEquals("Fortune the Cat", pet.getName());
    assertEquals(0, pet.getCurrentSpaceIndex());
  }
  
  /**
   * Tests that null name throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPetCreationWithNullName() {
    new Pet(null, 0);
  }
  
  /**
   * Tests that blank name throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPetCreationWithBlankName() {
    new Pet("", 0);
  }
  
  /**
   * Tests that negative start index throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPetCreationWithNegativeIndex() {
    new Pet("Cat", -1);
  }
  
  /**
   * Tests setting valid space index.
   */
  @Test
  public void testSetCurrentSpaceIndex() {
    pet.setCurrentSpaceIndex(5);
    assertEquals(5, pet.getCurrentSpaceIndex());
  }
  
  /**
   * Tests that setting negative space index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSetNegativeSpaceIndex() {
    pet.setCurrentSpaceIndex(-1);
  }
  
  /**
   * Tests pet equality based on name and position.
   */
  @Test
  public void testPetEquality() {
    Pet pet1 = new Pet("Fortune", 0);
    Pet pet2 = new Pet("Fortune", 0);
    Pet pet3 = new Pet("Fortune", 1);
    Pet pet4 = new Pet("Other", 0);
    
    assertEquals(pet1, pet2);
    assertNotEquals(pet1, pet3);
    assertNotEquals(pet1, pet4);
  }
  
  /**
   * Tests pet hash code consistency.
   */
  @Test
  public void testPetHashCode() {
    Pet pet1 = new Pet("Fortune", 0);
    Pet pet2 = new Pet("Fortune", 0);
    
    assertEquals(pet1.hashCode(), pet2.hashCode());
  }
  
  /**
   * Tests toString format.
   */
  @Test
  public void testToString() {
    String result = pet.toString();
    assertTrue(result.contains("Fortune the Cat"));
    assertTrue(result.contains("0"));
  }
}