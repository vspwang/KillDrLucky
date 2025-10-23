package killdrlucky;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Text-based controller for the Kill Doctor Lucky game. Supports multiple
 * players and alternating turns.
 */
public class GameController {

  private final GameModelApi model;
  private final Readable in;
  private final Appendable out;
  private final int maxTurns;

  /**
   * Constructs a GameController instance responsible for managing user
   * interaction and turn-based gameplay in the Kill Doctor Lucky game.
   *
   * @param model    the game model implementing the core game logic
   * @param in       the input source used to read user commands
   * @param out      the output destination for displaying game information
   * @param maxTurns the maximum number of turns to allow before the game ends
   */
  public GameController(GameModelApi model, Readable in, Appendable out, int maxTurns) {
    if (model == null || in == null || out == null) {
      throw new IllegalArgumentException("Arguments cannot be null");
    }
    this.model = model;
    this.in = in;
    this.out = out;
    this.maxTurns = maxTurns;
  }

  /**
   * Main interactive loop.
   */
  public void playGame() throws IOException {

    out.append("==========Kill Doctor Lucky==========\n");
    out.append("Type 'help' for command list.\n");
    Scanner scan = new Scanner(in);
    
    // Phase 1: Add players
    while (model.getPlayers().isEmpty()) {
      if (!scan.hasNextLine()) {
        out.append("No more input. Exiting.\n");
        return;
      }
      // out.append("\nNo players yet. Use: add <name> <startIndex> <isAi>\n> ");
      String cmd = scan.nextLine().trim();
      if (cmd.startsWith("add") || cmd.startsWith("help")) { 
        executeCommand(cmd);
      } else {
        out.append("Game needs at least one player before starting.\n");
      }
    }

    out.append("\nPlayers entered. Game begins!\n");
    
    int turnCount = 0;
    while (!model.isGameOver() && scan.hasNextLine() && turnCount < maxTurns) {
      if (!scan.hasNextLine()) {
        out.append("No more input. Ending game.\n");
        break;
      }

      List<Iplayer> players = model.getPlayers();
      Iplayer current = players.get(turnCount % players.size());

      
      out.append("\n--- Turn " + (turnCount + 1) + " ---\n");
      out.append("Current player: ").append(current.getName()).append("\n");

      boolean endTurn = false;

      if (current.isComputerControlled()) {
        out.append("\n==========================\n");
        out.append(" Computer Player Turn — ").append(current.getName()).append("\n");
        out.append("==========================\n");

        // Show state before the action
        out.append("Before action:\n").append(model.describePlayer(current.getName())).append("\n");

        // Perform the automatic move / pickup / look
        String result = model.autoAction(current.getName());
        out.append("[Computer Action] ").append(result).append("\n");

        // Show state after the action
        out.append("After action:\n").append(model.describePlayer(current.getName())).append("\n");

        endTurn = true; // Computer always ends its turn
      } else {
        out.append("Before action:\n").append(model.describePlayer(current.getName())).append("\n");
        out.append("Enter command (help for list): \n");
        String input = scan.nextLine().trim();

        if ("quit".equalsIgnoreCase(input)) {
          out.append("Game ended by user.\n");
          model.endGame();
          break;
        }
        endTurn = executeCommand(input);
      }

      if (endTurn) {
        model.moveTarget();
        turnCount++;
      }
    }

    out.append("\nGame over! Turns played: " + turnCount + "\n");
    if (turnCount >= maxTurns) {
      out.append("(Reached maximum number of turns)\n");
    }
    out.append("Thank you for playing!\n");
  }

  /**
   * Parses and executes a command.
   */
  private boolean executeCommand(String input) throws IOException {
    Scanner parser = new Scanner(input);
    if (!parser.hasNext()) {
      out.append("Invalid command.\n");
      return false;
    }

    String command = parser.next().toLowerCase();

    try {
      switch (command) {
        case "add": {
          String name = parser.next();
          int start = parser.nextInt();
          String aiInput = parser.next().toLowerCase();
          boolean isAi = "true".equals(aiInput) || "yes".equals(aiInput) || "y".equals(aiInput);
          model.addPlayer(name, start, isAi, 0);
          out.append("Added player ").append(name).append(" (AI: ").append(String.valueOf(isAi))
              .append(") at space ").append(String.valueOf(start)).append("\n");
          return false;
        }

        case "move": {
          String name = parser.next();
          String dir = parser.hasNextLine() ? parser.nextLine().trim() : "";
          if (dir.isEmpty()) {
            out.append("Usage: move <player> <space name>\n");
            return false;
          }
          out.append(model.movePlayer(name, dir)).append("\n");
          return true;
        }

        case "pickup": {
          String name = parser.next();
          String item = parser.nextLine().trim();
          out.append(model.pickUpItem(name, item)).append("\n");
          return true;
        }

        case "look": {
          String name = parser.next();
          out.append(model.lookAround(name)).append("\n");
          return true;
        }

        case "describe": {
          String name = parser.next();
          out.append(model.describePlayer(name)).append("\n");
          return false;
        }

        case "space": {
          String space = parser.nextLine().trim();
          out.append(model.describeSpace(space)).append("\n");
          return false;
        }

        case "save": {
          String filename = parser.hasNext() ? parser.next() : "world_map.png";
          model.saveWorldImage(filename);
          out.append("Saved world map as ").append(filename).append("\n");
          return false;
        }

        case "help": {
          printHelp();
          return false;
        }

        default:
          out.append("Unknown command. Type 'help' for available options.\n");
          return false;
      }
    } catch (IOException | IllegalArgumentException e) {
      out.append("Error executing command: ")
          .append(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())
          .append("\n");
      return false;
    }
  }

  /**
   * Prints available commands.
   */
  private void printHelp() throws IOException {
    out.append("""
        \nGame Commands:
        add <name> <startIndex> <isAi>   - Add player (Computer or human)
        move <name> <destination>          - Move the player
        pickup <name> <itemName>         - Pick up an item in same room
        look <name>                      - Look around current room
        describe <name>                  - Describe player
        space <roomName>                 - Describe room
        quit                             - End the game
        """);
  }
}
