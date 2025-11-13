package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ComputerPlayer.
 */
public class ComputerPlayerTest {

  private World world;

  /**
   * Sets up the test environment before each test.
   *
   * @throws IOException if there's an error reading the world file
   */
  @BeforeEach
  void setup() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }

  @Test
  void testIsComputerControlledTrue() {
    ComputerPlayer bot = new ComputerPlayer("Bot", 0, 10);
    assertTrue(bot.isComputerControlled());
  }

  @Test
  void testAutoActionMovesOrPicksUp() {
    world.addPlayer("Bot", 0, true, 3);
    String result = world.autoAction("Bot");
    assertTrue(result.contains("AI"));
  }

  @Test
  void testConstructor() {
    ComputerPlayer bot = new ComputerPlayer("bot", 1, 10);
    assertEquals("bot", bot.getName());
    assertEquals(1, bot.getCurrentSpaceIndex());
  }
}
