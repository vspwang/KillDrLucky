package killdrlucky;

import java.util.List;

/**
 * Represents a player in the game world.
 */
public interface Iplayer extends Character {
  
  /**
   * Returns true if this player is controlled by the computer, or false if human-controlled.
   *
   * @return {@code true} if this player is AI-controlled; {@code false} otherwise
   */
  boolean isComputerControlled();

  /**
   * Returns the list of items currently carried by this player.
   *
   * @return an immutable or modifiable list of {@link Item} objects held by the player
   */
  List<Item> getItems();

  /**
   * Performs a move action in the specified direction.
   *
   * @param direction the direction to move (e.g., "north", "east")
   * @param model     the active game model context
   * @return textual result or description of the move
   */
  String move(String direction, GameModelApi model);

  /**
   * Performs a pick-up action for an item in the current space.
   *
   * @param itemName the name of the item to pick up
   * @param model    the active game model context
   * @return textual result of the pickup action
   */
  String pickUp(String itemName, GameModelApi model);

  /**
   * Allows the player to look around from the current space.
   *
   * @param model the active game model context
   * @return description of visible spaces and contents
   */
  String lookAround(GameModelApi model);
  
  /**
   * Adds an item to the player's inventory.
   *
   * @param item the item to add
   */
  void addItem(Item item);
  
  /**
   * Removes an item from the player's inventory.
   *
   * @param item the item to remove
   */
  void removeItem(Item item);
}
