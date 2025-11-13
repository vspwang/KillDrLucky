package killdrlucky;

/**
 * Command for moving a player to a neighboring space.
 */
public class MoveCommand implements Command {
  private final GameModelApi model;
  private final String playerName;
  private final String destination;

  /**
   * Constructs a MoveCommand for moving a player to a destination.
   *
   * @param model the game model to execute the move on; must not be null
   * @param playerName the name of the player to move; must not be null or empty
   * @param destination the name or index of the destination space;
   * 
   * @throws IllegalArgumentException if any parameter is null, 
   *          or if playerName or destination is empty
   */
  public MoveCommand(GameModelApi model, String playerName, String destination) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    if (destination == null || destination.trim().isEmpty()) {
      throw new IllegalArgumentException("Destination cannot be null or empty");
    }

    this.model = model;
    this.playerName = playerName;
    this.destination = destination;
  }

  @Override
  public String execute() {
    try {
      return model.movePlayer(playerName, destination);
    } catch (IllegalArgumentException e) {
      return "Error: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return true; 
  }
  
  @Override
  public String toString() {
    return String.format("MoveCommand[player=%s, destination=%s]", 
                         playerName, destination);
  }
  
}