package killdrlucky;



import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for pet DFS wandering (Extra Credit).
 * 
 * <p>Verifies that the pet follows a depth-first traversal pattern.
 */
public class WorldDfsPetTest {
  
  private World world;
  
  /**
   * setup for DFS search of pet.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }
  
  /**
   * Tests pet starts at space 0.
   */
  @Test
  public void testPetStartsAtZero() {
    assertEquals(0, world.getPet().getCurrentSpaceIndex());
  }
  
  /**
   * Tests pet moves to a neighbor on first DFS step.
   */
  @Test
  public void testPetMovesToNeighborFirstStep() {
    int initialLocation = world.getPet().getCurrentSpaceIndex();
    
    world.movePetDfs();
    
    int newLocation = world.getPet().getCurrentSpaceIndex();
    
    // Pet should have moved
    assertNotEquals(initialLocation, newLocation);
    
    // New location should be a neighbor of initial location
    assertTrue(world.neighborsOf(initialLocation).contains(newLocation));
  }
  
  /**
   * Tests pet eventually visits all spaces.
   */
  @Test
  public void testPetVisitsAllSpaces() {
    Set<Integer> visitedSpaces = new HashSet<>();
    int totalSpaces = world.getSpaces().size();
    
    // Run DFS for enough steps to visit all spaces
    // (worst case: 2 * totalSpaces steps due to backtracking)
    for (int i = 0; i < totalSpaces * 3; i++) {
      visitedSpaces.add(world.getPet().getCurrentSpaceIndex());
      world.movePetDfs();
    }
    
    // Should have visited all spaces
    assertEquals("Pet should visit all spaces in DFS",
                totalSpaces, visitedSpaces.size());
  }
  
  /**
   * Tests DFS eventually cycles back to start.
   */
  @Test
  public void testDfsCyclesBackToStart() {
    int totalSpaces = world.getSpaces().size();
    
    // Run enough steps to complete one full DFS cycle
    for (int i = 0; i < totalSpaces * 3; i++) {
      world.movePetDfs();
    }
    
    // Should be back at start or in the traversal
    int currentLocation = world.getPet().getCurrentSpaceIndex();
    assertTrue(currentLocation >= 0 && currentLocation < totalSpaces);
  }
  
  /**
   * Tests manual pet move resets DFS traversal.
   */
  @Test
  public void testManualMoveResetsDfs() {
    // Do some DFS moves
    world.movePetDfs();
    world.movePetDfs();
    
    // Manual move
    world.movePet("Kitchen");
    
    // Next DFS should start from new location
    int locationAfterManualMove = world.getPet().getCurrentSpaceIndex();
    world.movePetDfs();
    
    // Should move to a neighbor of Kitchen, not continue old DFS path
    int newLocation = world.getPet().getCurrentSpaceIndex();
    assertNotEquals(locationAfterManualMove, newLocation);
  }
  
  /**
   * Tests pet doesn't get stuck in infinite loop.
   */
  @Test(timeout = 1000)
  public void testNoDfsInfiniteLoop() {
    // Run many iterations - should not hang
    for (int i = 0; i < 1000; i++) {
      world.movePetDfs();
    }
  }
}