package killdrlucky;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * Concrete implementation of the WorldModel interface. Represents the full game
 * state for Kill Dr Lucky.
 */
public class World implements WorldModel {

  private final String name;
  private final int rows;
  private final int cols;
  private final List<Room> spaces;
  private final List<Item> items;
  private final List<Player> players;
  private final Target target;
  private final VisibilityStrategy visibilityStrategy;
  private final Map<Integer, List<Integer>> neighbors;

  /**
   * Constructs a World object from parsed data.
   *
   * @param data               the parsed world data
   * @param visibilityStrategy the strategy to use for visibility
   */
  public World(WorldParser.WorldData data, VisibilityStrategy visibilityStrategy) {
    this.name = Objects.requireNonNull(data.worldName);
    this.rows = data.rows;
    this.cols = data.cols;
    this.spaces = new ArrayList<>(data.rooms);
    this.items = new ArrayList<>(data.items);
    this.target = Objects.requireNonNull(data.target);
    this.players = new ArrayList<>(); // initially empty (can be added later)
    this.visibilityStrategy = Objects.requireNonNull(visibilityStrategy);
    this.neighbors = computeNeighbors();
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
  public List<Room> getSpaces() {
    return Collections.unmodifiableList(spaces);
  }

  @Override
  public Room getSpace(int idx) {
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
    Room r = getSpace(idx);
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
    return sb.toString().trim();
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

    Player p = players.get(playerId);
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

    Player p = players.get(playerId);
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
    for (Room r : spaces) {
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
    }

    g.dispose();
    return img;
  }

  // ---------- Internal Utilities ----------

  private Map<Integer, List<Integer>> computeNeighbors() {
    Map<Integer, List<Integer>> map = new HashMap<>();
    for (int i = 0; i < spaces.size(); i++) {
      Room s1 = spaces.get(i);
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

  private boolean isSeenByOthers(Player attacker) {
    int attackerRoom = attacker.getCurrentSpaceIndex();
    Set<Integer> visibleRooms = visibleFrom(attackerRoom);
    for (Player p : players) {
      if (p == attacker) {
        continue;
      }
      if (visibleRooms.contains(p.getCurrentSpaceIndex())) {
        return true;
      }
    }
    return false;
  }

  /**.
   * Check null and add a player to the World
   *
   * @param p The player added
   */
  public void addPlayer(Player p) {
    if (p == null) {
      throw new IllegalArgumentException("Player cannot be null.");
    }
    players.add(p);
  }

  public List<Player> getPlayers() {
    return Collections.unmodifiableList(players);
  }
}
