package killdrlucky;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Controller for the Kill Doctor Lucky game using the Command design pattern.
 * 
 * <p>This controller is responsible for:
 *   Reading and parsing user input
 *   Creating appropriate Command objects via command factory
 *   Executing commands through the Command interface
 *   Managing turn order and tracking turn count
 *   Displaying output to the user
 * 
 */
public class GameController implements Controller {

  private final GameModelApi model;
  private final Readable in;
  private final Appendable out;
  private final int maxTurns;
  private final Map<String, Function<Scanner, Command>> commandFactory;

  /**
   * Constructs a GameController using the Command pattern.
   *
   * @param model the game model containing all game logic; must not be null
   * @param in the input source for reading user commands; must not be null
   * @param out the output destination for displaying information; must not be null
   * @param maxTurns the maximum number of turns allowed; must be positive
   * @throws IllegalArgumentException if any parameter is null or maxTurns is not positive
   */
  public GameController(GameModelApi model, Readable in, Appendable out, int maxTurns) {
    if (model == null || in == null || out == null) {
      throw new IllegalArgumentException("Model, input, and output cannot be null");
    }
    if (maxTurns <= 0) {
      throw new IllegalArgumentException("Max turns must be positive, got: " + maxTurns);
    }

    this.model = model;
    this.in = in;
    this.out = out;
    this.maxTurns = maxTurns;
    this.commandFactory = new HashMap<>();
    
    initializeCommandFactory();
  }

  /**
   * Initializes the command factory with mappings from command names to constructors.
   */
  private void initializeCommandFactory() {
    commandFactory.put("add", this::createAddPlayerCommand);
    commandFactory.put("move", this::createMoveCommand);
    commandFactory.put("pickup", this::createPickUpCommand);
    commandFactory.put("look", this::createLookAroundCommand);
    commandFactory.put("describe", this::createDescribePlayerCommand);
    commandFactory.put("space", this::createDescribeSpaceCommand);
    commandFactory.put("save", this::createSaveImageCommand);
    commandFactory.put("attack", this::createAttackCommand);
    commandFactory.put("movepet", this::createMovePetCommand);
  }

  /**
   * Factory method for creating AddPlayerCommand from user input.
   * 
   * <p>Expected format: add &lt;name&gt; &lt;startIndex&gt; &lt;isAI&gt; [capacity]
   * Examples:
   *
   *<p>add Alice 0 false 3
   *   add Bot 5 true 5
   *   add Player1 2 no 4
   *
   * @param parser the scanner containing command parameters
   * @return a new AddPlayerCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createAddPlayerCommand(Scanner parser) {
    if (!parser.hasNext()) {
      throw new IllegalArgumentException(
          "Usage: add <name> <startIndex> <isAI> [capacity]");
    }
    
    String name = parser.next();
    
    if (!parser.hasNextInt()) {
      throw new IllegalArgumentException("Start index must be a number");
    }
    int start = parser.nextInt();
    
    if (!parser.hasNext()) {
      throw new IllegalArgumentException("Must specify if AI (true/false/yes/no)");
    }
    String aiInput = parser.next().toLowerCase();
    boolean isAi = "true".equals(aiInput) || "yes".equals(aiInput) || "y".equals(aiInput);
    
    // Default capacity is 3 if not specified
    int capacity = parser.hasNextInt() ? parser.nextInt() : 3;
    
    return new AddPlayerCommand(model, name, start, isAi, capacity);
  }

  /**
   * Factory method for creating MoveCommand from user input.
   * 
   * <p>Expected format: move &lt;playerName&gt; &lt;destination&gt;
   *
   * <p>Examples:
   *   move Alice Kitchen
   *   move Bob 0
   *
   * @param parser the scanner containing command parameters
   * @return a new MoveCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createMoveCommand(Scanner parser) {
    if (!parser.hasNext()) {
      throw new IllegalArgumentException("Usage: move <player> <destination>");
    }
    
    String name = parser.next();
    String destination = parser.hasNextLine() ? parser.nextLine().trim() : "";
    
    if (destination.isEmpty()) {
      throw new IllegalArgumentException("Destination cannot be empty");
    }
    
    return new MoveCommand(model, name, destination);
  }

  /**
   * Factory method for creating PickUpCommand from user input.
   * 
   * <p>Expected format: pickup &lt;playerName&gt; &lt;itemName&gt;
   *
   * <p>Example: pickup Alice Knife
   *
   * @param parser the scanner containing command parameters
   * @return a new PickUpCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createPickUpCommand(Scanner parser) {
    if (!parser.hasNext()) {
      throw new IllegalArgumentException("Usage: pickup <player> <item>");
    }
    
    String name = parser.next();
    String item = parser.hasNextLine() ? parser.nextLine().trim() : "";
    
    if (item.isEmpty()) {
      throw new IllegalArgumentException("Item name cannot be empty");
    }
    
    return new PickUpCommand(model, name, item);
  }

  /**
   * Factory method for creating LookAroundCommand from user input.
   * 
   * <p>Expected format: look &lt;playerName&gt;
   *
   * <p>Example: look Alice
   *
   * @param parser the scanner containing command parameters
   * @return a new LookAroundCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createLookAroundCommand(Scanner parser) {
    if (!parser.hasNext()) {
      throw new IllegalArgumentException("Usage: look <player>");
    }
    
    String name = parser.next();
    return new LookAroundCommand(model, name);
  }

  /**
   * Factory method for creating DescribePlayerCommand from user input.
   * 
   * <p>Expected format: describe &lt;playerName&gt;
   *
   * <p>Example: describe Alice
   *
   * @param parser the scanner containing command parameters
   * @return a new DescribePlayerCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createDescribePlayerCommand(Scanner parser) {
    if (!parser.hasNext()) {
      throw new IllegalArgumentException("Usage: describe <player>");
    }
    
    String name = parser.next();
    return new DescribePlayerCommand(model, name);
  }

  /**
   * Factory method for creating DescribeSpaceCommand from user input.
   * 
   * <p>Expected format: space &lt;spaceName&gt;
   *
   * <p>Example: space Kitchen
   *
   * @param parser the scanner containing command parameters
   * @return a new DescribeSpaceCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createDescribeSpaceCommand(Scanner parser) {
    String space = parser.hasNextLine() ? parser.nextLine().trim() : "";
    
    if (space.isEmpty()) {
      throw new IllegalArgumentException("Usage: space <roomName>");
    }
    
    System.out.println("DEBUG: Parsed space name: [" + space + "]"); 
    
    return new DescribeSpaceCommand(model, space);
  }

  /**
   * Factory method for creating a save image command from user input.
   * 
   * <p>Expected format: save [filename]
   *
   * <p>Examples:
   *   save (uses default "world_map.png")
   *   save my_world.png
   * 
   * <p>Note: This uses an anonymous class for simplicity.
   *
   * @param parser the scanner containing command parameters
   * @return a new Command that saves the world image
   */
  private Command createSaveImageCommand(Scanner parser) {
    String filename = parser.hasNext() ? parser.next() : "world_map.png";
    
    return new Command() {
      @Override
      public String execute() {
        try {
          model.saveWorldImage(filename);
          return "✓ Saved world map as " + filename;
        } catch (IOException e) {
          return "✗ Error saving image: " + e.getMessage();
        }
      }
      
      @Override
      public boolean isTurnAction() {
        return false;
      }
      
      @Override
      public String toString() {
        return "SaveImageCommand[filename=" + filename + "]";
      }
    };
  }
  
  /**
   * Factory method for creating AttackCommand from user input.
   * 
   * <p>Expected format: attack &lt;playerName&gt; [itemName]
   *
   * <p>Examples:
   * <ul>
   *   <li>attack Alice Knife</li>
   *   <li>attack Bob (poke in the eye)</li>
   * </ul>
   *
   * @param parser the scanner containing command parameters
   * @return a new AttackCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createAttackCommand(Scanner parser) {
    if (!parser.hasNext()) {
      throw new IllegalArgumentException("Usage: attack <player> [item]");
    }
    
    String playerName = parser.next();
    String itemName = parser.hasNextLine() ? parser.nextLine().trim() : "";
    
    // Empty itemName means "poke in the eye"
    if (itemName.isEmpty()) {
      itemName = null;
    }
    
    return new AttackCommand(model, playerName, itemName);
  }

  /**
   * Factory method for creating MovePetCommand from user input.
   * 
   * <p>Expected format: movepet &lt;spaceName&gt;
   *
   * <p>Example: movepet Kitchen
   *
   * @param parser the scanner containing command parameters
   * @return a new MovePetCommand
   * @throws IllegalArgumentException if input format is invalid
   */
  private Command createMovePetCommand(Scanner parser) {
    String spaceName = parser.hasNextLine() ? parser.nextLine().trim() : "";
    
    if (spaceName.isEmpty()) {
      throw new IllegalArgumentException("Usage: movepet <roomName>");
    }
    
    return new MovePetCommand(model, spaceName);
  }

  /**
   * Starts and manages the complete game flow.
   * 
   * <p>Game phases:
   *   Setup:< Players are added to the game</li>
   *   Game Loop: Players take turns executing commands
   *   End Game: Display final results and statistics
   *
   *
   * @throws IOException if there's an error reading input or writing output
   */
  @Override
  public void playGame() throws IOException {
    out.append("╔════════════════════════════════════════╗\n");
    out.append("║     KILL DOCTOR LUCKY GAME             ║\n");
    out.append("╚════════════════════════════════════════╝\n\n");
    out.append("Type 'help' for command list.\n\n");

    Scanner scan = new Scanner(in);

    // Phase 1: Setup - Add players
    setupPhase(scan);

    if (model.getPlayers().isEmpty()) {
      out.append("No players added. Exiting.\n");
      return;
    }

    out.append("\n╔════════════════════════════════════════╗\n");
    out.append("║          GAME START!                   ║\n");
    out.append("╚════════════════════════════════════════╝\n");

    // Phase 2: Game Loop
    gameLoop(scan);

    // Phase 3: End Game
    // End game summary is displayed at the end of gameLoop
  }

  /**
   * Manages the setup phase where players are added to the game.
   * 
   * <p>During setup, users can:
   * <ul>
   *   <li>Add human-controlled players</li>
   *   <li>Add computer-controlled players</li>
   *   <li>View help information</li>
   *   <li>Start the game when ready</li>
   * </ul>
   * 
   * <p>The game cannot start until at least one player is added.
   *
   * @param scan the scanner for reading user input
   * @throws IOException if there's an error writing output
   */
  private void setupPhase(Scanner scan) throws IOException {
    out.append("═══════════════════════════════════════\n");
    out.append("         SETUP PHASE\n");
    out.append("═══════════════════════════════════════\n");
    out.append("Add players using: add <name> <startIndex> <isAI> <capacity>\n");
    out.append("Examples:\n");
    out.append("  add Player 0 false 5     (human player)\n");
    out.append("  add Bot 1 true 5        (AI player)\n");
    out.append("Type 'start' when ready to begin.\n\n");

    while (scan.hasNextLine()) {
      out.append("> ");
      String input = scan.nextLine().trim();

      if (input.isEmpty()) {
        continue;
      }

      if ("start".equalsIgnoreCase(input)) {
        if (model.getPlayers().isEmpty()) {
          out.append("Add at least one player before starting!\n");
        } else {
          break;
        }
      } else if ("help".equalsIgnoreCase(input)) {
        printHelp();
      } else {
        // Use command pattern even in setup phase
        executeCommand(input, false);
      }
    }
  }

  /**
   * Manages the main game loop where players take turns.
   * 
   * <p>Each turn consists of:
   * <ol>
   *   <li>Display whose turn it is</li>
   *   <li>Show current player state and target location</li>
   *   <li>Execute an action (human input or AI automatic)</li>
   *   <li>Move the target character if turn action was taken</li>
   *   <li>Check for game-ending conditions</li>
   * </ol>
   * 
   * <p>The game ends when:
   * <ul>
   *   <li>The target character is killed (health reaches 0)</li>
   *   <li>Maximum turns are reached</li>
   *   <li>The user quits</li>
   * </ul>
   *
   * @param scan the scanner for reading user input
   * @throws IOException if there's an error writing output
   */
  private void gameLoop(Scanner scan) throws IOException {
    int turnCount = 0;
    
    while (!model.isGameOver() && turnCount < maxTurns && scan.hasNextLine()) {
      List<Iplayer> players = model.getPlayers();
      Iplayer currentPlayer = players.get(turnCount % players.size());

      // Display turn header
      out.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
      out.append(String.format("Turn %d | Player: %s", 
                               turnCount + 1,
                               currentPlayer.getName()));
      if (currentPlayer.isComputerControlled()) {
        out.append(" (AI)");
      }
      out.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

      // Show current state
      out.append(model.describePlayer(currentPlayer.getName())).append("\n");
      out.append(String.format("Target location: %s\n\n",
                               model.getSpace(model.getTarget().getCurrentSpaceIndex())
                                   .getName()));

      boolean turnEnded = false;

      if (currentPlayer.isComputerControlled()) {
        // Computer player takes automatic action
        String result = model.autoAction(currentPlayer.getName());
        out.append(result).append("\n");
        turnEnded = true;
      } else {
        // Human player inputs command
        out.append("Enter command (or 'help'): ");
        String input = scan.nextLine().trim();

        if ("quit".equalsIgnoreCase(input)) {
          out.append("Game ended by user.\n");
          model.endGame();
          break;
        }

        // Execute command and check if it's a turn action
        turnEnded = executeCommand(input, true);
      }

      // If turn ended, move target and increment turn counter
      if (turnEnded) {
        model.moveTarget();
        out.append(String.format("\n→ Target moved to: %s\n",
                                model.getSpace(model.getTarget().getCurrentSpaceIndex())
                                    .getName()));
        // EXTRA CREDIT: Move pet with DFS traversal
        int oldPetLocation = model.getPet().getCurrentSpaceIndex();
        model.movePetDfs();
        int newPetLocation = model.getPet().getCurrentSpaceIndex();
        
        if (oldPetLocation != newPetLocation) {
          out.append(String.format("🐾 %s wandered to: %s\n",
                                  model.getPet().getName(),
                                  model.getSpace(newPetLocation).getName()));
        }
        turnCount++;
      }
    }

    // Display end game summary
    out.append("\n╔════════════════════════════════════════╗\n");
    out.append("║         GAME OVER                      ║\n");
    out.append("╚════════════════════════════════════════╝\n\n");

    out.append("Turns played: ").append(String.valueOf(turnCount)).append("\n");

    if (turnCount >= maxTurns) {
      out.append("Reason: Maximum turns reached\n");
    } else if (!model.getTarget().isAlive()) {
      out.append("Target eliminated! \n");
    } else {
      out.append("Reason: Game ended by user\n");
    }

    out.append("\nThank you for playing!\n");
  }

  /**
   * Executes a command based on user input string using the Command pattern.
   * 
   * <p>This method demonstrates the Command pattern in action:
   * <ol>
   *   <li>Parse the command name from input</li>
   *   <li>Use command factory to create appropriate Command object</li>
   *   <li>Execute the command through the Command interface</li>
   *   <li>Display the result</li>
   *   <li>Return whether the command was a turn action</li>
   * </ol>
   * 
   * <p>The controller doesn't need to know the details of each command -
   * it just creates and executes them through the Command interface.
   * This makes the system flexible and easy to extend.
   *
   * @param input the raw user input string
   * @param checkTurn whether to check if this is a turn action
   * @return true if the command was a turn action, false otherwise
   * @throws IOException if there's an I/O error
   */
  private boolean executeCommand(String input, boolean checkTurn) throws IOException {
    Scanner parser = new Scanner(input);

    if (!parser.hasNext()) {
      out.append("Invalid command.\n");
      return false;
    }

    String commandName = parser.next().toLowerCase();

    // Special case: help command
    if ("help".equals(commandName)) {
      printHelp();
      return false;
    }

    try {
      // Use factory to create command
      Function<Scanner, Command> factory = commandFactory.get(commandName);
      
      if (factory == null) {
        out.append("Unknown command: ").append(commandName).append("\n");
        out.append("    Type 'help' for available commands.\n");
        return false;
      }

      // Create the command using the factory
      Command command = factory.apply(parser);
      
      // Execute the command
      String result = command.execute();
      out.append(result).append("\n");

      // Return whether this was a turn action
      return checkTurn && command.isTurnAction();

    } catch (IllegalArgumentException e) {
      out.append("Error: ").append(e.getMessage()).append("\n");
      return false;
    } catch (IOException e) {
      out.append("Unexpected error: ").append(e.getMessage()).append("\n");
      return false;
    }
  }

  /**
   * Displays help information about available commands.
   * 
   * <p>Shows all commands organized by category:
   * <ul>
   *   <li>Setup commands (used before game starts)</li>
   *   <li>Turn actions (consume a turn when executed)</li>
   *   <li>Information commands (don't consume a turn)</li>
   *   <li>Other utility commands</li>
   * </ul>
   *
   * @throws IOException if there's an error writing output
   */
  private void printHelp() throws IOException {
    out.append("""
        
        ╔══════════════════════════════════════════════════════════╗
        ║                  COMMAND REFERENCE                       ║
        ╠══════════════════════════════════════════════════════════╣
        ║ Setup Commands:                                          ║
        ║   add <name> <start> <AI> <capacity>  - Add a player     ║
        ║      Example: add Alice 0 false 5                        ║
        ║      Example: add Bot 3 true 3                           ║
        ║                                                          ║
        ║ Turn Actions (consume a turn):                           ║
        ║   move <player> <destination>  - Move to neighbor space  ║
        ║   pickup <player> <item>       - Pick up an item         ║
        ║   look <player>                - Look around             ║
        ║   attack <player> [item]       - Attack the target       ║
        ║   movepet <roomName>           - Move the pet            ║
        ║                                                          ║
        ║ Information Commands (don't consume turn):               ║
        ║   describe <player>            - Player info             ║
        ║   space <room name>            - Room info               ║
        ║   save [filename]              - Save world map          ║
        ║                                                          ║
        ║ Other:                                                   ║
        ║   help                         - Show this help          ║
        ║   quit                         - End game                ║
        ╚══════════════════════════════════════════════════════════╝
        
        """);
  }
}