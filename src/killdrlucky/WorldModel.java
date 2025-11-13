package killdrlucky;

import java.awt.image.BufferedImage;

/**
 * Represents a mutable world model for the Kill Dr Lucky game. Extends
 * ReadOnlyWorld with operations that modify the game state.
 */
public interface WorldModel extends ReadOnlyWorld {

  /**
   * Moves the target character (Dr Lucky) to the designated space. Automatically, the order of
   * movement follows space index sequence (0 → 1 → ... → n−1 → 0).
   */
  void moveTarget();

  /**
   * Renders the world as a 2D image for visualization.
   *
   * @param cellSize pixel size per grid cell
   * @return a rendered BufferedImage of the world
   */
  BufferedImage renderBufferedImage(int cellSize);

  /**
   * Checks whether a given player can attack the target using the specified item.
   *
   * @param playerId the ID or index of the player
   * @param itemId   the ID or index of the item
   * @return the result of the pre-attack validation
   */
  AttackStatus canAttack(int playerId, int itemId);

  /**
   * Performs an attack attempt by a player using a given item. Automatically
   * applies damage if the attack is valid.
   *
   * @param playerId the ID or index of the attacking player
   * @param itemId   the ID or index of the item used
   * @return the outcome of the attack
   */
  AttackStatus attack(int playerId, int itemId);
}
