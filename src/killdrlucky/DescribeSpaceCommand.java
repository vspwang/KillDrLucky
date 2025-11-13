package killdrlucky;

/**
 * Command for describing a specific space in the game world.
 */
public class DescribeSpaceCommand implements Command {
  private final GameModelApi model;
  private final String spaceName;

  /**
   * Constructs a DescribeSpaceCommand for a specific space.
   *
   * @param model the game model to query; must not be null
   * @param spaceName the name of the space to describe;
   *                  must not be null or empty
   * @throws IllegalArgumentException if model is null,
   *                                  or if spaceName is null or empty
   */
  public DescribeSpaceCommand(GameModelApi model, String spaceName) {
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
      return model.describeSpace(spaceName);
    } catch (IllegalArgumentException e) {
      return "Error describing space: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return false;
  }

  @Override
  public String toString() {
    return String.format("DescribeSpaceCommand[space=%s]", spaceName);
  }
}