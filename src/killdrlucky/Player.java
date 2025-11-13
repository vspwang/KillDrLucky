package killdrlucky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
//
//  @Override
//  public String move(String destination, GameModelApi model) {
//    if (destination == null || destination.isEmpty()) {
//      throw new IllegalArgumentException("destination cannot be null or empty");
//    }
//    if (model == null) {
//      throw new IllegalArgumentException("Model cannot be null");
//    }
//
//    // Find neighbors of current space
//    int current = this.getCurrentSpaceIndex();
//    List<Integer> neighbors = model.neighborsOf(current);
//
//    if (neighbors == null || neighbors.isEmpty()) {
//      return this.name + " cannot move — no available neighbors.";
//    }
//
//    // Determine the next room by destination keyword
//    // For simplicity, destination can be "0", "1", etc. (index of neighbor)
//    try {
//      int idx = Integer.parseInt(destination);
//      if (idx < 0 || idx >= neighbors.size()) {
//        return "Invalid move destination.";
//      }
//      int nextIndex = neighbors.get(idx);
//      this.setCurrentSpaceIndex(nextIndex);
//      return this.name + " moved to " + model.getSpace(nextIndex).getName();
//    } catch (NumberFormatException e) {
//      // destination as text (e.g., "Kitchen")
//      for (int nextIndex : neighbors) {
//        if (model.getSpace(nextIndex).getName().equalsIgnoreCase(destination)) {
//          this.setCurrentSpaceIndex(nextIndex);
//          return this.name + " moved to " + model.getSpace(nextIndex).getName();
//        }
//      }
//      return "No neighboring space named '" + destination + "'.";
//    }
//  }
//
//  @Override
//  public String pickUp(String itemName, GameModelApi model) {
//    if (itemName == null || itemName.isEmpty()) {
//      return "Invalid item name.";
//    }
//
//    // Find the space the player is currently in
//    int currentIdx = getCurrentSpaceIndex();
//
//    // Find the item in the world
//    Item targetItem = null;
//    for (Item it : model.getItems()) {
//      if (it.getName().equalsIgnoreCase(itemName) && it.getRoomIndex() == currentIdx) {
//        targetItem = it;
//        break;
//      }
//    }
//
//    if (targetItem == null) {
//      return "Item not found in this space.";
//    }
//
//    // Pick up the item
//    addItem(targetItem);
//    targetItem.setRoomIndex(-1); // mark as removed from world
//    return getName() + " picked up " + targetItem.getName() + ".";
//  }
//
//  @Override
//  public String lookAround(GameModelApi model) {
//    // Find current location
//    int currentIdx = getCurrentSpaceIndex();
//
//    // Describe current space
//    StringBuilder sb = new StringBuilder();
//    sb.append("You are in ").append(model.getSpaces().get(currentIdx).getName()).append(".\n");
//
//    // List items in the same space
//    List<Item> itemsHere = model.getItems().stream().filter(it -> it.getRoomIndex() == currentIdx)
//        .collect(Collectors.toList());
//
//    if (itemsHere.isEmpty()) {
//      sb.append("No items here.\n");
//    } else {
//      sb.append("Items: ");
//      for (Item it : itemsHere) {
//        sb.append(it.getName()).append("(").append(it.getDamage()).append(") ");
//      }
//      sb.append("\n");
//    }
//
//    // List visible rooms
//    Set<Integer> visible = model.visibleFrom(currentIdx);
//    if (visible.isEmpty()) {
//      sb.append("No rooms visible from here.\n");
//    } else {
//      sb.append("You can see: ");
//      for (int idx : visible) {
//        sb.append(model.getSpaces().get(idx).getName()).append(" ");
//      }
//      sb.append("\n");
//    }
//
//    return sb.toString().trim();
//  }

}
