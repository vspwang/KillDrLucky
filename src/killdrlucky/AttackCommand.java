package killdrlucky;

/**
 * Command for attempting to attack the target character.
 * 
 * <p>An attack can be made with an item or by "poking in the eye" (1 damage).
 * The attack succeeds only if the player is not seen by others.
 * 
 * <p>This is a turn action.
 */
public class AttackCommand implements Command {
  private final GameModelApi model;
  private final String playerName;
  private final String itemName; // null means "poke in the eye"

  /**
   * Constructs an AttackCommand.
   * 
   * @param model the game model; must not be null
   * @param playerName the name of the attacking player; must not be null or empty
   * @param itemName the name of the item to use, or null to poke in the eye
   * @throws IllegalArgumentException if model or playerName is invalid
   */
  public AttackCommand(GameModelApi model, String playerName, String itemName) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty");
    }
    
    this.model = model;
    this.playerName = playerName;
    this.itemName = itemName; // Can be null
  }

  @Override
  public String execute() {
    try {
      return model.attackTarget(playerName, itemName);
    } catch (Exception e) {
      return "✗ Error: " + e.getMessage();
    }
  }

  @Override
  public boolean isTurnAction() {
    return true;
  }

  @Override
  public String toString() {
    return String.format("AttackCommand[player=%s, item=%s]", 
                         playerName, 
                         itemName == null ? "poke" : itemName);
  }
}