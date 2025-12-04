package killdrlucky;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.Timer;

/**
 * GUI controller for Kill Doctor Lucky game.
 */
public class GuiController implements ControllerInterface {
  private GameModelApi model;
  private GameView view;
  private final int maxTurns;
  private int currentTurn = 0;
  private String worldFilePath;

  /**
   * Creates GUI controller.
   * 
   * @param modelParam    the game model containing game logic and state
   * @param viewParam     the game view for displaying UI and capturing user input
   * @param maxTurnsParam the maximum number of turns allowed in the game
   * @param worldFile     the path to the world file
   */
  public GuiController(GameModelApi modelParam, GameView viewParam, int maxTurnsParam,
      String worldFile) {
    this.model = modelParam;
    this.view = viewParam;
    this.maxTurns = maxTurnsParam;
    this.worldFilePath = worldFile;

    setupListeners();
    updateView();
  }

  /**
   * Update the model reference.
   * 
   * @param newModel the new model
   */
  public void setModel(GameModelApi newModel) {
    this.model = newModel;
    this.currentTurn = 0; // Reset turn counter
  }

  private void setupListeners() {
    // Mouse clicks
    view.setClickListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        handleClick(e.getX(), e.getY());
      }
    });

    // Keyboard
    view.setKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        handleKey(e.getKeyChar());
      }
    });

    // Menu callbacks
    view.setOnStartNewGame(() -> handleNewGame());
    view.setOnRestartGame(() -> handleRestartGame());
  }

  private void handleNewGame() {
    String newWorldFile = view.promptInput("Enter world file path (e.g., res/mansion.txt):");
    if (newWorldFile != null && !newWorldFile.trim().isEmpty()) {
      worldFilePath = newWorldFile;
      restartWithNewWorld();
    }
  }

  private void handleRestartGame() {
    int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
        "Restart game with current world?", "Restart", javax.swing.JOptionPane.YES_NO_OPTION);

    if (confirm == javax.swing.JOptionPane.YES_OPTION) {
      restartWithNewWorld();
    }
  }

  private void restartWithNewWorld() {
    try {
      // Parse world
      WorldParser parser = new WorldParser();
      WorldParser.WorldData data = parser.parse(java.nio.file.Path.of(worldFilePath));

      VisibilityStrategy strategy = new AxisAlignedVisibility();
      model = new World(data, strategy);

      // Update view with new model
      view.setModel(model);
      view.clearMessages();

      // Add players
      String numPlayersStr = view.promptInput("How many players? (1-10)");
      if (numPlayersStr == null) {
        view.showWelcomeScreen();
        return;
      }

      int numPlayers = Integer.parseInt(numPlayersStr);

      for (int i = 0; i < numPlayers; i++) {
        String name = view.promptInput("Player " + (i + 1) + " name:");
        if (name == null || name.trim().isEmpty()) {
          continue;
        }

        String spaceStr = view
            .promptInput("Starting space (0-" + (model.getSpaces().size() - 1) + "):");
        if (spaceStr == null) {
          continue;
        }
        int space = Integer.parseInt(spaceStr);

        int isAi = javax.swing.JOptionPane.showConfirmDialog(null, "Is this a computer player?",
            "Player Type", javax.swing.JOptionPane.YES_NO_OPTION);

        model.addPlayer(name, space, isAi == javax.swing.JOptionPane.YES_OPTION, 5);
      }

      // Reset turn counter
      currentTurn = 0;

      // Show game screen
      view.showGameScreen();
      view.addMessage("🎮 New Game Started!");
      view.addMessage("Players: " + model.getPlayers().size());
      view.addMessage("Click space to move | P: Pick up | L: Look | A: Attack | M: Move pet");
      view.addMessage("Click on a player icon to view their info");

      updateView();

      // If first player is AI, start
      GameState state = model.getGameState();
      if (state.isCurrentPlayerAi) {
        Timer timer = new Timer(1000, e -> playComputer());
        timer.setRepeats(false);
        timer.start();
      }

    } catch (IOException e) {
      view.showMessage("Error loading world: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void handleClick(int x, int y) {
    GameState state = model.getGameState();

    if (state.isCurrentPlayerAi) {
      view.showMessage("It's the computer's turn!");
      return;
    }

    // Check if clicked on a player
    String clickedPlayer = view.getWorldPanel().getPlayerAt(x, y);
    if (clickedPlayer != null) {
      String description = model.describePlayer(clickedPlayer);
      view.addMessage("=== Player Info ===");
      view.addMessage(description);
      return;
    }

    // Check if clicked on a space
    int spaceIdx = view.getWorldPanel().getSpaceAt(x, y);

    if (spaceIdx < 0) {
      return;
    }

    // Check if neighbor
    List<Integer> neighbors = model.neighborsOf(state.currentPlayerSpace);
    if (!neighbors.contains(spaceIdx)) {
      view.addMessage("Not a neighbor! Current neighbors: " + neighbors);
      return;
    }

    String spaceName = model.getSpace(spaceIdx).getName();
    executeAction("move", spaceName);
  }

  @Override
  public void handleKey(char key) {
    GameState state = model.getGameState();

    if (state.isCurrentPlayerAi) {
      return;
    }

    switch (java.lang.Character.toLowerCase(key)) {
      case 'p':
        handlePickup();
        break;
      case 'l':
        executeAction("look", "");
        break;
      case 'a':
        handleAttack();
        break;
      case 'm':
        handleMovePet();
        break;
      default:
        // Ignore other keys
        break;
    }
  }

  private void handlePickup() {
    GameState state = model.getGameState();
    List<Item> items = model.getItems();

    StringBuilder sb = new StringBuilder("Items in this room:\n");
    int count = 0;
    for (Item item : items) {
      if (item.getRoomIndex() == state.currentPlayerSpace) {
        sb.append("- ").append(item.getName()).append(" (").append(item.getDamage())
            .append(" damage)\n");
        count++;
      }
    }

    if (count == 0) {
      view.addMessage("No items here to pick up!");
      return;
    }

    String itemName = view.promptInput(sb.toString() + "\nEnter item name:");
    if (itemName != null && !itemName.trim().isEmpty()) {
      executeAction("pickup", itemName);
    }
  }

  private void handleAttack() {
    GameState state = model.getGameState();

    if (state.currentPlayerSpace != state.targetSpace) {
      view.showMessage("Target is not in the same room!");
      return;
    }

    String weapon = view.promptInput("Enter weapon name (or leave empty to poke in the eye):");
    executeAction("attack", weapon == null ? "" : weapon);
  }

  private void handleMovePet() {
    String spaceName = view.promptInput("Enter space name to move pet:");
    if (spaceName != null && !spaceName.trim().isEmpty()) {
      executeAction("movepet", spaceName);
    }
  }

  @Override
  public void executeAction(String action, String param) {
    GameState state = model.getGameState();

    ActionResult result = model.executeAction(state.currentPlayerName, action, param);

    view.addMessage(result.message);

    if (result.success && result.isTurnAction) {
      model.moveTarget();
      model.movePetDfs();
      model.advanceTurn();
      currentTurn++;

      if (checkGameOver()) {
        return;
      }

      updateView();

      // Auto-play computer turn
      GameState newState = model.getGameState();
      if (newState.isCurrentPlayerAi) {
        Timer timer = new Timer(1000, e -> playComputer());
        timer.setRepeats(false);
        timer.start();
      }
    }
  }

  private void playComputer() {
    GameState state = model.getGameState();

    String result = model.autoAction(state.currentPlayerName);
    view.addMessage("[AI] " + result);

    model.moveTarget();
    model.movePetDfs();
    model.advanceTurn();
    currentTurn++;

    if (checkGameOver()) {
      return;
    }

    updateView();

    // Chain computer turns
    GameState newState = model.getGameState();
    if (newState.isCurrentPlayerAi) {
      Timer timer = new Timer(1000, e -> playComputer());
      timer.setRepeats(false);
      timer.start();
    }
  }

  private boolean checkGameOver() {
    GameState state = model.getGameState();

    if (state.gameOver) {
      // Target was killed - use winner from state
      String winnerName = state.winner.isEmpty() ? state.currentPlayerName : state.winner;

      String msg = String.format(
          "GAME OVER!\n\n" + "Winner: %s\n" + "Target '%s' has been eliminated!\n"
              + "Total turns: %d/%d",
          winnerName, model.getTarget().getName(), currentTurn, maxTurns);

      view.showMessage(msg);
      view.addMessage("=== GAME OVER ===");
      view.addMessage("Winner: " + winnerName); // ← 使用 winnerName
      view.addMessage(
          "Target '" + model.getTarget().getName() + "' eliminated in " + currentTurn + " turns!");
      view.showWelcomeScreen();
      return true;
    }

    if (currentTurn >= maxTurns) {
      // Max turns reached - Target escaped
      String msg = String.format(
          "GAME OVER!\n\n" + "Maximum turns reached (%d/%d)\n" + "Target '%s' ESCAPED!\n"
              + "Remaining health: %d\n\n" + "No winner - Target survived!",
          currentTurn, maxTurns, model.getTarget().getName(), state.targetHealth);

      view.showMessage(msg);
      view.addMessage("=== GAME OVER ===");
      view.addMessage("Maximum turns reached (" + currentTurn + "/" + maxTurns + ")");
      view.addMessage("Target '" + model.getTarget().getName() + "' ESCAPED with "
          + state.targetHealth + " health remaining!");
      view.addMessage("All players FAILED!");
      view.showWelcomeScreen();
      return true;
    }

    return false;
  }

  @Override
  public void updateView() {
    GameState state = model.getGameState();

    view.updateStatus(String.format(
        "Turn %d/%d | Player: %s (%s) | Location: %s | Target Health: %d", currentTurn, maxTurns,
        state.currentPlayerName, state.isCurrentPlayerAi ? "AI" : "Human",
        state.currentPlayerLocation, state.targetHealth));

    view.refresh();
  }
}