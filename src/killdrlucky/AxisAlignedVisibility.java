package killdrlucky;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Axis-aligned visibility implementation.
 * A space can "see" another space if: - They share the same row range or column
 * range alignment (axis-aligned), AND - No other space lies directly between
 * them along that row or column.
 * Diagonal visibility is not allowed.
 */
public class AxisAlignedVisibility implements VisibilityStrategy {

  @Override
  public Set<Integer> visibleFrom(int idx, List<Room> spaces) {
    if (spaces == null || spaces.isEmpty()) {
      throw new IllegalArgumentException("Spaces cannot be null or empty.");
    }
    if (idx < 0 || idx >= spaces.size()) {
      throw new IllegalArgumentException("Invalid space index: " + idx);
    }

    Set<Integer> visible = new HashSet<>();
    Room source = spaces.get(idx);
    Rect srcRect = source.getArea();

    int srcTop = srcRect.getUpperLeft().getRow();
    int srcBottom = srcRect.getLowerRight().getRow();
    int srcLeft = srcRect.getUpperLeft().getCol();
    int srcRight = srcRect.getLowerRight().getCol();

    for (int i = 0; i < spaces.size(); i++) {
      if (i == idx) {
        continue;
      }

      Room target = spaces.get(i);
      Rect tgtRect = target.getArea();

      int tgtTop = tgtRect.getUpperLeft().getRow();
      int tgtBottom = tgtRect.getLowerRight().getRow();
      int tgtLeft = tgtRect.getUpperLeft().getCol();
      int tgtRight = tgtRect.getLowerRight().getCol();

      // Same row alignment (horizontal visibility)
      boolean sameRowBand = (tgtTop <= srcBottom && tgtBottom >= srcTop);

      // Same column alignment (vertical visibility)
      boolean sameColBand = (tgtLeft <= srcRight && tgtRight >= srcLeft);

      if (sameRowBand || sameColBand) {
        // Check if blocked by another space between source and target
        boolean blocked = false;
        for (int j = 0; j < spaces.size(); j++) {
          if (j == idx || j == i) {
            continue;
          }

          Room blocker = spaces.get(j);
          Rect b = blocker.getArea();

          // Case 1: horizontal alignment, check x-axis overlap between source and target
          if (sameRowBand && b.getUpperLeft().getRow() <= srcBottom
              && b.getLowerRight().getRow() >= srcTop) {
            if (b.getUpperLeft().getCol() > Math.min(srcRight, tgtRight)
                && b.getUpperLeft().getCol() < Math.max(srcLeft, tgtLeft)) {
              blocked = true;
              break;
            }
          }

          // Case 2: vertical alignment, check y-axis overlap
          if (sameColBand && b.getUpperLeft().getCol() <= srcRight
              && b.getLowerRight().getCol() >= srcLeft) {
            if (b.getUpperLeft().getRow() > Math.min(srcBottom, tgtBottom)
                && b.getUpperLeft().getRow() < Math.max(srcTop, tgtTop)) {
              blocked = true;
              break;
            }
          }
        }

        if (!blocked) {
          visible.add(i);
        }
      }
    }

    return visible;
  }
}
