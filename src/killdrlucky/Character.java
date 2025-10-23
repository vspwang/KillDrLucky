package killdrlucky;

/**
 * Represents a character in the Kill Dr Lucky world.
 * Both Player and Target implement this interface.
 */
public interface Character {

  /**
   * Gets the name of the character.
   *
   * @return the name of the character
   */
  String getName();

  /**
   * Returns the current space index where the player is located.
   *
   * @return the index of the room where the character currently is
   */
  int getCurrentSpaceIndex();

  /**
   * Sets the character's current room index.
   *
   * @param idx the new room index
   */
  void setCurrentSpaceIndex(int idx);
}
