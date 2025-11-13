package killdrlucky;

/**
 * Command for describing a specific player's current state.
 */
public class DescribePlayerCommand implements Command {
  private final GameModelApi model;
  private final String playerName;

  /**
   * Constructs a DescribePlayerCommand for a specific player.
   *
   * @param model the game model to query; must not be null
   * @param playerName the name of the player to describe;
   *                   must not be null or empty
   * @throws IllegalArgumentException if model is null,
   *                                  or if playerName is null or empty
   */
  public DescribePlayerCommand(GameModelApi model, String playerName) {
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
      return model.describePlayer(playerName);
    } catch (IllegalArgumentException e) {
      return "Error describing player: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return false;
  }

  @Override
  public String toString() {
    return String.format("DescribePlayerCommand[player=%s]", playerName);
  }
}