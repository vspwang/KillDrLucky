package killdrlucky;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mock implementation of GameModelApi for testing.
 */
public class MockGameModel implements GameModelApi {
  private List<String> methodCalls = new ArrayList<>();
  private GameState gameState;
  private ActionResult executeActionResult;
  private List<Integer> neighbors;
  private Space mockSpace;
  private List<Item> items;
  private String describePlayerResult;
  private Target target;

  /**
   * Constructs a new MockGameModel with default test values.
   * Initializes the mock with a default game state (player "Alice" in "Kitchen"),
   * a successful action result, an empty neighbor list, an empty item list,
   * and a target "Dr. Lucky" with 50 health at space 0.
   * This provides a consistent starting state for unit tests.
   */
  public MockGameModel() {
    gameState = new GameState("Alice", false, 0, "Kitchen", 0, 50, 5, 2, false, "");
    executeActionResult = new ActionResult(true, "Success", true);
    neighbors = new ArrayList<>();
    items = new ArrayList<>();

    target = new Target("Dr. Lucky", 50, 0);
  }

  // Setters for test setup
  public void setGameState(GameState state) {
    this.gameState = state;
  }

  public void setExecuteActionResult(ActionResult result) {
    this.executeActionResult = result;
  }

  public void setNeighbors(List<Integer> neighborsParam) {
    this.neighbors = neighborsParam;
  }

  public void setMockSpace(Space space) {
    this.mockSpace = space;
  }

  public void setItems(List<Item> itemsParam) {
    this.items = itemsParam;
  }

  public void setDescribePlayerResult(String result) {
    this.describePlayerResult = result;
  }

  // Verify methods were called
  public boolean wasMethodCalled(String methodName) {
    return methodCalls.contains(methodName);
  }

  public int getMethodCallCount(String methodName) {
    return (int) methodCalls.stream().filter(m -> m.equals(methodName)).count();
  }

  public void resetMethodCalls() {
    methodCalls.clear();
  }

  // Implement interface methods
  @Override
  public ActionResult executeAction(String playerName, String actionType, String parameter) {
    methodCalls.add("executeAction");
    return executeActionResult;
  }

  @Override
  public GameState getGameState() {
    methodCalls.add("getGameState");
    return gameState;
  }

  @Override
  public void advanceTurn() {
    methodCalls.add("advanceTurn");
  }

  @Override
  public void moveTarget() {
    methodCalls.add("moveTarget");
  }

  @Override
  public void movePetDfs() {
    methodCalls.add("movePetDfs");
  }

  @Override
  public List<Integer> neighborsOf(int idx) {
    methodCalls.add("neighborsOf");
    return neighbors;
  }

  @Override
  public Space getSpace(int idx) {
    methodCalls.add("getSpace");
    return mockSpace;
  }

  @Override
  public List<Item> getItems() {
    methodCalls.add("getItems");
    return items;
  }

  @Override
  public String describePlayer(String name) {
    methodCalls.add("describePlayer");
    return describePlayerResult != null ? describePlayerResult : "Player: " + name;
  }

  @Override
  public Target getTarget() {
    return target;
  }

  @Override
  public String autoAction(String playerName) {
    methodCalls.add("autoAction");
    return "AI action";
  }

  // Other required methods - minimal implementations
  @Override
  public String getWorldName() {
    return "Test World";
  }

  @Override
  public int getRows() {
    return 10;
  }

  @Override
  public int getCols() {
    return 10;
  }

  @Override
  public List<Space> getSpaces() {
    return new ArrayList<>();
  }

  @Override
  public Set<Integer> visibleFrom(int idx) {
    return new HashSet<>();
  }

  @Override
  public String describeSpace(int idx) {
    return "Space";
  }
  
  @Override
  public String describeSpace(String spaceName) {
    return "Space description";
  }

  @Override
  public Pet getPet() {
    return new Pet("Pet", 0);
  }

  @Override
  public void addPlayer(String name, int startSpaceIndex, boolean computerControlled,
      int capacity) {
    methodCalls.add("addPlayer");
  }

  @Override
  public List<Iplayer> getPlayers() {
    return new ArrayList<>();
  }

  @Override
  public String movePlayer(String name, String direction) {
    return "Moved";
  }

  @Override
  public String pickUpItem(String name, String itemName) {
    return "Picked up";
  }

  @Override
  public String lookAround(String name) {
    return "Looking around";
  }

  @Override
  public boolean isGameOver() {
    return gameState.gameOver;
  }

  @Override
  public void endGame() {
    methodCalls.add("endGame");
  }

  @Override
  public void saveWorldImage(String filename) throws IOException {
  }

  @Override
  public BufferedImage renderBufferedImage(int cellSize) {
    return null;
  }

  @Override
  public String attackTarget(String playerName, String itemName) {
    return "Attack";
  }

  @Override
  public String movePet(String spaceName) {
    return "Pet moved";
  }
}