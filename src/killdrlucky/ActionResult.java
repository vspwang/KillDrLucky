package killdrlucky;

/**
 * Represents the result of executing a game action.
 */
public class ActionResult {
  public final boolean success;
  public final String message;
  public final boolean isTurnAction;
  
  /**
   * Creates an ActionResult.
   * 
   * @param successParam whether the action succeeded
   * @param messageParam user-friendly result message
   * @param isTurnActionParam whether this action consumes a turn
   */
  public ActionResult(boolean successParam, String messageParam, boolean isTurnActionParam) {
    this.success = successParam;
    this.message = messageParam;
    this.isTurnAction = isTurnActionParam;
  }
  
  /**
   * Gets success status.
   * 
   * @return true if action succeeded
   */
  public boolean isSuccess() {
    return success;
  }
  
  /**
   * Gets result message.
   * 
   * @return user-friendly message
   */
  public String getMessage() {
    return message;
  }
  
  /**
   * Checks if this action consumes a turn.
   * 
   * @return true if this is a turn action
   */
  public boolean isTurnAction() {
    return isTurnAction;
  }
}