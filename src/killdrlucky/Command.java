package killdrlucky;

/**
 * Represents a command that can be executed in the game.
 */
public interface Command {
  /**
   * Executes the command.
   *
   * @return the result message of the command execution
   */
  String execute();

  /**
   * Returns whether this command counts as a turn.
   *
   * @return true if this command ends the current player's turn
   */
  boolean isTurnAction();
}