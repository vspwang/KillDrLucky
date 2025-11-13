package killdrlucky;

/**
 * Command for adding a new player to the game.
 */
public class AddPlayerCommand implements Command {
  private final GameModelApi model;
  private final String name;
  private final int startIndex;
  private final boolean isAi;
  private final int capacity;

  /**
   * Constructs an AddPlayerCommand with all player properties.
   *
   * @param model the game model to add the player to; must not be null
   * @param name the unique name for the player; must not be null or empty
   * @param startIndex the index of the starting space; must be valid
   * @param isAi {@code true} for computer-controlled player,
   *             {@code false} for human-controlled
   * @param capacity the maximum number of items the player can carry;
   *                 must be non-negative
   * @throws IllegalArgumentException if model is null, name is null/empty,
   *                                  startIndex is negative, or capacity is negative
   */
  public AddPlayerCommand(GameModelApi model, String name, 
                         int startIndex, boolean isAi, int capacity) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    if (startIndex < 0) {
      throw new IllegalArgumentException(
          "Start index must be non-negative, got: " + startIndex);
    }
    if (capacity < 0) {
      throw new IllegalArgumentException(
          "Capacity must be non-negative, got: " + capacity);
    }
    
    this.model = model;
    this.name = name;
    this.startIndex = startIndex;
    this.isAi = isAi;
    this.capacity = capacity;
  }

  @Override
  public String execute() {
    try {
      model.addPlayer(name, startIndex, isAi, capacity);
      return String.format("Added %s player: %s at space %d (capacity: %d)",
                          isAi ? "computer" : "human", 
                          name, 
                          startIndex,
                          capacity);
    } catch (IllegalArgumentException e) {
      return "Error adding player: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return false;
  }

  @Override
  public String toString() {
    return String.format(
        "AddPlayerCommand[name=%s, start=%d, ai=%b, capacity=%d]",
        name, startIndex, isAi, capacity);
  }
}