package killdrlucky;

import java.util.List;

/**
 * Represents a room or space in the Kill Dr Lucky world.
 * Provides read-only access to its identity, geometry, and contained items.
 */
public interface Space {

  /**
   * Returns the index of this space in the world (0-based).
   *
   * @return the index of this space in the world (0-based)
   */
  int getIndex();

  /**
   * Returns the name of this space (e.g., "Kitchen", "Hallway").
   *
   * @return the name of this space (e.g., "Kitchen", "Hallway")
   */
  String getName();

  /**
   * Returns the rectangular area occupied by this space.
   *
   * @return the rectangular area occupied by this space
   */
  Rect getArea();

  /**
   * Get a list of Items.
   *
   * @return a defensive copy of all items contained in this space
   */
  List<Item> getItems();
}
