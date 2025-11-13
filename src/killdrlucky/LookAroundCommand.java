package killdrlucky;

/**
 * Command for looking around the player's current space.
 */
public class LookAroundCommand implements Command {
  private final GameModelApi model;
  private final String playerName;

  /**
   * Constructs a LookAroundCommand for a specific player.
   *
   * @param model the game model to query for information;
   *              must not be null
   * @param playerName the name of the player looking around;
   *                   must not be null or empty
   * @throws IllegalArgumentException if model is null,
   *                                  or if playerName is null or empty
   */
  public LookAroundCommand(GameModelApi model, String playerName) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    
    this.model = model;
    this.playerName = playerName;
  }

  @Override
  public String execute() {
    try {
      return model.lookAround(playerName);
    } catch (IllegalArgumentException e) {
      return "Error looking around: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return true;
  }

  @Override
  public String toString() {
    return String.format("LookAroundCommand[player=%s]", playerName);
  }
}