package killdrlucky;

/**
 * Command for picking up an item in the player's current space.
 */
public class PickUpCommand implements Command {
  private final GameModelApi model;
  private final String playerName;
  private final String itemName;

  /**
   * Constructs a PickUpCommand for picking up an item.
   *
   * @param model the game model to execute the pickup on; must not be null
   * @param playerName the name of the player picking up the item;
   *                   must not be null or empty
   * @param itemName the name of the item to pick up;
   *                 must not be null or empty
   * @throws IllegalArgumentException if any parameter is null,
   *                                  or if playerName or itemName is empty
   */
  public PickUpCommand(GameModelApi model, String playerName, String itemName) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    if (itemName == null || itemName.trim().isEmpty()) {
      throw new IllegalArgumentException("Item name cannot be null or empty");
    }
    
    this.model = model;
    this.playerName = playerName;
    this.itemName = itemName;
  }

  @Override
  public String execute() {
    try {
      return model.pickUpItem(playerName, itemName);
    } catch (Exception e) {
      return "Error picking up item: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return true;
  }

  @Override
  public String toString() {
    return String.format("PickUpCommand[player=%s, item=%s]", 
                         playerName, itemName);
  }
}