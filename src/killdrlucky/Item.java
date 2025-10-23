package killdrlucky;

/**
 * Represents an item that can exist in a room in the Kill Dr Lucky world.
 * Each item has a name, a damage value, and the index of the room it belongs to.
 */
public interface Item {

  /**.
   *
   * @return the name of the item
   * 
   */
  String getName();

  /**.
   *
   * @return the amount of damage this item can deal
   */
  int getDamage();

  /**.
   *
   * @return the index of the room that contains this item
   */
  int getRoomIndex();

  /**.
  *
  * @param i set room index of this item
  */
  void setRoomIndex(int i);
}
