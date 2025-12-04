package killdrlucky;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * Interface for game view.
 * Isolates view from controller implementation.
 */
public interface GameViewInterface {
  
  /**
   * Set mouse click listener for the game panel.
   * 
   * @param listener the MouseListener to handle click events
   */
  void setClickListener(MouseListener listener);
  
  /**
   * Set keyboard listener for game actions.
   * 
   * @param listener the KeyListener to handle key press events
   */
  void setKeyListener(KeyListener listener);
  
  /**
   * Update status bar text at the top of the view.
   * 
   * @param text the status text to display (e.g., current turn, player info)
   */
  void updateStatus(String text);
  
  /**
   * Add a message to the message area at the bottom of the view.
   * 
   * @param text the message text to append
   */
  void addMessage(String text);
  
  /**
   * Refresh the view to reflect current game state.
   * Triggers repaint of all visual components.
   */
  void refresh();
  
  /**
   * Show an input dialog and get user input.
   * 
   * @param message the prompt message to display to the user
   * @return the user's input text, or null if cancelled
   */
  String promptInput(String message);
  
  /**
   * Show a simple message dialog.
   * 
   * @param message the message to display to the user
   */
  void showMessage(String message);
}