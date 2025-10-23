package killdrlucky;



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for GameController.
 */
public class GameControllerTest {

  private World world;

  @BeforeEach
  void setup() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(Path.of("res/mansion.txt"));
    world = new World(data, new AxisAlignedVisibility());
  }


  @Test
  void testInvalidCommandHandledGracefully() throws IOException {
    String input = "foobar\nquit\n";
    StringWriter out = new StringWriter();
    GameController controller = new GameController(world, new StringReader(input), out, 5);

    controller.playGame();

    String result = out.toString();
    //assertTrue(result.contains("Please add at least one player"));
  }
  



}
