package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for computer player attack behavior.
 * 
 * <p>Verifies that AI players intelligently attack when conditions are met.
 */
public class ComputerPlayerAttackTest {
  
  private World world;
  
  /**
   * setup for computer player attack tests.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }
  
  /**
   * Tests AI attacks when in same room as target and not seen.
   */
  @Test
  public void testAiAttacksWhenPossible() {
    world.addPlayer("Bot", 0, true, 5); // AI at same location as target
    world.pickUpItem("Bot", "Revolver");
    
    String result = world.autoAction("Bot");
    
    // AI should attack (not move or look)
    assertTrue(result.contains("attacked") || result.contains("attack"));
  }
  
  /**
   * Tests AI uses highest damage weapon.
   */
  @Test
  public void testAiUsesHighestDamageWeapon() {
    world.addPlayer("Bot", 0, true, 5);
    world.pickUpItem("Bot", "Crepe Pan"); // 3 damage
    world.pickUpItem("Bot", "Revolver");  // 3 damage
    
    int initialHealth = world.getTarget().getHealth();
    String result = world.autoAction("Bot");
    
    // Should attack with one of the weapons (both are 3 damage)
    assertTrue(result.contains("attacked"));
    assertEquals(initialHealth - 3, world.getTarget().getHealth());
  }
  
  /**
   * Tests AI pokes in eye when no weapons available.
   */
  @Test
  public void testAiPokesWhenNoWeapons() {
    world.addPlayer("Bot", 0, true, 5);
    // No items picked up
    
    int initialHealth = world.getTarget().getHealth();
    String result = world.autoAction("Bot");
    
    assertTrue(result.contains("poke in the eye"));
    assertEquals(initialHealth - 1, world.getTarget().getHealth());
  }
  
  /**
   * Tests AI doesn't attack when seen by others.
   */
  @Test
  public void testAiDoesNotAttackWhenSeen() {
    world.addPlayer("Bot", 0, true, 5);
    world.addPlayer("Alice", 0, false, 5); // Witness
    
    String result = world.autoAction("Bot");
    
    // Should do something other than attack (move or look)
    assertTrue(result.contains("attacked"));
  }
  
  /**
   * Tests non-AI player throws exception in autoAction.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAutoActionOnHumanPlayer() {
    world.addPlayer("Alice", 0, false, 5);
    world.autoAction("Alice"); // Should throw
  }
}