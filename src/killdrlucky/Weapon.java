package killdrlucky;

import java.util.Objects;

/**
 * A concrete implementation of an Item. Represents a weapon that can be used to
 * attack Dr Lucky.
 */
public class Weapon implements Item {

  private final String name;
  private final int damage;
  private int roomIndex;

  /**
   * Constructs a Weapon object.
   *
   * @param nameParam      the name of the weapon
   * @param damageParam    the damage value (must be non-negative)
   * @param roomIndexParam the index of the room containing this weapon
   * @throws IllegalArgumentException if name is null/blank or damage < 0 or
   *                                  roomIndex < 0
   */
  public Weapon(String nameParam, int damageParam, int roomIndexParam) {
    if (nameParam == null || nameParam.isBlank()) {
      throw new IllegalArgumentException("Weapon name cannot be null or blank.");
    }
    if (damageParam < 0) {
      throw new IllegalArgumentException("Weapon damage must be non-negative.");
    }
    if (roomIndex < 0) {
      throw new IllegalArgumentException("Room index must be non-negative.");
    }

    this.name = nameParam;
    this.damage = damageParam;
    this.roomIndex = roomIndexParam;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDamage() {
    return damage;
  }

  @Override
  public int getRoomIndex() {
    return roomIndex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Weapon)) {
      return false;
    }
    Weapon weapon = (Weapon) o;
    return damage == weapon.damage && roomIndex == weapon.roomIndex && name.equals(weapon.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, damage, roomIndex);
  }

  @Override
  public String toString() {
    return "Weapon{" + "name='" + name + '\'' + ", damage=" + damage + ", roomIndex=" + roomIndex
        + '}';
  }

  @Override
  public void setRoomIndex(int i) {
    this.roomIndex = i;
    
  }
}
