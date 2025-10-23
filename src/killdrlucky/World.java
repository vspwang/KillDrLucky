package killdrlucky;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
  public String describeSpace(int idx) {
    Space r = getSpace(idx);
    StringBuilder sb = new StringBuilder();

    sb.append("Room: ").append(r.getName()).append("\n");

    // Retrieve items in this room by filtering from global item list
    List<Item> stuff = new ArrayList<>();
    for (Item it : items) {
      if (it.getRoomIndex() == idx) {
        stuff.add(it);
      }
    }

    sb.append("Items: ");
    if (stuff.isEmpty()) {
      sb.append("none");
    } else {
      for (Item it : stuff) {
        sb.append(it.getName()).append("(").append(it.getDamage()).append(") ");
      }
    }

    sb.append("\nVisible from here: ");
    Set<Integer> visible = visibleFrom(idx);
    if (visible.isEmpty()) {
      sb.append("none");
    } else {
      for (int v : visible) {
        sb.append(spaces.get(v).getName()).append(" ");
      }
    }
    
    // Add neighbors section
    sb.append("\nNeighbors: ");
    List<Integer> adj = neighborsOf(idx);
    if (adj == null || adj.isEmpty()) {
      sb.append("none");
    } else {
      for (int n : adj) {
        sb.append(spaces.get(n).getName()).append(" ");
      }
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
  public void moveTargetNext() {
    int next = (target.getCurrentSpaceIndex() + 1) % spaces.size();
    target.setCurrentSpaceIndex(next);
  }

  @Override
  public AttackStatus canAttack(int playerId, int itemId) {
    if (playerId < 0 || playerId >= players.size()) {
      throw new IllegalArgumentException("Invalid player index.");
    }
    if (target == null || !target.isAlive()) {
      return AttackStatus.TARGET_ALREADY_DEAD;
    }

    Iplayer p = players.get(playerId);
    if (p.getCurrentSpaceIndex() != target.getCurrentSpaceIndex()) {
      return AttackStatus.NOT_SAME_SPACE;
    }

    // find weapon by itemId (index in global items list)
    if (itemId < 0 || itemId >= items.size()) {
      return AttackStatus.NO_SUCH_ITEM;
    }
    Item weapon = items.get(itemId);
    if (!p.getItems().contains(weapon)) {
      return AttackStatus.NO_SUCH_ITEM;
    }

    // check visibility (is player being seen by others)
    boolean seen = isSeenByOthers(p);
    if (seen) {
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

    Iplayer p = players.get(playerId);
    Item weapon = items.get(itemId);
    target.takeDamage(weapon.getDamage());

    // optional: remove the weapon after use
    p.removeItem(weapon);
    return AttackStatus.SUCCESS;
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

  public List<Iplayer> getPlayers() {
    return Collections.unmodifiableList(players);
  }

  @Override
  public void addPlayer(String name, int startIdx, boolean isAi, int capacity) {
    Objects.requireNonNull(name, "Player name cannot be null");
    if (startIdx < 0 || startIdx >= spaces.size()) {
      throw new IllegalArgumentException("Invalid starting index");
    }
    for (Iplayer p : players) {
      if (p.getName().equalsIgnoreCase(name)) {
        throw new IllegalArgumentException("Duplicate player name");
      }
    }
    Iplayer player = isAi
        ? new ComputerPlayer(name, startIdx, new ArrayList<Item>())
        : new Player(name, startIdx, new ArrayList<Item>());
    players.add(player);
    
  }

  @Override
  public String movePlayer(String name, String destination) {
    Iplayer p = findPlayer(name);
    if (p == null) {
      throw new IllegalArgumentException("Player not found: " + name);
    }
    return p.move(destination, this);
  }

  @Override
  public String pickUpItem(String name, String itemName) {
    Iplayer p = findPlayer(name);
    return p.pickUp(itemName, this);
  }

  @Override
  public String lookAround(String name) {
    Iplayer p = findPlayer(name);
    return p.lookAround(this);
  }

  @Override
  public String describePlayer(String name) {
    Iplayer p = findPlayer(name);
    Space current = spaces.get(p.getCurrentSpaceIndex());
    StringBuilder sb = new StringBuilder();
    sb.append("Player: ").append(p.getName())
      .append(" (").append(p.isComputerControlled() ? "AI" : "Human").append(")\n")
      .append("Current Space: ").append(current.getName()).append("\nItems: ");
    if (p.getItems().isEmpty()) {
      sb.append("None");
    } else {
      sb.append(p.getItems().stream().map(Item::getName).collect(Collectors.joining(", ")));
    }
    return sb.toString();
  }

  @Override
  public void moveTarget() {
    moveTargetNext();
    
  }

  @Override
  public String autoAction(String name) {
    Iplayer p = findPlayer(name);
    if (p == null) {
      return "AI player not found: " + name;
    }

    int currentIdx = p.getCurrentSpaceIndex();
    List<Integer> neighbors = neighborsOf(currentIdx);

    // 50% chance to move, 50% to pick up (if available)
    if (Math.random() < 0.5 && !neighbors.isEmpty()) {
      int randomIdx = neighbors.get((int) (Math.random() * neighbors.size()));
      String roomName = spaces.get(randomIdx).getName();
      p.setCurrentSpaceIndex(randomIdx);
      return name + " (AI) moved to " + roomName + ".";
    } else {
      // Try picking up an item from current room
      for (Item it : items) {
        if (it.getRoomIndex() == currentIdx) {
          // Remove from room and add to player
          p.addItem(it);
          it.setRoomIndex(-1); // remove from world
          return name + " (AI) picked up " + it.getName() + ".";
        }
      }
      return name + " (AI) looked around but found nothing.";
    }
  }


  @Override
  public boolean isGameOver() {
    return gameOver;
  }

  @Override
  public void endGame() {
    gameOver = true;
    
  }

  @Override
  public void saveWorldImage(String filename) throws IOException {
    // TODO Auto-generated method stub
    ;
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

        boolean horizontalTouch = (r1.getLowerRight().getRow() >= r2.getUpperLeft().getRow()
            && r1.getUpperLeft().getRow() <= r2.getLowerRight().getRow())
            && (r1.getLowerRight().getCol() + 1 == r2.getUpperLeft().getCol()
                || r2.getLowerRight().getCol() + 1 == r1.getUpperLeft().getCol());

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

  private boolean isSeenByOthers(Iplayer p2) {
    int attackerRoom = p2.getCurrentSpaceIndex();
    Set<Integer> visibleRooms = visibleFrom(attackerRoom);
    for (Iplayer p : players) {
      if (p == p2) {
        continue;
      }
      if (visibleRooms.contains(p.getCurrentSpaceIndex())) {
        return true;
      }
    }
    return false;
  }
  
  private Iplayer findPlayer(String name) {
    return players.stream()
        .filter(p -> p.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Player not found: " + name));
  }

  @Override
  public List<Item> getItems() {
    return Collections.unmodifiableList(items);
  }

}
