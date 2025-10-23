# Kill Dr Lucky – Interactive MVC Implementation (Milestone 2)

## 1. Overview

This milestone extends the Kill Doctor Lucky project into a fully interactive, modular, and testable system following the Model–View–Controller (MVC) architecture.

It introduces an interactive text-based controller, allowing users to play directly through the console, with both human-controlled and computer-controlled players taking alternating turns.
Core gameplay now includes:

- **Adding players (human or AI)**  
- **Moving between rooms**   
- **Picking up items**  
- **Looking around**   
- **Displaying player and space information**   
- **Automatic AI decision-making**  
- **Generating and saving the world map as a PNG**
- **Enforcing a maximum number of turns before the game ends**

---

## 2. Architecture Overview

### Layer Responsibilities

| Layer          | Component                                                                     | Description                                                                                              |
| -------------- | ----------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| **Model**      | `World`, `Room`, `Item`, `Target`, `Player`, `ComputerPlayer`                 | Represents and updates the full game state. Implements `GameModelApi` and `WorldModel`.                  |
| **Controller** | `GameController`                                                              | Handles command-line input/output, manages turns, executes player actions, and interacts with the model. |
| **View**       | (Integrated in controller for now)                                            | Text-based console output; a graphical view will be added in a later milestone.                          |
| **Utility**    | `WorldParser`, `Rect`, `Point`, `VisibilityStrategy`, `AxisAlignedVisibility` | Parsing and geometry utilities, determining visibility and adjacency of rooms.                           |

### Design Patterns Used
- **Model–View–Controller (MVC)** – Separates logic (model) from user interaction (controller).

- **Strategy Pattern** – Enables interchangeable visibility logic via VisibilityStrategy.

- **Factory Pattern (WorldParser)** – Parses the world configuration file into a reusable WorldData structure.

- **Facade Pattern** – GameModelApi abstracts the complexity of the underlying World model.

---

## 3. How to Run

### Command-Line Execution

```bash
java -jar <spec-file> <input-worldfile> <max-turns>
```

### Example:
```bash
java -jar KillDrLucky.jar res/mansion.txt 10

```

```
| Argument          | Description                                                 |
| ----------------- | ----------------------------------------------------------- |
| `res/mansion.txt` | Path to the world file parsed by `WorldParser`.             |
| `10`              | Maximum number of turns before the game automatically ends. |

```

## 4. Example Run

File: res/run_basics.txt

### Purpose:
This run shows:

- Adding a human-controlled player

- Adding a computer-controlled player

- Human player moving, picking up an item, and looking around

- AI player performing automatic random actions

- Alternating turns between players

- Displaying player and room descriptions

- Saving the world map as world_map.png

- Ending the game automatically after the turn limit

### Comand Used:
```
java -jar KillDrLucky.jar res/mansion.txt 10 > res/run_basics.txt

```

### Output Files Generated:
- res/run_basics.txt — textual demonstration log
- res/WorldMap.png — visual map output


## References
8. References

The following references were used during the design and implementation of this milestone:

- Java SE 17 API Documentation – consulted for classes such as List, Path, and BufferedReader.

- YouTube tutorials – used to better understand turn-based game logic and command-line input handling in Java.

- Creative Design Inspiration – the world, room, and item names were partially inspired by Frank Herbert’s Dune to create a thematic consistency.


