package killdrlucky;

/**
 * Interface for game controller.
 */
public interface ControllerInterface {
  
  /**
   * Handle mouse click at coordinates.
   * 
   * @param x the coordinate x
   * @param y the coordinate y
   */
  void handleClick(int x, int y);
  
  /**
   * Handle key press.
   * 
   * @param key the pressed key
   */
  void handleKey(char key);
  
  /**
   * Execute a game action.
   * 
   * @param actionType the type of the player action
   * @param parameter the game parameter
   */
  void executeAction(String actionType, String parameter);
  
  /**
   * Update view to reflect current state.
   */
  void updateView();
}