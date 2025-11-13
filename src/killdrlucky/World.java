package killdrlucky;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
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

  /**
   * Constructs a World object from parsed data.
   *
   * @param data               the parsed world data
   * @param visibilityStrategy the strategy to use for visibility
   */
  public World(WorldParser.WorldData data, VisibilityStrategy visibilityStrategy) {
    if (data == null || visibilityStrategy == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }
    this.name = data.worldName;
    this.rows = data.rows;
    this.cols = data.cols;
    this.spaces = new ArrayList<>(data.rooms);
    this.items = new ArrayList<>(data.items);
    this.target = data.target;
    this.visibilityStrategy = visibilityStrategy;
    this.neighbors = computeNeighbors();
    this.players = new ArrayList<>();
    this.random = new Random();
    this.gameOver = false;
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
    return visibilityStrategy.visibleFrom(idx, spaces);
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
      sb.append(visible.stream()
          .map(v -> spaces.get(v).getName())
          .collect(Collectors.joining(", ")));
    }
    sb.append("\n");

    // Add neighbors section
    sb.append("Neighbors: ");
    List<Integer> adj = neighborsOf(idx);
    if (adj == null || adj.isEmpty()) {
      sb.append("none");
    } else {
      sb.append(adj.stream()
          .map(n -> String.format("%s [%d]", spaces.get(n).getName(), n))
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

  public List<Iplayer> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  @Override
  public void addPlayer(String name, int startSpaceIndex,
                       boolean computerControlled, int capacity) {
    Objects.requireNonNull(name, "Player name cannot be null");
    
    if (name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be empty");
    }
    
    if (startSpaceIndex < 0 || startSpaceIndex >= spaces.size()) {
      throw new IllegalArgumentException(
          String.format("Invalid starting index: %d. Valid range: [0, %d]",
                       startSpaceIndex, spaces.size() - 1));
    }
    
    if (capacity < 0) {
      throw new IllegalArgumentException(
          "Capacity must be non-negative, got: " + capacity);
    }
    
    // Check for duplicate names
    for (Iplayer p : players) {
      if (p.getName().equalsIgnoreCase(name)) {
        throw new IllegalArgumentException(
            "Player with name '" + name + "' already exists");
      }
    }
    
    Iplayer player = computerControlled
        ? new ComputerPlayer(name, startSpaceIndex, capacity)
        : new Player(name, startSpaceIndex, capacity);
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
        return String.format("Invalid destination index: %d. Valid range: [0, %d]",
                            destIdx, neighborIndices.size() - 1);
      }
      int targetSpaceIdx = neighborIndices.get(destIdx);
      player.setCurrentSpaceIndex(targetSpaceIdx);
      return String.format("%s moved to %s", 
                          playerName, 
                          spaces.get(targetSpaceIdx).getName());
    } catch (NumberFormatException e) {
      // Try to match by space name
      for (int neighborIdx : neighborIndices) {
        if (spaces.get(neighborIdx).getName().equalsIgnoreCase(destination)) {
          player.setCurrentSpaceIndex(neighborIdx);
          return String.format("%s moved to %s", 
                              playerName, 
                              spaces.get(neighborIdx).getName());
        }
      }
      return String.format("No neighboring space named '%s'. Available: %s",
                          destination,
                          neighborIndices.stream()
                              .map(i -> spaces.get(i).getName())
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
      return String.format("%s cannot carry more items (capacity: %d/%d)",
                          playerName, 
                          player.getCurrentCapacity(),
                          player.getMaxCapacity());
    }

    // Find the item in the current space
    Item targetItem = null;
    for (Item it : items) {
      if (it.getName().equalsIgnoreCase(itemName) 
          && it.getRoomIndex() == currentIdx) {
        targetItem = it;
        break;
      }
    }

    if (targetItem == null) {
      return String.format("Item '%s' not found in %s.", 
                          itemName,
                          spaces.get(currentIdx).getName());
    }

    // Transfer item from world to player
    player.addItem(targetItem);
    targetItem.setRoomIndex(-1); // Mark as picked up
    
    return String.format("%s picked up %s (damage: %d). Carrying: %d/%d",
                        playerName, 
                        targetItem.getName(),
                        targetItem.getDamage(),
                        player.getCurrentCapacity(),
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
    List<Item> itemsHere = items.stream()
        .filter(it -> it.getRoomIndex() == currentIdx)
        .collect(Collectors.toList());

    sb.append("Items here: ");
    if (itemsHere.isEmpty()) {
      sb.append("none");
    } else {
      sb.append(itemsHere.stream()
          .map(it -> String.format("%s (%d)", it.getName(), it.getDamage()))
          .collect(Collectors.joining(", ")));
    }
    sb.append("\n");

    // Other players in current space
    List<String> otherPlayersHere = players.stream()
        .filter(p -> p.getCurrentSpaceIndex() == currentIdx 
                     && !p.getName().equals(playerName))
        .map(Iplayer::getName)
        .collect(Collectors.toList());

    if (!otherPlayersHere.isEmpty()) {
      sb.append("Other players here: ")
        .append(String.join(", ", otherPlayersHere))
          .append("\n");
    }

    // Target in current space
    if (target.getCurrentSpaceIndex() == currentIdx) {
      sb.append(String.format("%s is here! (Health: %d)\n",
                             target.getName(), target.getHealth()));
    }

    // Visible spaces
    Set<Integer> visible = visibleFrom(currentIdx);
    sb.append("\nYou can see into:\n");
    if (visible.isEmpty()) {
      sb.append("  (no visible spaces)\n");
    } else {
      for (int idx : visible) {
        Space space = spaces.get(idx);
        sb.append(String.format("  • %s", space.getName()));
        
        // Players in visible space
        List<String> playersInSpace = players.stream()
            .filter(p -> p.getCurrentSpaceIndex() == idx)
            .map(Iplayer::getName)
            .collect(Collectors.toList());
        
        if (!playersInSpace.isEmpty()) {
          sb.append(" [").append(String.join(", ", playersInSpace)).append("]");
        }
        
        // Target in visible space
        if (target.getCurrentSpaceIndex() == idx) {
          sb.append(String.format(" [%s is there!]", target.getName()));
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
    sb.append(String.format("Type: %s\n", 
                           player.isComputerControlled() ? "Computer (AI)" : "Human"));
    sb.append(String.format("Location: %s [index: %d]\n", 
                           currentSpace.getName(), 
                           player.getCurrentSpaceIndex()));
    sb.append(String.format("Inventory: %d/%d items\n",
                           player.getCurrentCapacity(),
                           player.getMaxCapacity()));
    
    if (player.getItems().isEmpty()) {
      sb.append("  (empty)\n");
    } else {
      for (Item item : player.getItems()) {
        sb.append(String.format("  • %s (damage: %d)\n", 
                               item.getName(), 
                               item.getDamage()));
      }
    }
    
    return sb.toString().trim();
  }

  @Override
  public String autoAction(String playerName) {
    Iplayer player = findPlayer(playerName);
    
    if (!player.isComputerControlled()) {
      throw new IllegalArgumentException(
          playerName + " is not a computer-controlled player");
    }

    int currentIdx = player.getCurrentSpaceIndex();
    List<Integer> neighborIndices = neighborsOf(currentIdx);

    // 50% chance to move if there are neighbors
    if (random.nextDouble() < 0.5 && !neighborIndices.isEmpty()) {
      int randomNeighborIdx = neighborIndices.get(random.nextInt(neighborIndices.size()));
      player.setCurrentSpaceIndex(randomNeighborIdx);
      return String.format("[AI] %s moved to %s", 
                          playerName, 
                          spaces.get(randomNeighborIdx).getName());
    }

    // Try to pick up an item if at capacity allows
    if (player.canCarryMore()) {
      for (Item item : items) {
        if (item.getRoomIndex() == currentIdx) {
          player.addItem(item);
          item.setRoomIndex(-1);
          return String.format("[AI] %s picked up %s (damage: %d)", 
                              playerName, 
                              item.getName(),
                              item.getDamage());
        }
      }
    }

    // Default: look around
    return String.format("[AI] %s looked around %s but found nothing interesting.",
                        playerName,
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
        boolean horizontalTouch = 
            (r1.getLowerRight().getRow() >= r2.getUpperLeft().getRow()
             && r1.getUpperLeft().getRow() <= r2.getLowerRight().getRow())
            && (r1.getLowerRight().getCol() + 1 == r2.getUpperLeft().getCol()
                || r2.getLowerRight().getCol() + 1 == r1.getUpperLeft().getCol());

        // Check for vertical adjacency
        boolean verticalTouch = 
            (r1.getLowerRight().getCol() >= r2.getUpperLeft().getCol()
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

  private Iplayer findPlayer(String name) {
    return players.stream()
        .filter(p -> p.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Player not found: " + name));
  }

}
