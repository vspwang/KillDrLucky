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
   * @param indexParam room Index
   * @param nameParam room Name
   * @param areaParam room Rect
   * @param itemsParam items in room
   */
  public Room(int indexParam, String nameParam, Rect areaParam, List<Item> itemsParam) {
    if (indexParam < 0) {
      throw new IllegalArgumentException("Index must be non-negative.");
    }
    if (nameParam == null || nameParam.isBlank()) {
      throw new IllegalArgumentException("Name cannot be null or blank.");
    }
    if (areaParam == null) {
      throw new IllegalArgumentException("Area cannot be null.");
    }
    if (itemsParam == null) {
      throw new IllegalArgumentException("Items cannot be null.");
    }
    this.index = indexParam;
    this.name = nameParam;
    this.area = areaParam;
    this.items = new ArrayList<>(itemsParam);
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
