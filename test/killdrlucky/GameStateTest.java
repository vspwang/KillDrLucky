package killdrlucky;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

/**
 * Test class for GameState.
 */
public class GameStateTest {

  @Test
  public void testGameState_allFieldsValid() {
    GameState state = new GameState(
        "Alice", false, 3, "Kitchen", 1, 50, 5, 2, false, ""
    );
    
    assertEquals("Alice", state.currentPlayerName);
    assertFalse(state.isCurrentPlayerAi);
    assertEquals(3, state.currentPlayerSpace);
    assertEquals("Kitchen", state.currentPlayerLocation);
    assertEquals(1, state.currentTurn);
    assertEquals(50, state.targetHealth);
    assertEquals(5, state.targetSpace);
    assertEquals(2, state.petSpace);
    assertFalse(state.gameOver);
    assertEquals("", state.winner);
  }

  @Test
  public void testGameState_currentPlayerName() {
    GameState state = new GameState(
        "Bob", true, 0, "Hall", 0, 50, 0, 0, false, ""
    );
    
    assertEquals("Bob", state.currentPlayerName);
  }

  @Test
  public void testGameState_isCurrentPlayerAi() {
    GameState state = new GameState(
        "AI1", true, 0, "Hall", 0, 50, 0, 0, false, ""
    );
    
    assertTrue(state.isCurrentPlayerAi);
  }

  @Test
  public void testGameState_gameOverWithWinner() {
    GameState state = new GameState(
        "Alice", false, 5, "Library", 10, 0, 5, 2, true, "Alice"
    );
    
    assertTrue(state.gameOver);
    assertEquals("Alice", state.winner);
  }

  @Test
  public void testGameState_noWinner() {
    GameState state = new GameState(
        "Bob", false, 3, "Kitchen", 5, 30, 4, 1, false, ""
    );
    
    assertFalse(state.gameOver);
    assertEquals("", state.winner);
  }
}