package killdrlucky;

/**
 * Immutable snapshot of current game state.
 * Returned by Model to prevent exposing internal objects.
 */
public class GameState {
  public final String currentPlayerName;
  public final boolean isCurrentPlayerAi;
  public final int currentPlayerSpace;
  public final String currentPlayerLocation;
  public final int currentTurn;
  public final int targetHealth;
  public final int targetSpace;
  public final int petSpace;
  public final boolean gameOver;
  public final String winner;
  
  /**
   * Creates a GameState snapshot.
   * 
   * @param currentPlayerNameParam the name of the current player whose turn it is
   * @param isCurrentPlayerAiParam true if the current player is computer-controlled, false if human
   * @param currentPlayerSpaceParam the space index where the current player is located
   * @param currentPlayerLocationParam the name of the space where the current player is located
   * @param currentTurnParam the current turn number (player index)
   * @param targetHealthParam the remaining health points of the target character
   * @param targetSpaceParam the space index where the target character is located
   * @param petSpaceParam the space index where the pet is located
   * @param gameOverParam true if the game has ended, false otherwise
   * @param winnerParam the name of the winner if game is over, empty string otherwise
   */
  public GameState(String currentPlayerNameParam, boolean isCurrentPlayerAiParam,
                   int currentPlayerSpaceParam, String currentPlayerLocationParam,
                   int currentTurnParam, int targetHealthParam, int targetSpaceParam,
                   int petSpaceParam, boolean gameOverParam, String winnerParam) {
    this.currentPlayerName = currentPlayerNameParam;
    this.isCurrentPlayerAi = isCurrentPlayerAiParam;
    this.currentPlayerSpace = currentPlayerSpaceParam;
    this.currentPlayerLocation = currentPlayerLocationParam;
    this.currentTurn = currentTurnParam;
    this.targetHealth = targetHealthParam;
    this.targetSpace = targetSpaceParam;
    this.petSpace = petSpaceParam;
    this.gameOver = gameOverParam;
    this.winner = winnerParam;
  }
}