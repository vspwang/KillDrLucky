package killdrlucky;

import java.util.List;
import java.util.Set;

/**
 * Provides a read-only view of the Kill Dr Lucky world.
 * External components can query the state of the world but cannot modify it.
 */
public interface ReadOnlyWorld {

  /**.
   *
   * @return the name of the world
   */
  String getWorldName();

  /**.
   *
   * @return total number of rows in the world grid
   */
  int getRows();

  /**.
   *
   * @return total number of columns in the world grid
   */
  int getCols();

  /**.
   *
   * @return a read-only list of all spaces (rooms) in the world
   */
  List<Room> getSpaces();

  /**
   * Retrieves a space by index.
   *
   * @param idx the index of the space
   * @return the space with that index
   * @throws IllegalArgumentException if index is invalid
   */
  Room getSpace(int idx);

  /**.
   *
   * @return the target character (Dr Lucky)
   */
  Target getTarget();

  /**
   * Returns all neighboring space indices of a given space.
   *
   * @param idx the index of the space
   * @return a list of neighboring space indices
   */
  List<Integer> neighborsOf(int idx);

  /**
   * Returns all spaces visible from a given space.
   *
   * @param idx the index of the space
   * @return a set of indices representing visible spaces
   */
  Set<Integer> visibleFrom(int idx);

  /**
   * Provides a text description of the given space, including
   * its name, contained items, and visible neighboring spaces.
   *
   * @param idx the index of the space
   * @return a formatted string describing that space
   */
  String describeSpace(int idx);
}
