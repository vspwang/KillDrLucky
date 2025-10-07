package killdrlucky;

import java.util.Objects;

/**.
 * Represent a rectangle by 2 Points: ul - upper left, lr - lower right
 */
public class Rect {
  
  private final Point ul;
  private final Point lr;
  
  /**.
   *
   * @param ul upper left Point
   * @param lr lower right Point
   */
  public Rect(Point ul, Point lr) {
    if (ul == null || lr == null) {
      throw new IllegalArgumentException("Points shouldn't be null.");
    }
    if (ul.getRow() > lr.getRow() || ul.getCol() > lr.getCol()) {
      throw new IllegalArgumentException("Point ul should be above and on the left of Point lr.");
    }
    this.ul = ul;
    this.lr = lr;
    
  }
  
  public Point getUpperLeft() {
    return ul;
  }
  
  public Point getLowerRight() {
    return lr;
  }
  
  /**.
   *
   * @return returning the width of Rect
   */
  public int width() {
    return lr.getCol() - ul.getCol() + 1;
  }
  
  /**.
   *
   * @return returning the height of Rect
   */
  public int height() {
    return lr.getRow() - ul.getRow() + 1;
  }
  
  /**.
   *
   * @param p the Point to be checked
   * @return returning if it's contained in the Rect
   */
  public boolean contains(Point p) {
    if (p == null) {
      throw new IllegalArgumentException("Cannot check a null Point.");
    }
    return p.getCol() >= ul.getCol() && p.getCol() <= lr.getCol()
        && p.getRow() >= ul.getRow() && p.getRow() <= lr.getRow();
  }
  
  /**.
   *
   * @param r the Rect to be checked
   * @return returning if there is overlap with this Rect
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
