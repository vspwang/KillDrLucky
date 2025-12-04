package killdrlucky;

import java.util.Objects;

/**
 * Represent a rectangle by 2 Points: ul - upper left, lr - lower right.
 */
public class Rect {
  
  private final Point ul;
  private final Point lr;
  
  /**
   * Constructs a Rect with the given corner points.
   *
   * @param ulParam upper left Point
   * @param lrParam lower right Point
   */
  public Rect(Point ulParam, Point lrParam) {
    if (ulParam == null || lrParam == null) {
      throw new IllegalArgumentException("Points shouldn't be null.");
    }
    if (ulParam.getRow() > lrParam.getRow() || ulParam.getCol() > lrParam.getCol()) {
      throw new IllegalArgumentException("Point ul should be above and on the left of Point lr.");
    }
    this.ul = ulParam;
    this.lr = lrParam;
    
  }
  
  /**
   * Gets the upper left point.
   *
   * @return the upper left corner
   */
  public Point getUpperLeft() {
    return ul;
  }
  
  /**
   * Gets the lower right point.
   *
   * @return the lower right corner
   */
  public Point getLowerRight() {
    return lr;
  }
  
  /**
   * Calculates the width of this rectangle.
   *
   * @return the width of the Rect
   */
  public int width() {
    return lr.getCol() - ul.getCol() + 1;
  }
  
  /**
   * Calculates the height of this rectangle.
   *
   * @return the height of the Rect
   */
  public int height() {
    return lr.getRow() - ul.getRow() + 1;
  }
  
  /**
   * Checks if a point is contained within this rectangle.
   *
   * @param p the Point to be checked
   * @return true if the point is contained in the Rect, false otherwise
   */
  public boolean contains(Point p) {
    if (p == null) {
      throw new IllegalArgumentException("Cannot check a null Point.");
    }
    return p.getCol() >= ul.getCol() && p.getCol() <= lr.getCol()
        && p.getRow() >= ul.getRow() && p.getRow() <= lr.getRow();
  }
  
  /**
   * Checks if there is overlap with another rectangle.
   *
   * @param r the Rect to be checked
   * @return true if there is overlap with this Rect, false otherwise
   */
  public boolean intersects(Rect r) {
    // intersects: true only when there is positive-area overlap.
    // Touching at edge/corner is NOT overlap.
    if (r == null) {
      throw new IllegalArgumentException("Cannot check a null Rect.");
    }
    int left   = Math.max(this.ul.getCol(), r.ul.getCol());
    int right  = Math.min(this.lr.getCol(), r.lr.getCol());
    int top    = Math.max(this.ul.getRow(), r.ul.getRow());
    int bottom = Math.min(this.lr.getRow(), r.lr.getRow());

    // strict > to exclude shared edges/corners
    return (right > left) && (bottom > top);
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Rect)) {
      return false;
    }
    Rect r = (Rect) o;
    return ul.equals(r.ul) && lr.equals(r.lr);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(ul, lr);
  }
  
  @Override
  public String toString() {
    return "[" + ul + " to " + lr + "]";
  }

}
