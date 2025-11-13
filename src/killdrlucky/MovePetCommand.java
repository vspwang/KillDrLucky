package killdrlucky;

/**
 * Command for moving the pet to a specified space.
 * 
 * <p>Moving the pet affects visibility - the space with the pet
 * cannot be seen by its neighbors.
 * 
 * <p>This is a turn action.
 */
public class MovePetCommand implements Command {
  private final GameModelApi model;
  private final String spaceName;

  /**
   * Constructs a MovePetCommand.
   *
   * @param model the game model; must not be null
   * @param spaceName the name of the destination space; must not be null or empty
   * @throws IllegalArgumentException if parameters are invalid
   */
  public MovePetCommand(GameModelApi model, String spaceName) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (spaceName == null || spaceName.trim().isEmpty()) {
      throw new IllegalArgumentException("Space name cannot be null or empty");
    }
    
    this.model = model;
    this.spaceName = spaceName;
  }

  @Override
  public String execute() {
    try {
      return model.movePet(spaceName);
    } catch (IllegalArgumentException e) {
      return "✗ Error: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return true;
  }

  @Override
  public String toString() {
    return String.format("MovePetCommand[destination=%s]", spaceName);
  }
}