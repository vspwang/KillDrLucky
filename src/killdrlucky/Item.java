package killdrlucky;

/**
 * Represents an item that can exist in a room in the Kill Dr Lucky world.
 * Each item has a name, a damage value, and the index of the room it belongs to.
 */
public interface Item {

  /**
   * Returns the name of the item.
   *
   * @return the name of the item
   */
  String getName();

  /**
   * Returns the amount of damage this item can deal.
   *
   * @return the amount of damage this item can deal
   */
  int getDamage();

  /**
   * Returns the index of the room that contains this item.
   *
   * @return the index of the room that contains this item
   */
  int getRoomIndex();

  /**
   * Sets the room index of this item.
   *
   * @param i the new room index
   */
  void setRoomIndex(int i);
}
