package killdrlucky;

import java.util.List;

/**
 * Represents a computer-controlled player that performs automatic actions.
 */
public class ComputerPlayer extends Player {

  /**.
   * This constructor initializes the player's position in the world,
   * assigns their carrying capacity, and sets up an empty inventory list.
   *
   * @param name       the player's unique name; must not be null or empty
   * @param startIndex the index of the space where the player starts
   * @param items      the maximum number of items the player can carry
   */
  public ComputerPlayer(String name, int startIndex, List<Item> items) {
    super(name, startIndex, items);
  }

  @Override
  public String move(String destination, GameModelApi model) {
    // For computer players, delegate to the model's autoAction
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    return model.autoAction(this.getName());
  }

  @Override
  public String pickUp(String itemName, GameModelApi model) {
    return "Computer-controlled players act automatically.";
  }

  @Override
  public String lookAround(GameModelApi model) {
    return "Computer-controlled players act automatically.";
  }
  
  @Override
  public boolean isComputerControlled() {
    return true;
  }

}
