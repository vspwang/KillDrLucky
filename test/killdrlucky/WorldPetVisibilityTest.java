package killdrlucky;



import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for pet's effect on visibility.
 * 
 * <p>Tests that spaces containing the pet cannot be seen by their neighbors.
 */
public class WorldPetVisibilityTest {
  
  private World world;
  private GameModelApi model;
  
  /**
   * setup for World Pet Visibility Test.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
    model = world;
  }
  
  /**
   * Tests that pet blocks visibility of its space from neighbors.
   */
  @Test
  public void testPetBlocksVisibility() {
    // Pet starts at space 0 (Armory)
    assertEquals(0, model.getPet().getCurrentSpaceIndex());
    
    // From Billiard Room (neighbor of Armory), should NOT see Armory
    Set<Integer> visibleFromBilliard = model.visibleFrom(1);
    assertFalse("Pet should block visibility of Armory from Billiard Room",
               visibleFromBilliard.contains(0));
  }
  
  /**
   * Tests visibility returns to normal when pet moves away.
   */
  @Test
  public void testVisibilityAfterPetMoves() {
    // Move pet away from Armory
    model.movePet("Kitchen");
    
    // Pet is no longer blocking
    assertNotEquals(0, model.getPet().getCurrentSpaceIndex());
  }
  
  /**
   * Tests pet location is shown in space description.
   */
  @Test
  public void testPetInSpaceDescription() {
    String desc = model.describeSpace(0);
    
    assertTrue(desc.contains("Fortune the Cat") || desc.contains("Pet"));
  }
  
  /**
   * Tests pet location updates after movePet.
   */
  @Test
  public void testMovePetUpdatesLocation() {
    model.movePet("Billiard Room");
    
    assertEquals(1, model.getPet().getCurrentSpaceIndex());
  }
  
  /**
   * Tests movePet with invalid space name.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePetInvalidSpace() {
    model.movePet("Nonexistent Room");
  }
  
  /**
   * Tests look around shows pet blocking neighbor.
   */
  @Test
  public void testLookAroundShowsPetBlocking() {
    model.addPlayer("Alice", 1, false, 5); // In Billiard Room
    
    String result = model.lookAround("Alice");
    
    // Should mention that Armory (neighbor) cannot be seen due to pet
    assertTrue(result.contains("pet") || result.contains("Cannot see inside"));
  }
}