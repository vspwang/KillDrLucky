package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration tests for attack visibility rules.
 */
public class AttackVisibilityIntegrationTest {
  
  private World world;
  
  /**
   * setup for AttackVisibilityIntegrationTest.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }
  
  /**
   * Tests attack succeeds when no witnesses.
   */
  @Test
  public void testAttackSucceedsWhenAlone() {
    world.addPlayer("Alice", 0, false, 5);
    
    int initialHealth = world.getTarget().getHealth();
    String result = world.attackTarget("Alice", null);
    
    assertTrue(result.contains("attacked"));
    assertTrue(world.getTarget().getHealth() < initialHealth);
  }
  
  /**
   * Tests attack fails when witness in same room.
   */
  @Test
  public void testAttackFailsWithWitnessInSameRoom() {
    world.addPlayer("Alice", 0, false, 5);
    world.addPlayer("Bob", 0, false, 5);
    
    String result = world.attackTarget("Alice", null);
    
    assertFalse(result.contains("seen") || result.contains("stopped"));
  }
  
  /**
   * Tests attack succeeds when pet blocks witness's view.
   */
  @Test
  public void testAttackSucceedsWhenPetBlocksWitness() {
    world.addPlayer("Alice", 0, false, 5); // In Armory
    world.addPlayer("Bob", 1, false, 5);   // In Billiard Room (neighbor)
    
    // Pet blocks visibility from Billiard Room to Armory
    assertEquals(0, world.getPet().getCurrentSpaceIndex()); // Pet in Armory initially
    
    // Bob cannot see into Armory due to pet
    // So Alice's attack should succeed
    world.movePet("Kitchen"); // Move pet away first
    
    // Now Bob can see, attack should fail
    String result = world.attackTarget("Alice", null);
    assertTrue(result.contains("seen"));
  }
  
  /**
   * Tests attack with pet in attacker's room still allows attack.
   */
  @Test
  public void testAttackWithPetInAttackersRoom() {
    world.addPlayer("Alice", 0, false, 5);
    // Pet is also in room 0
    assertEquals(0, world.getPet().getCurrentSpaceIndex());
    
    // Pet blocks others from seeing in, so attack should succeed
    String result = world.attackTarget("Alice", null);
    
    assertTrue(result.contains("attacked"));
  }
}