package killdrlucky;

import java.util.Objects;

/**
 * Represents Dr Lucky, the target of the players' attacks.
 */
public class Target implements Character {

  private final String name;
  private int health;
  private int currentSpaceIndex;

  /**
   * Constructs a Target.
   *
   * @param name       the target's name
   * @param health     initial health value
   * @param startIndex starting room index
   * @throws IllegalArgumentException if name is null/blank or health < 0 or index
   *                                  < 0
   */
  public Target(String name, int health, int startIndex) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Target name cannot be null or blank.");
    }
    if (health < 0) {
      throw new IllegalArgumentException("Health cannot be negative.");
    }
    if (startIndex < 0) {
      throw new IllegalArgumentException("Room index must be non-negative.");
    }

    this.name = name;
    this.health = health;
    this.currentSpaceIndex = startIndex;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getCurrentSpaceIndex() {
    return currentSpaceIndex;
  }

  @Override
  public void setCurrentSpaceIndex(int idx) {
    if (idx < 0) {
      throw new IllegalArgumentException("Room index must be non-negative.");
    }
    this.currentSpaceIndex = idx;
  }

  /**
   * Gets the current health of the target.
   *
   * @return the current health of the target
   */
  public int getHealth() {
    return health;
  }

  /**
   * Sets the current health of the target.
   */
  public void setHealth(int h) {
    if (h < 0) {
      throw new IllegalArgumentException("Health cannot be negative.");
    }
    this.health = h;
  }

  /**
   * Applies damage to the target, not letting health go below zero. 
   *
   * @param amount damage amount
   */
  public void takeDamage(int amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("Damage cannot be negative.");
    }
    health = Math.max(0, health - amount);
  }

  /**
   * Checks if the target is still alive.
   *
   * @return true if the target is still alive (health greater than 0), false otherwise
   */
  public boolean isAlive() {
    return health > 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Target)) {
      return false;
    }
    Target target = (Target) o;
    return health == target.health && currentSpaceIndex == target.currentSpaceIndex
        && name.equals(target.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, health, currentSpaceIndex);
  }

  @Override
  public String toString() {
    return "Target{" + "name='" + name + '\'' + ", health=" + health + ", currentSpaceIndex="
        + currentSpaceIndex + '}';
  }
}
