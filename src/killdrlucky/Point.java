package killdrlucky;

import java.util.Objects;

/**
 * Represent a point with 2 int (row, col).
 */
public final class Point {
  
  private final int row;
  private final int col;
  
  /**.
   *
   * @param row row index
   * @param col col index
   */
  public Point(int row, int col) {
    if (row < 0 || col < 0) {
      throw new IllegalArgumentException("Row and Col should be non-negative integers.");
    }
    this.row = row;
    this.col = col;
    
  }
  
  public int getRow() {
    return this.row;
  }
  
  public int getCol() {
    return this.col;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Point)) {
      return false;
    }
    Point p = (Point) o;
    return p.row == this.row && p.col == this.col;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }
  
  @Override
  public String toString() {
    return "(" + row + ", " + col + ")";
  }
}
