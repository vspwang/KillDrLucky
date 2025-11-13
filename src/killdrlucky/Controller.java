package killdrlucky;

import java.io.IOException;

/**
 * A controller defines the entry point for running the Kill Doctor Lucky game.
 */
public interface Controller {

  /**
   * Starts and manages the game loop.
   *
   * @throws IOException if there is IO error
   */
  void playGame() throws IOException;
}
