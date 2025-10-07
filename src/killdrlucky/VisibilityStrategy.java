package killdrlucky;

import java.util.List;
import java.util.Set;

/**
 * Strategy interface for computing visibility relationships between spaces
 * in the Kill Dr Lucky world.
 * Different visibility algorithms (e.g., axis-aligned, diagonal, graph-based)
 * can be implemented by different strategy classes.
 */
public interface VisibilityStrategy {

  /**
   * Computes the set of space indices that are visible from a given space.
   *
   * @param idx    the index of the source space
   * @param spaces the list of all spaces in the world
   * @return a set of indices of spaces that are visible from the given space
   * @throws IllegalArgumentException if idx is invalid or spaces is null
   */
  Set<Integer> visibleFrom(int idx, List<Room> spaces);
}
