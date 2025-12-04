package killdrlucky;

/**
 * Interface for world rendering panel.
 */
public interface WorldPanelInterface {
  
  /**
   * Get space at pixel coordinates.
   * 
   * @param x the coordinate x
   * @param y the coodinate y
   * @return space index or -1 if none
   */
  int getSpaceAt(int x, int y);
  
  /**
   * Refresh panel display.
   */
  void refresh();
}