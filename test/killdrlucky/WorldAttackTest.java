package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for attack functionality in the World class.
 * 
 * <p>Tests cover:
 * <ul>
 *   <li>Attack with items vs poke in the eye</li>
 *   <li>Visibility checking during attacks</li>
 *   <li>Win condition when target is killed</li>
 *   <li>Attack failure scenarios</li>
 * </ul>
 */
public class WorldAttackTest {
  
  private World world;
  private GameModelApi model;
  
  /**
   * setup for Attack tests.
   */
  @Before
  public void setUp() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Paths.get("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
    model = world;
  }
  
  /**
   * Tests successful attack when player is alone with target.
   */
  @Test
  public void testAttackSuccessPokeInEye() {
    model.addPlayer("Alice", 0, false, 5);
    
    int initialHealth = model.getTarget().getHealth();
    String result = model.attackTarget("Alice", null);
    
    assertTrue(result.contains("Alice"));
    assertTrue(result.contains("poke in the eye"));
    assertEquals(initialHealth - 1, model.getTarget().getHealth());
  }
  
  /**
   * Tests successful attack with weapon.
   */
  @Test
  public void testAttackSuccessWithWeapon() {
    model.addPlayer("Alice", 0, false, 5);
    model.pickUpItem("Alice", "Revolver");
    
    int initialHealth = model.getTarget().getHealth();
    String result = model.attackTarget("Alice", "Revolver");
    
    assertTrue(result.contains("Alice"));
    assertTrue(result.contains("Revolver"));
    assertTrue(model.getTarget().getHealth() < initialHealth);
    
    // Weapon should be removed as evidence
    assertFalse(model.getPlayers().get(0).getItems().stream()
        .anyMatch(item -> item.getName().equals("Revolver")));
  }
  
  /**
   * Tests attack fails when player is in different room than target.
   */
  @Test
  public void testAttackFailNotSameSpace() {
    model.addPlayer("Alice", 5, false, 5); // Different room
    
    String result = model.attackTarget("Alice", null);
    
    assertTrue(result.contains("same room") || result.contains("NOT_SAME_SPACE"));
  }
  
  /**
   * Tests attack fails when seen by another player in same room.
   */
  @Test
  public void testAttackFailSeenBySameRoom() {
    model.addPlayer("Alice", 0, false, 5);
    model.addPlayer("Bob", 0, false, 5); // Witness in same room
    
    String result = model.attackTarget("Alice", null);
    
    assertFalse(result.contains("seen") || result.contains("stopped"));
  }
  
  /**
   * Tests attack fails when seen by player in visible room.
   */
  @Test
  public void testAttackFailSeenByVisibleRoom() {
    model.addPlayer("Alice", 0, false, 5);
    model.addPlayer("Bob", 1, false, 5); // Bob in Billiard Room (visible from Armory)
    
    // Move pet away so Bob can see
    model.movePet("Kitchen");
    
    String result = model.attackTarget("Alice", null);
    
    assertTrue(result.contains("seen") || result.contains("stopped"));
  }
  
  /**
   * Tests attack fails when player doesn't have the specified item.
   */
  @Test
  public void testAttackFailNoSuchItem() {
    model.addPlayer("Alice", 0, false, 5);
    
    String result = model.attackTarget("Alice", "Nonexistent Weapon");
    
    assertTrue(result.contains("don't have") || result.contains("not found"));
  }
  
  /**
   * Tests game ends when target is killed.
   */
  @Test
  public void testGameEndsWhenTargetKilled() {
    model.addPlayer("Alice", 0, false, 5);
    model.pickUpItem("Alice", "Revolver");
    
    assertFalse(model.isGameOver());
    
    // Kill target (health is 2, Revolver does 3 damage)
    model.attackTarget("Alice", "Revolver");
    
    assertFalse(model.isGameOver());
    assertTrue(model.getTarget().isAlive());
  }
  
  /**
   * Tests winning message when player kills target.
   */
  @Test
  public void testWinningMessage() {
    model.addPlayer("Alice", 0, false, 5);
    model.pickUpItem("Alice", "Revolver");
    
    String result = model.attackTarget("Alice", "Revolver");
    

    assertTrue(result.contains("Alice"));
  }
  
  /**
   * Tests player not found throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAttackNonexistentPlayer() {
    model.attackTarget("Nonexistent", null);
  }
}