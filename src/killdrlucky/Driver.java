package killdrlucky;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/**
 * Command-line driver for demonstrating the Kill Dr Lucky model.
 *
 * <p>Usage:
 *   java killdrlucky.Driver spec-file [--cell int] [--out png] [--space idx]
 *
 * <p>Example:
 *   java killdrlucky.Driver mansion.txt
 *   java killdrlucky.Driver mansion.txt --cell 24 --out mansion.png --space 5
 *
 * <p>This driver demonstrates:
 *  1. Reading and parsing the world specification file.
 *  2. Querying neighbors of a given space.
 *  3. Describing a space (name, items, visible spaces).
 *  4. Moving the target around the world.
 *  5. Rendering a world map as a BufferedImage and saving it to PNG.
 */
public class Driver {
  /**.
   *
   * @param args Passing args to Driver class
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      printUsageAndExit();
    }

    String specPath = args[0];
    int cellSize = 20;               // default cell size in pixels
    String outPath = "res/world.png";    // default output file name
    int demoSpaceIdx = 0;            // default space index to demonstrate

    // Parse optional arguments
    for (int i = 1; i < args.length; i++) {
      switch (args[i]) {
        case "--cell":
          ensureHasValue(args, i);
          cellSize = parsePositiveInt(args[++i], "--cell");
          break;
        case "--out":
          ensureHasValue(args, i);
          outPath = args[++i];
          break;
        case "--space":
          ensureHasValue(args, i);
          demoSpaceIdx = parseNonNegativeInt(args[++i], "--space");
          break;
        default:
          System.err.println("Unknown argument: " + args[i]);
          printUsageAndExit();
      }
    }

    try {
      // 1. Parse the world specification and create the world model
      WorldParser parser = new WorldParser();
      WorldParser.WorldData data = parser.parse(Path.of(specPath));
      World world = new World(data, new AxisAlignedVisibility());

      // 2. Print basic world info
      System.out.println("=== World Info ===");
      System.out.println("Name: " + world.getWorldName());
      System.out.println("Grid: " + world.getRows() + " rows x " + world.getCols() + " cols");
      System.out.println("Spaces: " + world.getSpaces().size());
      System.out.println("Target starts at space: " + world.getTarget().getCurrentSpaceIndex());
      System.out.println();

      // Validate demo space index
      if (demoSpaceIdx < 0 || demoSpaceIdx >= world.getSpaces().size()) {
        System.err.println("Invalid --space index: " + demoSpaceIdx);
        System.exit(2);
      }

      // 3. Show neighbors of the selected space
      System.out.println("=== Neighbors of space " + demoSpaceIdx + " (" 
          + world.getSpace(demoSpaceIdx).getName() + ") ===");
      System.out.println(world.neighborsOf(demoSpaceIdx));
      System.out.println();

      // 4. Show space description (name, items, visible spaces)
      System.out.println("=== Describe space " + demoSpaceIdx + " ===");
      System.out.println(world.describeSpace(demoSpaceIdx));
      System.out.println();

      // 5. Demonstrate target movement
      System.out.println("=== Move Target ===");
      System.out.println("Current target space: " + world.getTarget().getCurrentSpaceIndex());
      for (int step = 1; step <= 3; step++) {
        world.moveTargetNext();
        System.out.println("After move " + step + ": " + world.getTarget().getCurrentSpaceIndex());
      }
      System.out.println();

      // 6. Render and save a PNG map
      BufferedImage img = world.renderBufferedImage(cellSize);
      Path out = Paths.get(outPath);
      ImageIO.write(img, "png", out.toFile());
      System.out.println("Map image written to: " + out.toAbsolutePath());

    } catch (IOException e) {
      System.err.println("Failed to read world file: " + e.getMessage());
      System.exit(3);
    } catch (IllegalArgumentException e) {
      System.err.println("Input/parse error: " + e.getMessage());
      System.exit(4);
    }
  }

  // --------- Helper Methods ---------

  private static void printUsageAndExit() {
    System.err.println("Usage:");
    System.err.println(
        "  java killdrlucky.Driver <spec-file> [--cell <int>] [--out <png>] [--space <idx>]");
    System.err.println();
    System.err.println("Example:");
    System.err.println("  java killdrlucky.Driver mansion.txt");
    System.err.println(
        "  java killdrlucky.Driver mansion.txt --cell 24 --out mansion.png --space 5");
    System.exit(1);
  }

  private static void ensureHasValue(String[] args, int i) {
    if (i + 1 >= args.length) {
      printUsageAndExit();
    }
  }

  private static int parsePositiveInt(String s, String flag) {
    try {
      int v = Integer.parseInt(s);
      if (v <= 0) {
        throw new NumberFormatException();
      }
      return v;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(flag + " must be a positive integer: " + s);
    }
  }

  private static int parseNonNegativeInt(String s, String flag) {
    try {
      int v = Integer.parseInt(s);
      if (v < 0) {
        throw new NumberFormatException();
      }
      return v;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(flag + " must be a non-negative integer: " + s);
    }
  }
}
