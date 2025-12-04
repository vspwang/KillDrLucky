package killdrlucky;

import java.util.List;
import java.util.Set;

/**
 * Provides a read-only view of the Kill Dr Lucky world.
 * External components can query the state of the world but cannot modify it.
 */
public interface ReadOnlyWorld {

  /**
   * Returns the name of the world.
   *
   * @return the name of the world
   */
  String getWorldName();

  /**
   * Returns the total number of rows in the world grid.
   *
   * @return total number of rows in the world grid
   */
  int getRows();

  /**
   * Returns the total number of cols in the world grid.
   *
   * @return total number of rows in the world grid
   */
  int getCols();

  /**
   * Returns the list of spaces in the world.
   *
   * @return a read-only list of all spaces (rooms) in the world
   */
  List<Space> getSpaces();

  /**
   * Retrieves a space by index.
   *
   * @param idx the index of the space
   * @return the space with that index
   * @throws IllegalArgumentException if index is invalid
   */
  Space getSpace(int idx);

  /**
   * Returns the target of the world.
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
  
  /**
   * Returns all items in the world.
   *
   * @return list of all items
   */
  List<Item> getItems();
  
  /**
   * Returns the pet in the world.
   *
   * @return the pet character
   */
  Pet getPet();
  
  /**
   * Returns all players currently in the world.
   *
   * @return a read-only list of all players
   */
  List<Iplayer> getPlayers();
}
