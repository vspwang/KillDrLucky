package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ComputerPlayer.
 */
public class ComputerPlayerTest {

  private World world;

  @BeforeEach
  void setup() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }

  @Test
  void testIsComputerControlledTrue() {
    ComputerPlayer bot = new ComputerPlayer("Bot", 0, new ArrayList<>());
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
    ComputerPlayer bot = new ComputerPlayer("bot", 1, new ArrayList<>());
    assertEquals("bot", bot.getName());
    assertEquals(1, bot.getCurrentSpaceIndex());
  }
}
