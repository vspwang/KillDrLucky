package killdrlucky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Room class implemented Space for single room.
 */
public class Room implements Space {

  private final int index;
  private final String name;
  private final Rect area;
  private final List<Item> items;

  /**
   * Creating a new room with following parameters.
   *
   * @param index room Index
   * @param name room Name
   * @param area room Rect
   * @param items items in room
   */
  public Room(int index, String name, Rect area, List<Item> items) {
    if (index < 0) {
      throw new IllegalArgumentException("Index must be non-negative.");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank.");
    }
    if (area == null) {
      throw new IllegalArgumentException("Area cannot be null.");
    }
    if (items == null) {
      throw new IllegalArgumentException("Items cannot be null.");
    }
    this.index = index;
    this.name = name;
    this.area = area;
    this.items = new ArrayList<>(items);
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Rect getArea() {
    return area;
  }

  @Override
  public List<Item> getItems() {
    return Collections.unmodifiableList(new ArrayList<>(items));
  }

  @Override
  public String toString() {
    return "Space{" + index + ", name='" + name + "', area=" + area + "}";
  }
}
