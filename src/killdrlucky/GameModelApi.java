package killdrlucky;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * This interface serves as the abstraction between the controller and the
 * game logic layer, exposing only high-level actions that represent
 * player turns, world interactions, and game state queries.
 */
public interface GameModelApi extends ReadOnlyWorld {

  /**
   * Adds a player to the world at the specified starting space.
   *
   * @param name               the player's unique name
   * @param startSpaceIndex    the index of the space where the player starts
   * @param computerControlled true if the player is AI-controlled
   * @param capacity           the maximum number of items the player can carry
   */
  void addPlayer(String name, int startSpaceIndex,
                 boolean computerControlled, int capacity);

  /**
   * Returns an immutable list of all players currently in the world.
   *
   * @return the list of players
   */
  List<Iplayer> getPlayers();

  /**
   * Moves a player in the given direction if the destination space
   * is a valid neighbor of the player's current location.
   *
   * @param name      the player's name
   * @param direction the direction to move (e.g., space number)
   * @return a textual message describing the result of the move
   */
  String movePlayer(String name, String direction);

  /**
   * Allows the specified player to pick up an item located in
   * their current space.
   *
   * @param name     the player's name
   * @param itemName the item to pick up
   * @return a textual message describing the pickup action
   */
  String pickUpItem(String name, String itemName);

  /**
   * Returns a description of all visible spaces and their contents
   * from the perspective of the specified player.
   *
   * @param name the player's name
   * @return a description of visible spaces
   */
  String lookAround(String name);

  /**
   * Returns a detailed description of a specific player,
   * including their current space and inventory.
   *
   * @param name the player's name
   * @return the player description
   */
  String describePlayer(String name);

  /**
   * Returns a detailed description of a specific space,
   * including items and players present.
   *
   * @param spaceName the space name
   * @return the space description
   */
  String describeSpace(String spaceName);

  /**
   * Automatically moves the target character one step forward in the
   * predefined movement sequence. The target moves once after each turn.
   */
  void moveTarget();

  /**
   * Performs an automatic action for a computer-controlled player.
   *
   * <p>Possible actions may include:
   * <ul>
   *   <li>Moving to a neighboring space</li>
   *   <li>Picking up an available item</li>
   *   <li>Looking around the current space</li>
   * </ul>
   *
   * @param playerName the name of the computer-controlled player
   * @return a textual message describing the chosen action
   */
  String autoAction(String playerName);

  /**
   * Checks whether the game has ended.
   *
   * @return {@code true} if the game is over; {@code false} otherwise
   */
  boolean isGameOver();

  /**
   * Immediately ends the game.
   * Once ended, no further actions should be processed.
   */
  void endGame();

  /**
   * Saves a visual representation of the world map to an image file.
   * The output should include all spaces, items, players, and target.
   *
   * @param filename the name of the output image file (e.g., "world.png")
   */
  void saveWorldImage(String filename) throws IOException;

  /**
   * Renders the current world map as a BufferedImage.
   *
   * @param cellSize the pixel size for each grid cell
   * @return a rendered BufferedImage representing the world
   */
  BufferedImage renderBufferedImage(int cellSize);
  
  /**
   * Attempts to attack the target character.
   * 
   * <p>The attack can use an item from the player's inventory or
   * "poke in the eye" for 1 damage if itemName is null or empty.
   * 
   * <p>The attack only succeeds if:
   * <ul>
   *   <li>The player is in the same space as the target</li>
   *   <li>The player is not seen by any other player</li>
   * </ul>
   *
   * @param playerName the name of the attacking player
   * @param itemName the name of the item to use, or null/"" to poke in the eye
   * @return a message describing the result of the attack
   * @throws IllegalArgumentException if player is not found
   */
  String attackTarget(String playerName, String itemName);

  /**
   * Moves the pet to the specified space.
   * 
   * <p>The pet makes the space it occupies invisible to neighbors.
   *
   * @param spaceName the name of the destination space
   * @return a message describing the result
   * @throws IllegalArgumentException if space is not found
   */
  String movePet(String spaceName);

  /**
   * Gets the pet character.
   *
   * @return the pet
   */
  Pet getPet();


}
