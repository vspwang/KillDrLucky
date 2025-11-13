package killdrlucky;

import java.util.Objects;

/**
 * Represents the target character's pet in the Kill Dr Lucky game.
 */
public class Pet implements Character {
  
  private final String name;
  private int currentSpaceIndex;

  /**
   * Constructs a Pet with the specified name and starting location.
   *
   * @param name the pet's name; must not be null or blank
   * @param startIndex the index of the starting space; must be non-negative
   * @throws IllegalArgumentException if name is null/blank or startIndex is negative
   */
  public Pet(String name, int startIndex) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Pet name cannot be null or blank.");
    }
    if (startIndex < 0) {
      throw new IllegalArgumentException(
          "Start index must be non-negative, got: " + startIndex);
    }
    
    this.name = name;
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
      throw new IllegalArgumentException(
          "Space index must be non-negative, got: " + idx);
    }
    this.currentSpaceIndex = idx;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Pet)) {
      return false;
    }
    Pet pet = (Pet) o;
    return currentSpaceIndex == pet.currentSpaceIndex && name.equals(pet.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, currentSpaceIndex);
  }

  @Override
  public String toString() {
    return String.format("Pet{name='%s', space=%d}", name, currentSpaceIndex);
  }
}