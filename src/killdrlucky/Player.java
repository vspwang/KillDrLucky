package killdrlucky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a player in the Kill Dr Lucky game. Each player has a name,
 * current room index, and a list of items.
 */
public class Player implements Character {

  private final String name;
  private int currentSpaceIndex;
  private final List<Item> items;

  /**
   * Constructs a Player.
   *
   * @param name       the player's name
   * @param startIndex the starting room index
   * @param items      the initial list of items (can be empty, but not null)
   * @throws IllegalArgumentException if name is null/blank or index < 0 or items
   *                                  == null
   */
  public Player(String name, int startIndex, List<Item> items) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank.");
    }
    if (startIndex < 0) {
      throw new IllegalArgumentException("Room index must be non-negative.");
    }
    if (items == null) {
      throw new IllegalArgumentException("Item list cannot be null.");
    }

    this.name = name;
    this.currentSpaceIndex = startIndex;
    this.items = new ArrayList<>(items); // defensive copy
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getCurrentSpaceIndex() {
    return currentSpaceIndex;
  }

  @Override
  public void setCurrentSpaceIndex(int idx) {
    if (idx < 0) {
      throw new IllegalArgumentException("Room index must be non-negative.");
    }
    this.currentSpaceIndex = idx;
  }

  /**.
   *
   * @return an unmodifiable defensive copy of the player's items
   */
  public List<Item> getItems() {
    return Collections.unmodifiableList(new ArrayList<>(items));
  }

  /**
   * Adds an item to the player's inventory.
   *
   * @param item the item to add
   */
  public void addItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Cannot add a null item.");
    }
    items.add(item);
  }

  /**
   * Removes an item from the player's inventory.
   *
   * @param item the item to remove
   */
  public void removeItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Cannot remove a null item.");
    }
    items.remove(item);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    Player player = (Player) o;
    return currentSpaceIndex == player.currentSpaceIndex && name.equals(player.name)
        && items.equals(player.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, currentSpaceIndex, items);
  }

  @Override
  public String toString() {
    return "Player{" + "name='" + name + '\'' + ", currentSpaceIndex=" + currentSpaceIndex
        + ", items=" + items + '}';
  }
}
