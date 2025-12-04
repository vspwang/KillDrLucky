package killdrlucky;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

/**
 * Concrete implementation of the WorldModel interface. Represents the full game
 * state for Kill Dr Lucky.
 */
public class World implements WorldModel, GameModelApi {

  private final String name;
  private final int rows;
  private final int cols;
  private final List<Space> spaces;
  private final List<Item> items;
  private final List<Iplayer> players;
  private final Target target;
  private final VisibilityStrategy visibilityStrategy;
  private final Map<Integer, List<Integer>> neighbors;
  private boolean gameOver;
  private final Random random;
  private final Pet pet;

  private final Stack<Integer> dfsStack;
  private final Set<Integer> dfsVisited;
  private String winnerName = "";

  private int currentPlayerIndex = 0;

  /**
   * Constructs a World object from parsed data.
   *
   * @param data               the parsed world data
   * @param visibilityStrategyParam the strategy to use for visibility
   */
  public World(WorldParser.WorldData data, VisibilityStrategy visibilityStrategyParam) {
    if (data == null || visibilityStrategyParam == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.name = data.worldName;
    this.rows = data.rows;
    this.cols = data.cols;
    this.spaces = new ArrayList<>(data.rooms);
    this.items = new ArrayList<>(data.items);
    this.target = data.target;
    this.visibilityStrategy = visibilityStrategyParam;
    this.neighbors = computeNeighbors();
    this.players = new ArrayList<>();
    this.random = new Random();
    this.gameOver = false;
    this.pet = data.pet;

    // Initialize DFS traversal for wandering pet
    this.dfsStack = new Stack<>();
    this.dfsVisited = new HashSet<>();
    initializeDfsTraversal();
  }

  // ---------- Core Queries ----------

  @Override
  public String getWorldName() {
    return name;
  }

  @Override
  public int getRows() {
    return rows;
  }

  @Override
  public int getCols() {
    return cols;
  }

  @Override
  public List<Space> getSpaces() {
    return Collections.unmodifiableList(spaces);
  }

  @Override
  public Space getSpace(int idx) {
    if (idx < 0 || idx >= spaces.size()) {
      throw new IllegalArgumentException("Invalid space index: " + idx);
    }
    return spaces.get(idx);
  }

  @Override
  public Target getTarget() {
    return target;
  }

  @Override
  public List<Integer> neighborsOf(int idx) {
    if (!neighbors.containsKey(idx)) {
      throw new IllegalArgumentException("Invalid space index: " + idx);
    }
    return Collections.unmodifiableList(neighbors.get(idx));
  }

  @Override
  public Set<Integer> visibleFrom(int idx) {
    Set<Integer> visible = visibilityStrategy.visibleFrom(idx, spaces);
    int petSpace = pet.getCurrentSpaceIndex();
    visible.remove(petSpace);
    return visible;
  }

  @Override
  public List<Item> getItems() {
    return Collections.unmodifiableList(items);
  }

  @Override
  public String describeSpace(int idx) {
    Space r = getSpace(idx);
    StringBuilder sb = new StringBuilder();

    sb.append("╔══════════════════════════════════════╗\n");
    sb.append(String.format("║ Room: %-30s ║\n", r.getName()));
    sb.append(String.format("║ Index: %-29d ║\n", idx));
    sb.append("╚══════════════════════════════════════╝\n\n");

    // Add players in the space information
    List<String> playersHere = players.stream().filter(p -> p.getCurrentSpaceIndex() == idx)
        .map(p -> String.format("%s (%s)", p.getName(), p.isComputerControlled() ? "AI" : "Human"))
        .collect(Collectors.toList());

    sb.append("Players: ");
    if (playersHere.isEmpty()) {
      sb.append("none");
    } else {
      sb.append(String.join(", ", playersHere));
    }
    sb.append("\n");

    // Target character
    if (target.getCurrentSpaceIndex() == idx) {
      sb.append("\nTarget (").append(target.getName()).append(") is here! Health: ")
          .append(target.getHealth());
    }
    sb.append("\n");

    // Pet
    if (pet.getCurrentSpaceIndex() == idx) {
      sb.append(String.format("Pet: %s is here!\n", pet.getName()));
    }

    // Items in this room
    List<Item> stuff = items.stream().filter(it -> it.getRoomIndex() == idx)
        .collect(Collectors.toList());

    sb.append("Items: ");
    if (stuff.isEmpty()) {
      sb.append("none");
    } else {
      for (Item it : stuff) {
        sb.append(it.getName()).append("(").append(it.getDamage()).append(") ");
      }
    }
    sb.append("\n");

    // Add visibility information
    sb.append("️Visible spaces: ");
    Set<Integer> visible = visibleFrom(idx);
    if (visible.isEmpty()) {
      sb.append("none");
    } else {
      sb.append(
          visible.stream().map(v -> spaces.get(v).getName()).collect(Collectors.joining(", ")));
    }
    sb.append("\n");

    // Add neighbors section
    sb.append("Neighbors: ");
    List<Integer> adj = neighborsOf(idx);
    if (adj == null || adj.isEmpty()) {
      sb.append("none");
    } else {
      sb.append(adj.stream().map(n -> String.format("%s [%d]", spaces.get(n).getName(), n))
          .collect(Collectors.joining(", ")));
    }

    return sb.toString().trim();
  }

  @Override
  public String describeSpace(String spaceName) {
    for (int i = 0; i < spaces.size(); i++) {
      if (spaces.get(i).getName().equalsIgnoreCase(spaceName)) {
        return describeSpace(i);
      }
    }
    throw new IllegalArgumentException("Space not found: " + spaceName);
  }

  /**
   * Gets the pet character.
   *
   * @return the pet
   */
  @Override
  public Pet getPet() {
    return pet;
  }

  /**
   * Initializes the DFS traversal starting from the pet's current location.
   * 
   * <p>
   * This sets up the stack and visited set for depth-first traversal of all
   * spaces in the world.
   */
  private void initializeDfsTraversal() {
    dfsStack.clear();
    dfsVisited.clear();

    // Start DFS from pet's current location
    int startIdx = pet.getCurrentSpaceIndex();
    dfsStack.push(startIdx);
    dfsVisited.add(startIdx);
  }

  /**
   * Moves the pet to the next space following a DFS traversal pattern.
   * 
   * <p>
   * This implements the extra credit wandering pet feature. The pet visits all
   * spaces in the world following a depth-first traversal order, then restarts
   * from space 0.
   * 
   * <p>
   * DFS algorithm:
   * <ol>
   * <li>Pop current space from stack</li>
   * <li>Get all unvisited neighbors</li>
   * <li>Push unvisited neighbors onto stack (in reverse order for
   * consistency)</li>
   * <li>Move pet to top of stack</li>
   * <li>If stack is empty, restart DFS from space 0</li>
   * </ol>
   */
  public void movePetDfs() {
    // If stack is empty or all spaces visited, restart DFS
    if (dfsStack.isEmpty() || dfsVisited.size() >= spaces.size()) {
      // Reset and start from space 0
      dfsStack.clear();
      dfsVisited.clear();
      dfsStack.push(0);
      dfsVisited.add(0);
      pet.setCurrentSpaceIndex(0);
      return;
    }

    // Get current position
    int currentIdx = dfsStack.peek();

    // Get neighbors of current space
    List<Integer> neighborIndices = neighborsOf(currentIdx);

    // Find unvisited neighbors
    List<Integer> unvisited = new ArrayList<>();
    for (int neighborIdx : neighborIndices) {
      if (!dfsVisited.contains(neighborIdx)) {
        unvisited.add(neighborIdx);
      }
    }

    if (!unvisited.isEmpty()) {
      // Push unvisited neighbors onto stack (in reverse order for consistent
      // traversal)
      for (int i = unvisited.size() - 1; i >= 0; i--) {
        int neighborIdx = unvisited.get(i);
        dfsStack.push(neighborIdx);
        dfsVisited.add(neighborIdx);
      }

      // Move pet to the top of the stack
      int nextIdx = dfsStack.peek();
      pet.setCurrentSpaceIndex(nextIdx);
    } else {
      // No unvisited neighbors, backtrack
      dfsStack.pop();

      if (!dfsStack.isEmpty()) {
        int nextIdx = dfsStack.peek();
        pet.setCurrentSpaceIndex(nextIdx);
      } else {
        // Traversal complete, restart
        initializeDfsTraversal();
        pet.setCurrentSpaceIndex(0);
      }
    }
  }

  /**
   * Attempts to attack the target character with an item or by poking.
   *
   * @param playerName the name of the attacking player
   * @param itemName   the name of the item to use, or null/empty to poke in the
   *                   eye
   * @return a message describing the result of the attack
   * @throws IllegalArgumentException if player is not found
   */
  @Override
  public String attackTarget(String playerName, String itemName) {
    Iplayer player = findPlayer(playerName);

    // Check if player is in the same space as target
    if (player.getCurrentSpaceIndex() != target.getCurrentSpaceIndex()) {
      return "Attack failed: You must be in the same room as the target!";
    }

    // Check if player can be seen by others
    if (isSeenByOthers(player)) {
      return "Attack failed: You were seen by another player! The attack was stopped.";
    }

    int damage;
    Item weaponUsed = null;

    // Determine attack type
    if (itemName == null || itemName.trim().isEmpty()) {
      // Poke in the eye
      damage = 1;
    } else {
      // Find weapon in player's inventory
      weaponUsed = player.getItems().stream()
          .filter(it -> it.getName().equalsIgnoreCase(itemName.trim())).findFirst().orElse(null);

      if (weaponUsed == null) {
        return "Attack failed: You don't have that item: " + itemName;
      }

      damage = weaponUsed.getDamage();
    }

    // Apply damage to target
    target.takeDamage(damage);

    // Remove weapon as evidence (if weapon was used)
    if (weaponUsed != null) {
      player.removeItem(weaponUsed);
    }

    // Check if target is dead
    if (!target.isAlive()) {
      gameOver = true;
      winnerName = playerName;
      return String.format("%s WINS! \n%s killed %s with %s for %d damage!\nThe target is dead!",
          playerName, playerName, target.getName(),
          weaponUsed != null ? weaponUsed.getName() : "a poke in the eye", damage);
    }

    // Target still alive
    return String.format("⚔️ %s attacked %s with %s for %d damage!\n Target health remaining: %d",
        playerName, target.getName(),
        weaponUsed != null ? weaponUsed.getName() : "a poke in the eye", damage,
        target.getHealth());
  }

  /**
   * Moves the pet to the specified space.
   *
   * @param spaceName the name of the destination space
   * @return a message describing the result
   * @throws IllegalArgumentException if space is not found
   */
  @Override
  public String movePet(String spaceName) {
    if (spaceName == null || spaceName.trim().isEmpty()) {
      throw new IllegalArgumentException("Space name cannot be null or empty");
    }

    // Find space by name
    for (int i = 0; i < spaces.size(); i++) {
      if (spaces.get(i).getName().equalsIgnoreCase(spaceName.trim())) {
        int oldIdx = pet.getCurrentSpaceIndex();
        String oldSpaceName = spaces.get(oldIdx).getName();
        pet.setCurrentSpaceIndex(i);
        initializeDfsTraversal();
        return String.format("🐾 Moved %s from %s to %s", pet.getName(), oldSpaceName, spaceName);
      }
    }

    throw new IllegalArgumentException("Space not found: " + spaceName);
  }

  // ---------- Game Mechanics ----------
  @Override
  public void moveTarget() {
    moveTargetNext();
  }

  @Override
  public AttackStatus canAttack(int playerId, int itemId) {
    if (playerId < 0 || playerId >= players.size()) {
      throw new IllegalArgumentException("Invalid player index: " + playerId);
    }

    if (!target.isAlive()) {
      return AttackStatus.TARGET_ALREADY_DEAD;
    }

    Iplayer player = players.get(playerId);

    if (player.getCurrentSpaceIndex() != target.getCurrentSpaceIndex()) {
      return AttackStatus.NOT_SAME_SPACE;
    }

    // Find weapon by itemId
    if (itemId < 0 || itemId >= items.size()) {
      return AttackStatus.NO_SUCH_ITEM;
    }

    Item weapon = items.get(itemId);
    if (!player.getItems().contains(weapon)) {
      return AttackStatus.NO_SUCH_ITEM;
    }

    // Check if seen by others
    if (isSeenByOthers(player)) {
      return AttackStatus.SEEN_BY_OTHERS;
    }

    return AttackStatus.SUCCESS;
  }

  @Override
  public AttackStatus attack(int playerId, int itemId) {
    AttackStatus status = canAttack(playerId, itemId);
    if (status != AttackStatus.SUCCESS) {
      return status;
    }

    Iplayer player = players.get(playerId);
    Item weapon = items.get(itemId);
    target.takeDamage(weapon.getDamage());
    player.removeItem(weapon);

    if (!target.isAlive()) {
      gameOver = true;
    }

    return AttackStatus.SUCCESS;
  }

  /**
   * Returns a defensive copy of list of players.
   *
   * @return defensive copy of list of players
   */
  public List<Iplayer> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  @Override
  public void addPlayer(String nameParam, int startSpaceIndex, boolean computerControlled,
      int capacity) {
    Objects.requireNonNull(nameParam, "Player name cannot be null");

    if (nameParam.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be empty");
    }

    if (players.size() >= 10) {
      throw new IllegalArgumentException(
          "Maximum number of players (10) reached. Cannot add more players.");
    }
    // ==========================================

    if (startSpaceIndex < 0 || startSpaceIndex >= spaces.size()) {
      throw new IllegalArgumentException(String.format(
          "Invalid starting index: %d. Valid range: [0, %d]", startSpaceIndex, spaces.size() - 1));
    }

    if (capacity < 0) {
      throw new IllegalArgumentException("Capacity must be non-negative, got: " + capacity);
    }

    // Check for duplicate names
    for (Iplayer p : players) {
      if (p.getName().equalsIgnoreCase(nameParam)) {
        throw new IllegalArgumentException("Player with name '" + nameParam + "' already exists");
      }
    }

    Iplayer player = computerControlled ? new ComputerPlayer(nameParam, startSpaceIndex, capacity)
        : new Player(nameParam, startSpaceIndex, capacity);
    players.add(player);
  }

  @Override
  public String movePlayer(String playerName, String destination) {
    Iplayer player = findPlayer(playerName);
    int currentIdx = player.getCurrentSpaceIndex();
    List<Integer> neighborIndices = neighborsOf(currentIdx);

    if (neighborIndices == null || neighborIndices.isEmpty()) {
      return String.format("%s cannot move - no available neighbors.", playerName);
    }

    // Try to parse destination as index first
    try {
      int destIdx = Integer.parseInt(destination.trim());
      if (destIdx < 0 || destIdx >= neighborIndices.size()) {
        return String.format("Invalid destination index: %d. Valid range: [0, %d]", destIdx,
            neighborIndices.size() - 1);
      }
      int targetSpaceIdx = neighborIndices.get(destIdx);
      player.setCurrentSpaceIndex(targetSpaceIdx);
      return String.format("%s moved to %s", playerName, spaces.get(targetSpaceIdx).getName());
    } catch (NumberFormatException e) {
      // Try to match by space name
      for (int neighborIdx : neighborIndices) {
        if (spaces.get(neighborIdx).getName().equalsIgnoreCase(destination)) {
          player.setCurrentSpaceIndex(neighborIdx);
          return String.format("%s moved to %s", playerName, spaces.get(neighborIdx).getName());
        }
      }
      return String.format("No neighboring space named '%s'. Available: %s", destination,
          neighborIndices.stream().map(i -> spaces.get(i).getName())
              .collect(Collectors.joining(", ")));
    }
  }

  @Override
  public String pickUpItem(String playerName, String itemName) {
    if (itemName == null || itemName.trim().isEmpty()) {
      return "Invalid item name.";
    }

    Iplayer player = findPlayer(playerName);
    int currentIdx = player.getCurrentSpaceIndex();

    // Check capacity first
    if (!player.canCarryMore()) {
      return String.format("%s cannot carry more items (capacity: %d/%d)", playerName,
          player.getCurrentCapacity(), player.getMaxCapacity());
    }

    // Find the item in the current space
    Item targetItem = null;
    for (Item it : items) {
      if (it.getName().equalsIgnoreCase(itemName) && it.getRoomIndex() == currentIdx) {
        targetItem = it;
        break;
      }
    }

    if (targetItem == null) {
      return String.format("Item '%s' not found in %s.", itemName,
          spaces.get(currentIdx).getName());
    }

    // Transfer item from world to player
    player.addItem(targetItem);
    targetItem.setRoomIndex(-1); // Mark as picked up

    return String.format("%s picked up %s (damage: %d). Carrying: %d/%d", playerName,
        targetItem.getName(), targetItem.getDamage(), player.getCurrentCapacity(),
        player.getMaxCapacity());
  }

  @Override
  public String lookAround(String playerName) {
    Iplayer player = findPlayer(playerName);
    int currentIdx = player.getCurrentSpaceIndex();
    Space currentSpace = spaces.get(currentIdx);

    StringBuilder sb = new StringBuilder();
    sb.append("═══════════════════════════════════\n");
    sb.append(String.format("Looking around from: %s\n", currentSpace.getName()));
    sb.append("═══════════════════════════════════\n\n");

    // Items in current space
    List<Item> itemsHere = items.stream().filter(it -> it.getRoomIndex() == currentIdx)
        .collect(Collectors.toList());

    sb.append("Items here: ");
    if (itemsHere.isEmpty()) {
      sb.append("none");
    } else {
      sb.append(itemsHere.stream().map(it -> String.format("%s (%d)", it.getName(), it.getDamage()))
          .collect(Collectors.joining(", ")));
    }
    sb.append("\n");

    // Other players in current space
    List<String> otherPlayersHere = players.stream()
        .filter(p -> p.getCurrentSpaceIndex() == currentIdx && !p.getName().equals(playerName))
        .map(Iplayer::getName).collect(Collectors.toList());

    if (!otherPlayersHere.isEmpty()) {
      sb.append("Other players here: ").append(String.join(", ", otherPlayersHere)).append("\n");
    }

    // Target in current space
    if (target.getCurrentSpaceIndex() == currentIdx) {
      sb.append(String.format("%s is here! (Health: %d)\n", target.getName(), target.getHealth()));
    }

    // Pet in current space
    if (pet.getCurrentSpaceIndex() == currentIdx) {
      sb.append(String.format("  🐾 %s is here!\n", pet.getName()));
    }

    // NEW: Neighboring spaces with detailed information
    sb.append("\nNeighboring Spaces:\n");
    List<Integer> neighborsList = neighborsOf(currentIdx);
    Set<Integer> visible = visibleFrom(currentIdx);

    if (neighborsList.isEmpty()) {
      sb.append("  (no neighbors)\n");
    } else {
      for (int neighborIdx : neighborsList) {
        Space neighborSpace = spaces.get(neighborIdx);
        sb.append(String.format("  • %s [%d]", neighborSpace.getName(), neighborIdx));

        // Check if we can see into this neighbor
        if (pet.getCurrentSpaceIndex() == neighborIdx) {
          // Pet blocks view
          sb.append(" - Cannot see inside (pet is blocking view)");
        } else if (visible.contains(neighborIdx)) {
          // Can see inside
          sb.append(":");

          // Items in neighbor
          List<String> neighborItems = items.stream().filter(it -> it.getRoomIndex() == neighborIdx)
              .map(Item::getName).collect(Collectors.toList());

          // Players in neighbor
          List<String> neighborPlayers = players.stream()
              .filter(p -> p.getCurrentSpaceIndex() == neighborIdx).map(Iplayer::getName)
              .collect(Collectors.toList());

          // Target in neighbor
          boolean targetInNeighbor = target.getCurrentSpaceIndex() == neighborIdx;

          List<String> contents = new ArrayList<>();
          if (!neighborItems.isEmpty()) {
            contents.add("Items: " + String.join(", ", neighborItems));
          }
          if (!neighborPlayers.isEmpty()) {
            contents.add("Players: " + String.join(", ", neighborPlayers));
          }
          if (targetInNeighbor) {
            contents.add("Target: " + target.getName());
          }

          if (contents.isEmpty()) {
            sb.append(" (empty)");
          } else {
            sb.append("\n    ").append(String.join(", ", contents));
          }
        } else {
          sb.append(" - Cannot see inside");
        }

        sb.append("\n");
      }
    }

    return sb.toString().trim();
  }

  @Override
  public String describePlayer(String playerName) {
    Iplayer player = findPlayer(playerName);
    Space currentSpace = spaces.get(player.getCurrentSpaceIndex());

    StringBuilder sb = new StringBuilder();
    sb.append("╔════════════════════════════════════╗\n");
    sb.append(String.format("║ Player: %-26s ║\n", player.getName()));
    sb.append("╚════════════════════════════════════╝\n");
    sb.append(
        String.format("Type: %s\n", player.isComputerControlled() ? "Computer (AI)" : "Human"));
    sb.append(String.format("Location: %s [index: %d]\n", currentSpace.getName(),
        player.getCurrentSpaceIndex()));
    sb.append(String.format("Inventory: %d/%d items\n", player.getCurrentCapacity(),
        player.getMaxCapacity()));

    if (player.getItems().isEmpty()) {
      sb.append("  (empty)\n");
    } else {
      for (Item item : player.getItems()) {
        sb.append(String.format("  • %s (damage: %d)\n", item.getName(), item.getDamage()));
      }
    }

    return sb.toString().trim();
  }

  @Override
  public String autoAction(String playerName) {
    Iplayer player = findPlayer(playerName);

    if (!player.isComputerControlled()) {
      throw new IllegalArgumentException(playerName + " is not a computer-controlled player");
    }

    int currentIdx = player.getCurrentSpaceIndex();
    List<Integer> neighborIndices = neighborsOf(currentIdx);

    // PRIORITY 1: Attack if possible (same room as target and not seen)
    if (player.getCurrentSpaceIndex() == target.getCurrentSpaceIndex() && !isSeenByOthers(player)) {

      // Find highest damage weapon
      Item bestWeapon = player.getItems().stream()
          .max((a, b) -> Integer.compare(a.getDamage(), b.getDamage())).orElse(null);

      if (bestWeapon != null) {
        return attackTarget(playerName, bestWeapon.getName());
      } else {
        // Poke in the eye
        return attackTarget(playerName, null);
      }
    }

    // PRIORITY 2: Move or pickup (existing logic)
    // 50% chance to move if there are neighbors
    if (random.nextDouble() < 0.5 && !neighborIndices.isEmpty()) {
      int randomNeighborIdx = neighborIndices.get(random.nextInt(neighborIndices.size()));
      player.setCurrentSpaceIndex(randomNeighborIdx);
      return String.format("[AI] %s moved to %s", playerName,
          spaces.get(randomNeighborIdx).getName());
    }

    // Try to pick up an item if capacity allows
    if (player.canCarryMore()) {
      for (Item item : items) {
        if (item.getRoomIndex() == currentIdx) {
          player.addItem(item);
          item.setRoomIndex(-1);
          return String.format("[AI] %s picked up %s (damage: %d)", playerName, item.getName(),
              item.getDamage());
        }
      }
    }

    // Default: look around
    return String.format("[AI] %s looked around %s but found nothing interesting.", playerName,
        spaces.get(currentIdx).getName());
  }

  @Override
  public boolean isGameOver() {
    return gameOver || !target.isAlive();
  }

  @Override
  public void endGame() {
    gameOver = true;

  }

  // ---------- Graphics ----------

  @Override
  public BufferedImage renderBufferedImage(int cellSize) {
    int width = cols * cellSize;
    int height = rows * cellSize;
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);

    g.setColor(Color.LIGHT_GRAY);
    for (Space r : spaces) {
      Rect rect = r.getArea();
      int x = rect.getUpperLeft().getCol() * cellSize;
      int y = rect.getUpperLeft().getRow() * cellSize;
      int w = rect.width() * cellSize;
      int h = rect.height() * cellSize;
      g.setColor(Color.getHSBColor((float) Math.random(), 0.5f, 0.9f));
      g.fillRect(x, y, w, h);
      g.setColor(Color.BLACK);
      g.drawRect(x, y, w, h);
      g.drawString(r.getName(), x + 2, y + 12);
      g.drawString(String.valueOf(r.getIndex()), x + 2, y + 24);

    }

    g.dispose();
    return img;
  }

  @Override
  public void saveWorldImage(String filename) throws IOException {
    BufferedImage img = renderBufferedImage(30);
    File outputFile = new File(filename);
    ImageIO.write(img, "png", outputFile);
  }

  @Override
  public ActionResult executeAction(String playerName, String actionType, String parameter) {
    try {
      String message;
      boolean isTurn = true;

      switch (actionType.toLowerCase()) {
        case "move":
          message = movePlayer(playerName, parameter);
          break;
        case "pickup":
          message = pickUpItem(playerName, parameter);
          break;
        case "look":
          message = lookAround(playerName);
          break;
        case "attack":
          message = attackTarget(playerName, parameter);
          break;
        case "movepet":
          message = movePet(parameter);
          break;
        default:
          return new ActionResult(false, "Unknown action: " + actionType, false);
      }

      return new ActionResult(true, message, isTurn);

    } catch (IllegalArgumentException | IllegalStateException e) {
      return new ActionResult(false, "Error: " + e.getMessage(), false);
    }
  }

  @Override
  public GameState getGameState() {
    if (players.isEmpty()) {
      return new GameState("", false, 0, "", 0, target.getHealth(), target.getCurrentSpaceIndex(),
          pet.getCurrentSpaceIndex(), gameOver, winnerName // ← 改这里
      );
    }

    Iplayer current = players.get(currentPlayerIndex);
    Space currentSpace = spaces.get(current.getCurrentSpaceIndex());

    return new GameState(current.getName(), current.isComputerControlled(),
        current.getCurrentSpaceIndex(), currentSpace.getName(), currentPlayerIndex,
        target.getHealth(), target.getCurrentSpaceIndex(), pet.getCurrentSpaceIndex(), gameOver,
        winnerName);
  }

  @Override
  public void advanceTurn() {
    if (!players.isEmpty()) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
  }

  // ---------- Internal Utilities ----------

  private Map<Integer, List<Integer>> computeNeighbors() {
    Map<Integer, List<Integer>> map = new HashMap<>();

    for (int i = 0; i < spaces.size(); i++) {
      Space s1 = spaces.get(i);
      Rect r1 = s1.getArea();
      List<Integer> adj = new ArrayList<>();

      for (int j = 0; j < spaces.size(); j++) {
        if (i == j) {
          continue;
        }
        Rect r2 = spaces.get(j).getArea();

        // Check for horizontal adjacency
        boolean horizontalTouch = (r1.getLowerRight().getRow() >= r2.getUpperLeft().getRow()
            && r1.getUpperLeft().getRow() <= r2.getLowerRight().getRow())
            && (r1.getLowerRight().getCol() + 1 == r2.getUpperLeft().getCol()
                || r2.getLowerRight().getCol() + 1 == r1.getUpperLeft().getCol());

        // Check for vertical adjacency
        boolean verticalTouch = (r1.getLowerRight().getCol() >= r2.getUpperLeft().getCol()
            && r1.getUpperLeft().getCol() <= r2.getLowerRight().getCol())
            && (r1.getLowerRight().getRow() + 1 == r2.getUpperLeft().getRow()
                || r2.getLowerRight().getRow() + 1 == r1.getUpperLeft().getRow());

        if (horizontalTouch || verticalTouch) {
          adj.add(j);
        }
      }
      map.put(i, adj);
    }
    return map;
  }

  /**
   * Checks if a player is seen by other players.
   *
   * @param player the player to check
   * @return true if the player is seen by others, false otherwise
   */
  private boolean isSeenByOthers(Iplayer player) {
    int playerSpace = player.getCurrentSpaceIndex();
    Set<Integer> visibleSpaces = visibleFrom(playerSpace);

    for (Iplayer other : players) {
      if (other == player) {
        continue;
      }
      // Check if other player is in a visible space
      if (visibleSpaces.contains(other.getCurrentSpaceIndex())) {
        return true;
      }
      // Also check if other player can see this space
      Set<Integer> otherVisible = visibleFrom(other.getCurrentSpaceIndex());
      if (otherVisible.contains(playerSpace)) {
        return true;
      }
    }

    return false;
  }

  private void moveTargetNext() {
    int next = (target.getCurrentSpaceIndex() + 1) % spaces.size();
    target.setCurrentSpaceIndex(next);
  }

  private Iplayer findPlayer(String nameParam) {
    return players.stream().filter(p -> p.getName().equalsIgnoreCase(nameParam)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Player not found: " + nameParam));
  }

}
