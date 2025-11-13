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
  
  /**
   * Gets the maximum number of items this player can carry.
   *
   * @return the maximum carrying capacity
   */
  int getMaxCapacity();
  
  /**
   * Gets the current number of items being carried.
   *
   * @return the number of items in inventory
   */
  int getCurrentCapacity();

  /**
   * Checks if the player can carry more items.
   *
   * @return {@code true} if space is available; {@code false} otherwise
   */
  boolean canCarryMore();
}
