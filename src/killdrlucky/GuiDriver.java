package killdrlucky;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Driver for GUI version of Kill Doctor Lucky.
 */
public class GuiDriver {
  
  /**
   * Main entry point for the GUI version of the game.
   * 
   * @param args command-line arguments:
   *             args[0] = path to world file (default: "res/mansion.txt")
   *             args[1] = maximum number of turns (default: 50)
   * @throws Exception if there is an error parsing the world file
   */
  public static void main(String[] args) throws IOException {
    String worldFile = args.length > 0 ? args[0] : "res/mansion.txt";
    int maxTurns = args.length > 1 ? Integer.parseInt(args[1]) : 50;
    
    // Parse world ONCE for initial view
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of(worldFile));
    
    // Create initial empty model
    VisibilityStrategy strategy = new AxisAlignedVisibility();
    GameModelApi model = new World(data, strategy);
    
    SwingUtilities.invokeLater(() -> {
      // Create view - starts with welcome screen
      GameView view = new GameView(model);
      
      // Create controller
      GuiController controller = new GuiController(model, view, maxTurns, worldFile);
      
      // Set up callback to add players and start game
      view.setOnStartNewGame(() -> {
        try {
          WorldParser newParser = new WorldParser();
          WorldParser.WorldData newData = newParser.parse(Path.of(worldFile));
          VisibilityStrategy newStrategy = new AxisAlignedVisibility();
          GameModelApi newModel = new World(newData, newStrategy);
          
          // Update controller's model reference
          controller.setModel(newModel);
          
          // Update view's model reference
          view.setModel(newModel);
          view.clearMessages();
          
          // Add players via dialog
          String numPlayersStr = JOptionPane.showInputDialog("How many players? (1-10)");
          if (numPlayersStr == null) {
            return;
          }

          int numPlayers;
          try {
            numPlayers = Integer.parseInt(numPlayersStr.trim());
            
            if (numPlayers < 1 || numPlayers > 10) {
              JOptionPane.showMessageDialog(null, 
                  "Error: Number of players must be between 1 and 10!\nYou entered: " + numPlayers, 
                  "Invalid Input", 
                  JOptionPane.ERROR_MESSAGE);
              return;  
            }
            
          } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                "Invalid input! Please enter a number between 1 and 10.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
          }

          
          for (int i = 0; i < numPlayers; i++) {
            String name = JOptionPane.showInputDialog("Player " + (i + 1) + " name:");
            if (name == null || name.trim().isEmpty()) {
              continue;
            }
            
            String spaceStr = JOptionPane.showInputDialog(
                "Starting space (0-" + (newModel.getSpaces().size() - 1) + "):");
            if (spaceStr == null) {
              continue;
            }
            int space = Integer.parseInt(spaceStr);
            
            int isAi = JOptionPane.showConfirmDialog(null, 
                "Is this a computer player?", "Player Type", JOptionPane.YES_NO_OPTION);
            
            newModel.addPlayer(name, space, isAi == JOptionPane.YES_OPTION, 5);
          }
          
          // Switch to game screen
          view.showGameScreen();
          view.addMessage("🎮 Game Started!");
          view.addMessage("Players: " + newModel.getPlayers().size());
          view.addMessage("Click space to move | P: Pick up | L: Look | A: Attack | M: Move pet");
          view.addMessage("Click on a player icon to view their info");
          
          controller.updateView();
          
        } catch (IllegalArgumentException | IOException e) {
          JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
          e.printStackTrace();
        }
      });
    });
  }
}