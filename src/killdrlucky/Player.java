package killdrlucky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a player in the Kill Dr Lucky game. Each player has a name,
 * current room index, and a list of items.
 */
public class Player implements Iplayer {

  private final String name;
  private int currentSpaceIndex;
  private final List<Item> items;
  private final int maxCapacity;

  /**
   * Constructs a Player.
   *
   * @param name       the player's name
   * @param startIndex the starting room index
   * @param maxCap     the max capacity of a player's inventory
   * @throws IllegalArgumentException if name is null/blank or index < 0 or items
   *                                  == null
   */
  public Player(String name, int startIndex, int maxCap) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Player name cannot be null or blank.");
    }
    if (startIndex < 0) {
      throw new IllegalArgumentException("Room index must be non-negative.");
    }
    if (maxCap < 0) {
      throw new IllegalArgumentException("Max capacity must be non-negative, got: " + maxCap);
    }
    this.maxCapacity = maxCap;
    this.name = name;
    this.currentSpaceIndex = startIndex;
    this.items = new ArrayList<>();
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

  @Override
  public List<Item> getItems() {
    return Collections.unmodifiableList(new ArrayList<>(items));
  }

  @Override
  public void addItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Cannot add a null item.");
    }
    if (items.size() >= maxCapacity) {
      throw new IllegalStateException("Cannot carry more items. Max capacity: " + maxCapacity);
    }
    items.add(item);
  }

  @Override
  public void removeItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Cannot remove a null item.");
    }
    items.remove(item);
  }
  
  @Override
  public int getMaxCapacity() {
    return maxCapacity;
  }
  
  @Override
  public int getCurrentCapacity() {
    return items.size();
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
    return currentSpaceIndex == player.currentSpaceIndex 
        && maxCapacity == player.maxCapacity
        && name.equals(player.name)
        && items.equals(player.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, currentSpaceIndex, items, maxCapacity);
  }

  @Override
  public String toString() {
    return String.format("Player{name='%s', space=%d, items=%d/%d}",
        name, currentSpaceIndex, items.size(), maxCapacity);
  }

  @Override
  public boolean isComputerControlled() {
    return false;
  }
  
  @Override
  public boolean canCarryMore() {
    return items.size() < maxCapacity;
  }

}
