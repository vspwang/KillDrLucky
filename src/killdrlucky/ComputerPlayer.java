package killdrlucky;

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
   * @param maxCap     the max capacity of a player's inventory
   */
  public ComputerPlayer(String name, int startIndex, int maxCap) {
    super(name, startIndex, maxCap);
  }

//  @Override
//  public String move(String destination, GameModelApi model) {
//    // For computer players, delegate to the model's autoAction
//    if (model == null) {
//      throw new IllegalArgumentException("Model cannot be null");
//    }
//    return model.autoAction(this.getName());
//  }
//
//  @Override
//  public String pickUp(String itemName, GameModelApi model) {
//    return "Computer-controlled players act automatically.";
//  }
//
//  @Override
//  public String lookAround(GameModelApi model) {
//    return "Computer-controlled players act automatically.";
//  }
  
  @Override
  public boolean isComputerControlled() {
    return true;
  }

  @Override
  public String toString() {
    return String.format("ComputerPlayer{name='%s', space=%d, items=%d/%d}",
                        getName(), getCurrentSpaceIndex(), 
                        getCurrentCapacity(), getMaxCapacity());
  }
}
