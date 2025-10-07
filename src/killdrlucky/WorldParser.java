package killdrlucky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Parser for the Kill Dr Lucky world file format:
 * Line 1: rows cols worldName... 
 * Line 2: targetHealth targetName... (target starts at space 0)
 * Line 3: spaceCount 
 * Next N lines (spaces): ulRow ulCol lrRow lrCol roomName... 
 * Next line: itemCount 
 * Next M lines (items): roomIndex damage itemName...
 * Notes: - Names may contain spaces (everything after the numeric fields on
 * each line). - Rect coordinates are inclusive (width/height add +1 logic). -
 * Rooms must be within bounds and must not overlap (shared edges allowed). -
 * Item roomIndex must be valid.
 */
public class WorldParser {

  /** Immutable aggregate of parsed data. */
  public static final class WorldData {
    public final String worldName;
    public final int rows;
    public final int cols;
    public final List<Room> rooms;
    public final List<Item> items;
    public final Target target;
    
    /**.
     *
     * @param worldName Name of the World
     * @param rows Numbers of Row
     * @param cols Numbers of Col
     * @param rooms List of Room
     * @param items List of Item
     * @param target Target for this World
     */
    
    public WorldData(String worldName, int rows, int cols, List<Room> rooms, List<Item> items,
        Target target) {
      this.worldName = Objects.requireNonNull(worldName);
      this.rows = rows;
      this.cols = cols;
      this.rooms = Collections.unmodifiableList(new ArrayList<>(rooms));
      this.items = Collections.unmodifiableList(new ArrayList<>(items));
      this.target = Objects.requireNonNull(target);
    }
  }

  /**. 
   * Parse from a file path. 
   *
   * @param path file path
   * @return parse(Reader)
   */
  public WorldData parse(Path path) throws IOException {
    try (BufferedReader br = Files.newBufferedReader(path)) {
      return parse(br);
    }
  }

  /**.
   * Parse from any Reader. 
   *
   * @param reader input stream
   * @return parsed data into a WorldDara object
   */
  public WorldData parse(Reader reader) throws IOException {
    BufferedReader br = new BufferedReader(reader);

    // --- Line 1: rows cols worldName...
    String line1 = readNonEmpty(br, 1);
    ParsedHead h1 = parseHeadWithInts(line1, 2, 1);
    int rows = h1.ints[0];
    int cols = h1.ints[1];
    final String worldName = mustNonBlank(h1.rest, 1, "world name");

    // --- Line 2: targetHealth targetName...
    String line2 = readNonEmpty(br, 2);
    ParsedHead h2 = parseHeadWithInts(line2, 1, 2);
    int targetHealth = h2.ints[0];
    String targetName = mustNonBlank(h2.rest, 2, "target name");
    final Target target = new Target(targetName, targetHealth, /* start */ 0);

    // --- Line 3: spaceCount
    int lineNo = 3;
    String line3 = readNonEmpty(br, lineNo);
    int spaceCount = parseSingleInt(line3, lineNo, "space count", 1, Integer.MAX_VALUE);

    // --- Next N lines: spaces
    List<Room> rooms = new ArrayList<>(spaceCount);
    for (int i = 0; i < spaceCount; i++) {
      String ln = readNonEmpty(br, ++lineNo);
      ParsedHead hs = parseHeadWithInts(ln, 4, lineNo);
      int ulr = hs.ints[0]; 
      int ulc = hs.ints[1];
      int lrr = hs.ints[2];
      int lrc = hs.ints[3];
      String roomName = mustNonBlank(hs.rest, lineNo, "room name");
      Room room = new Room(i, roomName, new Rect(new Point(ulr, ulc), new Point(lrr, lrc)),
          List.of());
      rooms.add(room);
    }

    // --- Next line: itemCount
    String lineItemsCount = readNonEmpty(br, ++lineNo);
    int itemCount = parseSingleInt(lineItemsCount, lineNo, "item count", 0, Integer.MAX_VALUE);

    // --- Next M lines: items
    List<Item> items = new ArrayList<>(itemCount);
    for (int i = 0; i < itemCount; i++) {
      String ln = readNonEmpty(br, ++lineNo);
      ParsedHead hi = parseHeadWithInts(ln, 2, lineNo);
      int roomIndex = hi.ints[0];
      int damage = hi.ints[1];
      String itemName = mustNonBlank(hi.rest, lineNo, "item name");
      items.add(new Weapon(itemName, damage, roomIndex));
    }

    // --- Validations
    if (rows <= 0 || cols <= 0) {
      throw parseError(1, "rows/cols must be positive.");
    }
    validateRoomsInBounds(rooms, rows, cols);
    validateNoOverlap(rooms);
    validateItems(rooms, items);
    validateTargetStart(rooms, target);

    return new WorldData(worldName, rows, cols, rooms, items, target);
  }

  // ----------------- helpers -----------------

  private static final class ParsedHead {
    final int[] ints;
    final String rest;

    ParsedHead(int[] ints, String rest) {
      this.ints = ints;
      this.rest = rest;
    }
  }

  /** Reads a non-empty (non-blank) line; throws if null or blank. */
  private static String readNonEmpty(BufferedReader br, int lineNo) throws IOException {
    String line = br.readLine();
    if (line == null) {
      throw parseError(lineNo, "Unexpected end of file.");
    }
    line = line.strip();
    if (line.isEmpty()) {
      throw parseError(lineNo, "Empty line not allowed.");
    }
    return line;
  }

  /**
   * Parses the beginning of a line as {@code count} integers separated by spaces,
   * returns the parsed ints and the remaining substring (trimmed) as
   * {@code rest}.
   */
  private static ParsedHead parseHeadWithInts(String line, int count, int lineNo) {
    String[] parts = line.split("\\s+");
    if (parts.length < count) {
      throw parseError(lineNo, "Expected at least " + count + " integers at line start.");
    }
    int[] arr = new int[count];
    for (int i = 0; i < count; i++) {
      try {
        arr[i] = Integer.parseInt(parts[i]);
      } catch (NumberFormatException e) {
        throw parseError(lineNo, "Invalid integer: '" + parts[i] + "'");
      }
    }
    // Build rest: everything after the first `count` tokens in the original line
    int pos = 0;
    int tokensSeen = 0;
    // advance pos across count tokens
    for (int i = 0; i < line.length() && tokensSeen < count; i++) {
      if (!java.lang.Character.isWhitespace(line.charAt(i))) {
        while (i < line.length() && !java.lang.Character.isWhitespace(line.charAt(i))) {
          i++;
        }
        tokensSeen++;
        pos = i;
      }
    }
    String rest = line.substring(pos).trim();
    return new ParsedHead(arr, rest);
  }

  private static String mustNonBlank(String s, int lineNo, String what) {
    if (s == null || s.isBlank()) {
      throw parseError(lineNo, "Missing " + what + ".");
    }
    return s;
  }

  private static int parseSingleInt(String s, int lineNo, String what, int min, int max) {
    try {
      int v = Integer.parseInt(s.trim());
      if (v < min || v > max) {
        throw new NumberFormatException();
      }
      return v;
    } catch (NumberFormatException e) {
      throw parseError(lineNo, "Invalid " + what + ": " + s);
    }
  }

  private static void validateRoomsInBounds(List<Room> rooms, int rows, int cols) {
    for (Room r : rooms) {
      Rect rect = r.getArea();
      if (rect.getUpperLeft().getRow() < 0 || rect.getUpperLeft().getCol() < 0
          || rect.getLowerRight().getRow() >= rows || rect.getLowerRight().getCol() >= cols) {
        throw new IllegalArgumentException("Room out of bounds: " + r.getName());
      }
    }
  }

  /**
   * Overlap means positive-area intersection; shared edges/corners are allowed.
   */
  private static void validateNoOverlap(List<Room> rooms) {
    for (int i = 0; i < rooms.size(); i++) {
      for (int j = i + 1; j < rooms.size(); j++) {
        if (rooms.get(i).getArea().intersects(rooms.get(j).getArea())) {
          throw new IllegalArgumentException(
              "Overlapping rooms: " + rooms.get(i).getName() + " and " + rooms.get(j).getName());
        }
      }
    }
  }

  private static void validateItems(List<Room> rooms, List<Item> items) {
    int n = rooms.size();
    for (Item it : items) {
      int ri = it.getRoomIndex();
      if (ri < 0 || ri >= n) {
        throw new IllegalArgumentException(
            "Item '" + it.getName() + "' has invalid room index: " + ri);
      }
      if (it.getDamage() < 0) {
        throw new IllegalArgumentException("Item '" + it.getName() + "' has negative damage.");
      }
    }
  }

  private static void validateTargetStart(List<Room> rooms, Target target) {
    int idx = target.getCurrentSpaceIndex();
    if (idx < 0 || idx >= rooms.size()) {
      throw new IllegalArgumentException("Target start index out of range: " + idx);
    }
  }

  private static IllegalArgumentException parseError(int lineNo, String msg) {
    return new IllegalArgumentException("Parse error at line " + lineNo + ": " + msg);
  }
}
