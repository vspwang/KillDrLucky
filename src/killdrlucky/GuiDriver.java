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

    // Parse world
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of(worldFile));

    // Create model
    VisibilityStrategy strategy = new AxisAlignedVisibility();
    GameModelApi model = new World(data, strategy);

    SwingUtilities.invokeLater(() -> {
      // Add players via dialog
      String numPlayersStr = JOptionPane.showInputDialog("How many players? (1-10)");
      if (numPlayersStr == null) {
        System.exit(0);
      }

      int numPlayers = Integer.parseInt(numPlayersStr);

      for (int i = 0; i < numPlayers; i++) {
        String name = JOptionPane.showInputDialog("Player " + (i + 1) + " name:");
        if (name == null || name.trim().isEmpty()) {
          continue;
        }

        String spaceStr = JOptionPane
            .showInputDialog("Starting space (0-" + (model.getSpaces().size() - 1) + "):");
        int space = Integer.parseInt(spaceStr);

        int isAi = JOptionPane.showConfirmDialog(null, "Is this a computer player?", "Player Type",
            JOptionPane.YES_NO_OPTION);

        model.addPlayer(name, space, isAi == JOptionPane.YES_OPTION, 5);
      }

      // Create view and controller
      GameView view = new GameView(model);
      new GuiController(model, view, maxTurns);

      view.addMessage("🎮 Game Started!");
      view.addMessage("Click on a neighboring space to move");
      view.addMessage("Press P to pick up, L to look around, A to attack, M to move pet");
    });
  }
}