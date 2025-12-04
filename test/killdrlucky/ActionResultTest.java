package killdrlucky;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

/**
 * Test class for ActionResult.
 */
public class ActionResultTest {

  @Test
  public void testActionResult_successCase() {
    ActionResult result = new ActionResult(true, "Player moved successfully", true);
    
    assertTrue(result.isSuccess());
    assertEquals("Player moved successfully", result.getMessage());
    assertTrue(result.isTurnAction());
  }

  @Test
  public void testActionResult_failureCase() {
    ActionResult result = new ActionResult(false, "Invalid move", false);
    
    assertFalse(result.isSuccess());
    assertEquals("Invalid move", result.getMessage());
    assertFalse(result.isTurnAction());
  }

  @Test
  public void testIsSuccess_true() {
    ActionResult result = new ActionResult(true, "Success", true);
    assertTrue(result.isSuccess());
  }

  @Test
  public void testIsSuccess_false() {
    ActionResult result = new ActionResult(false, "Failure", false);
    assertFalse(result.isSuccess());
  }

  @Test
  public void testGetMessage() {
    ActionResult result = new ActionResult(true, "Test message", true);
    assertEquals("Test message", result.getMessage());
  }

  @Test
  public void testIsTurnAction_true() {
    ActionResult result = new ActionResult(true, "Action", true);
    assertTrue(result.isTurnAction());
  }

  @Test
  public void testIsTurnAction_false() {
    ActionResult result = new ActionResult(true, "Action", false);
    assertFalse(result.isTurnAction());
  }
}