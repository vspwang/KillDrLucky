package killdrlucky;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.Timer;

/**
 * GUI controller for Kill Doctor Lucky game.
 */
public class GuiController implements ControllerInterface {
  private final GameModelApi model;
  private final GameViewInterface view;
  private final int maxTurns;
  private int currentTurn = 0;
  
  /**
   * Creates GUI controller.
   */
  public GuiController(GameModelApi model, GameViewInterface view, int maxTurns) {
    this.model = model;
    this.view = view;
    this.maxTurns = maxTurns;
    
    setupListeners();
    updateView();
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
  }
  
  @Override
  public void handleClick(int x, int y) {
    GameState state = model.getGameState();
    
    if (state.isCurrentPlayerAi) {
      view.showMessage("It's the computer's turn!");
      return;
    }
    
    // Get clicked space
    WorldPanel panel = (WorldPanel) ((JScrollPane) 
        ((GameView) view).getContentPane().getComponent(1)).getViewport().getView();
    int spaceIdx = panel.getSpaceAt(x, y);
    
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
    key = java.lang.Character.toLowerCase(key);
    GameState state = model.getGameState();
    
    if (state.isCurrentPlayerAi) {
      return;
    }
    
    switch (key) {
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
    if (state.gameOver || currentTurn >= maxTurns) {
      String msg = state.gameOver 
          ? "🏆 Game Over! Target eliminated!" 
          : "⏰ Game Over! Maximum turns reached.";
      view.showMessage(msg);
      return true;
    }
    return false;
  }
  
  @Override
  public void updateView() {
    GameState state = model.getGameState();
    
    view.updateStatus(String.format(
        "Turn %d/%d | Player: %s (%s) | Location: %s | Target Health: %d",
        currentTurn, maxTurns, 
        state.currentPlayerName,
        state.isCurrentPlayerAi ? "AI" : "Human",
        state.currentPlayerLocation,
        state.targetHealth));
    
    view.refresh();
  }
}