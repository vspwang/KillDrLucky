package killdrlucky;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Driver class for launching the Kill Doctor Lucky game.
 * 
 * <p>This driver reads the world file path and optional maximum turns
 * from command-line arguments, constructs the world model, and launches
 * the interactive text-based controller. Input and output are abstracted
 * to Readable and Appendable to enable automated testing.
 */
public class Driver {

  /**
   * Entry point of the program.
   *
   * @param args Command-line arguments:
   *             <ul>
   *               <li>args[0] = world file path (e.g., "res/mansion.txt")</li>
   *               <li>args[1] = maximum number of turns (optional)</li>
   *             </ul>
   */
  public static void main(String[] args) {
    String worldFile = args.length > 0 ? args[0] : "res/mansion.txt";
    int maxTurns = args.length > 1 ? Integer.parseInt(args[1]) : 10;

    try {
      // Parse world file
      Path filePath = Path.of(worldFile);
      WorldParser parser = new WorldParser();
      WorldParser.WorldData data = parser.parse(filePath);

      // Build model
      VisibilityStrategy strategy = new AxisAlignedVisibility();
      GameModelApi model = new World(data, strategy);

      // Optional: generate map image
      BufferedImage img = model.renderBufferedImage(20);
      File outFile = new File("world_map.png");
      ImageIO.write(img, "png", outFile);
      System.out.println("Map image saved to: " + outFile.getAbsolutePath());

      // Abstract input/output for testability
      Readable in = new InputStreamReader(System.in);
      Appendable out = System.out;

      // Launch controller
      GameController controller = new GameController(model, in, out, maxTurns);
      controller.playGame();

    } catch (IOException e) {
      System.err.println("Error reading world file or saving map: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid argument: " + e.getMessage());
    }
  }
}
