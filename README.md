# Kill Dr Lucky – Interactive Game with Pet and Attack System (Milestone 3)

## 1. Overview

This milestone completes the Kill Doctor Lucky game by implementing the full gameplay mechanics, including combat, pet behavior, and win conditions. The system follows the Model–View–Controller (MVC) architecture with the Command design pattern for extensible action handling.

### New Features in Milestone 3

- **Pet System**: The target character's pet affects visibility and can be moved by players
- **Attack Mechanics**: Players can attempt to kill the target using items or by "poking in the eye"
- **Win Conditions**: Game ends when target is killed or maximum turns are reached
- **Enhanced AI**: Computer-controlled players now intelligently attack when possible
- **Improved Visibility**: Look around command shows detailed information about neighboring spaces
- **Extra Credit**: Pet automatically wanders using depth-first traversal (DFS)

### Core Gameplay

Players take turns performing one of the following actions:

- **Moving** to a neighboring space
- **Picking up** items from their current location
- **Looking around** to observe surroundings and neighboring spaces
- **Attacking** the target character (succeeds only if unseen by others)
- **Moving the pet** to a specified space (affects visibility)

The game ends when:
- A player successfully kills the target character (that player wins)
- Maximum number of turns is reached (target escapes, no winner)

---

## 2. Architecture Overview

### Layer Responsibilities

| Layer          | Components                                                                                          | Description                                                                                  |
| -------------- | --------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| **Model**      | `World`, `Room`, `Item`, `Target`, `Pet`, `Player`, `ComputerPlayer`                                | Represents and updates game state. Implements `GameModelApi` and `WorldModel`.               |
| **Controller** | `GameController`, `Command` implementations                                                         | Handles I/O, manages turns, creates and executes Command objects.                            |
| **Commands**   | `MoveCommand`, `PickUpCommand`, `LookAroundCommand`, `AttackCommand`, `MovePetCommand`, etc.        | Encapsulates player actions following the Command pattern.                                   |
| **View**       | Console output (integrated in controller)                                                           | Text-based display; graphical view planned for future milestone.                             |
| **Utility**    | `WorldParser`, `Rect`, `Point`, `VisibilityStrategy`, `AxisAlignedVisibility`, `AttackStatus`       | Parsing, geometry, visibility calculation, and attack result enumeration.                    |

### Design Patterns Used

- **Model–View–Controller (MVC)** – Separates game logic from user interaction
- **Command Pattern** – Encapsulates actions as objects for flexibility and extensibility
- **Strategy Pattern** – Enables interchangeable visibility algorithms via `VisibilityStrategy`
- **Factory Pattern** – `WorldParser` creates game world from configuration file
- **Facade Pattern** – `GameModelApi` provides simplified interface to complex `World` model

---

## 3. List of Features

### Milestone 1 Features
- Parse world specification file with rooms, items, and target
- Display information about specific spaces
- Generate graphical representation of the world map (PNG)
- Compute space neighbors and visibility relationships

### Milestone 2 Features
- Add multiple players (human and computer-controlled)
- Move players between neighboring spaces
- Pick up items with capacity limits
- Look around to see current and visible spaces
- Display player information
- Automatic AI decision-making
- Turn-based gameplay with maximum turn limit

### Milestone 3 Features (New)
- **Pet system**: Target's pet blocks visibility of its current space
- **Player can move pet**: Strategic gameplay by relocating the pet
- **Attack mechanics**: Attempt to kill target with items or by poking
- **Visibility-based combat**: Attacks fail if seen by other players
- **Evidence system**: Used weapons are removed from the game
- **Win conditions**: Game ends when target dies or turns run out
- **Enhanced AI**: Computer players intelligently attack when possible
- **Improved look around**: Shows detailed information about neighboring spaces
- **Extra Credit**: Pet wanders automatically using DFS traversal

---

## 4. How to Run

### Prerequisites

- Java 17 or higher
- `KillDrLucky.jar` (executable JAR file)
- World specification file (e.g., `mansion.txt`)

### Creating the JAR File (Eclipse)

1. **Ensure Driver.java can run**:
   - Right-click `Driver.java` → **Run As** → **Java Application**
   
2. **Export as Runnable JAR**:
   - Right-click project → **Export...**
   - Select **Java** → **Runnable JAR file** → **Next**
   
3. **Configure export**:
   - Launch configuration: `Driver - KillDrLucky`
   - Export destination: `/path/to/KillDrLucky.jar`
   - Library handling: **Extract required libraries into generated JAR**
   - Click **Finish**

4. **Verify JAR**:
```bash
   java -jar KillDrLucky.jar mansion.txt 50
```

### Command-Line Execution

#### Interactive Mode (Manual Input)
```bash
java -jar KillDrLucky.jar <worldFile> <maxTurns>
```

**Example**:
```bash
java -jar KillDrLucky.jar mansion.txt 100
```

Then manually enter commands:
```
add Alice 0 false 5
add Bot 5 true 3
start
look Alice
move Alice Billiard Room
attack Alice Revolver
quit
```

#### Batch Mode (Input from File)
```bash
java -jar KillDrLucky.jar <worldFile> <maxTurns> < input.txt > output.txt
```

**Example**:
```bash
# Windows
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt 2>&1

# Linux/Mac
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt 2>&1
```

#### Interactive with Output Logging
```bash
# Linux/Mac (using tee)
java -jar KillDrLucky.jar mansion.txt 100 | tee run_basics.txt

# Windows PowerShell
java -jar KillDrLucky.jar mansion.txt 100 | Tee-Object run_basics.txt
```

This allows you to see output on screen while simultaneously saving to a file.

### Arguments

| Argument       | Description                                                    |
| -------------- | -------------------------------------------------------------- |
| `<worldFile>`  | Path to world specification file (e.g., `mansion.txt`)         |
| `<maxTurns>`   | Maximum number of turns before game automatically ends         |

---

## 5. Available Commands

### Setup Phase Commands

| Command                                | Description                          | Example                    |
| -------------------------------------- | ------------------------------------ | -------------------------- |
| `add <name> <start> <isAI> <capacity>` | Add a player to the game             | `add Alice 0 false 5`      |
| `start`                                | Begin the game                       | `start`                    |
| `help`                                 | Display command reference            | `help`                     |

### Turn Action Commands (Consume a Turn)

| Command                     | Description                                | Example                  |
| --------------------------- | ------------------------------------------ | ------------------------ |
| `move <player> <dest>`      | Move player to neighboring space           | `move Alice Kitchen`     |
| `pickup <player> <item>`    | Pick up an item from current space         | `pickup Alice Knife`     |
| `look <player>`             | Look around current and neighboring spaces | `look Alice`             |
| `attack <player> [item]`    | Attempt to kill the target                 | `attack Alice Revolver`  |
| `movepet <spaceName>`       | Move the pet to specified space            | `movepet Billiard Room`  |

### Information Commands (Don't Consume Turn)

| Command                | Description                   | Example            |
| ---------------------- | ----------------------------- | ------------------ |
| `describe <player>`    | Show player information       | `describe Alice`   |
| `space <roomName>`     | Show room information         | `space Armory`     |
| `save [filename]`      | Save world map as PNG         | `save map.png`     |

### Other Commands

| Command | Description    |
| ------- | -------------- |
| `help`  | Show help menu |
| `quit`  | End game       |

---

## 6. Example Runs

Three example run files are provided to demonstrate all required scenarios:

### File 1: `run_basics.txt`

**Purpose**: Demonstrates main gameplay features and scenarios

**Generated using**:
```bash
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt
```

**Scenarios demonstrated**:
- ✅ Pet blocking visibility of neighboring spaces
- ✅ Player moving the pet to a different location
- ✅ Human player making attack attempts (both failed and successful)
- ✅ Human player winning by killing the target
- ✅ Extra Credit: Pet wandering following DFS traversal pattern

**Input file** (`input_basics.txt`):
```
add Alice 0 false 5
add Bob 1 false 5
add AIBot 8 true 5
start
look Alice
space Armory
movepet Billiard Room
look Alice
move Alice Drawing Room
look Alice
move Alice Foyer
look Alice
move Bob Armory
attack Alice
move Bob Billiard Room
attack Alice
quit
```

### File 2: `run_ai_wins.txt`

**Purpose**: Demonstrates AI player attacking and winning

**Generated using**:
```bash
java -jar KillDrLucky.jar mansion.txt 200 < input_basics.txt > run_basics.txt
```

**Scenarios demonstrated**:
- ✅ Computer-controlled player making attack attempts
- ✅ Computer-controlled player winning the game

**Input file** (`input_basics.txt`):
```
add SuperBot 0 true 10
start
quit
```

### File 3: `run_target_escapes.txt`

**Purpose**: Demonstrates target escaping when maximum turns reached

**Generated using**:
```bash
java -jar KillDrLucky.jar mansion.txt 3 < input_escape.txt > run_target_escapes.txt
```

**Scenarios demonstrated**:
- ✅ Target character escaping with his life (game ending due to turn limit)

**Input file** (`input_escape.txt`):
```
add Alice 10 false 3
start
look Alice
look Alice
quit
```

---

## 7. Game Rules

### Attack Mechanics

Players can attempt to kill the target character by:

1. **Using an item**: Deals damage equal to the item's damage value
2. **Poking in the eye**: Deals 1 damage (when no item is specified)

**Attack conditions**:
- Player must be in the **same space** as the target
- Player must **not be seen** by any other player (in same or visible spaces)
- Used items are **removed** from the game as evidence

**Attack outcomes**:
- ✅ **Success**: Damage is dealt, item is removed
- ❌ **Seen by others**: Attack is stopped, no damage
- ❌ **Not in same space**: Cannot attack from a distance

### Pet Mechanics

The target character's pet (`Fortune the Cat` in the mansion):

- **Starts** in the same space as the target (space 0)
- **Blocks visibility**: Spaces containing the pet cannot be seen by neighbors
- **Can be moved** by players as a turn action
- **Wanders automatically** (Extra Credit): Follows DFS traversal after each turn

### Win Conditions

- **Player wins**: Successfully kills the target (health reaches 0)
- **Target escapes**: Maximum turns reached without target being killed (no winner)

### AI Behavior

Computer-controlled players:
1. **Priority 1**: Attack target if in same room and not seen by others (uses highest damage item)
2. **Priority 2**: Move to random neighboring space (50% chance)
3. **Priority 3**: Pick up items if capacity allows (50% chance)
4. **Default**: Look around

---

## 8. World File Format
```
<rows> <cols> <worldName>
<targetHealth> <targetName>
<petName>
<numberOfSpaces>
<ulRow> <ulCol> <lrRow> <lrCol> <spaceName>
...
<numberOfItems>
<spaceIndex> <damage> <itemName>
...
```

**Example** (`mansion.txt`):
```
36 30 Doctor Lucky's Mansion
2 Doctor Lucky
Fortune the Cat
21
22 19 23 26 Armory
16 21 21 28 Billiard Room
...
20
8 3 Crepe Pan
4 2 Letter Opener
...
```

---

## 9. Class Diagram Summary

### Model Classes
- **World**: Central game state manager, implements all game logic
- **Player** / **ComputerPlayer**: Data containers for player state
- **Target**: Represents Dr. Lucky with health tracking
- **Pet**: Target's pet that affects visibility
- **Room**: Represents individual spaces with geometric areas
- **Weapon**: Implements Item interface with damage values

### Command Classes (New in Milestone 3)
- **Command**: Interface defining execute() and isTurnAction()
- **MoveCommand**: Encapsulates player movement
- **PickUpCommand**: Encapsulates item pickup
- **LookAroundCommand**: Encapsulates observation action
- **AttackCommand**: Encapsulates attack attempts
- **MovePetCommand**: Encapsulates pet relocation
- **AddPlayerCommand**: Encapsulates player creation
- **DescribePlayerCommand** / **DescribeSpaceCommand**: Information queries

### Controller
- **GameController**: Manages game flow using Command pattern with factory method

---

## 10. Testing

### Test Coverage
- **Unit Tests**: Individual class methods (Player, Target, Pet, Room, etc.)
- **Integration Tests**: Command execution, turn management, visibility with pet
- **System Tests**: Complete gameplay scenarios

### Key Test Areas
- Player capacity enforcement
- Attack visibility checking
- Pet blocking line of sight
- DFS traversal correctness
- Win condition detection
- Turn limit enforcement

---

## 11. Design Decisions

### Command Pattern Implementation

**Why Command Pattern?**
- Decouples controller from specific action implementations
- Makes adding new commands easy (just create new Command class and register in factory)
- Enables future features like undo/redo or command logging
- Simplifies testing (each command can be tested independently)

**How it works**:
```
User Input → Controller → Command Factory → Command Object → Model Method → Result
```

Example:
```
"attack Alice Knife" → createAttackCommand() → AttackCommand → model.attackTarget() → "⚔️ Alice attacked..."
```

### Pet Visibility Rules

The pet makes its space **invisible to neighbors** (not visible spaces in general):
- Space A is neighbor of Space B if they share an edge
- If pet is in Space B, Space A cannot see into Space B
- This is different from the axis-aligned visibility used for distant spaces

### DFS Pet Wandering (Extra Credit)

The pet follows a depth-first traversal pattern:
1. Starts at space 0
2. Explores one neighbor fully before moving to the next
3. Backtracks when no unvisited neighbors remain
4. Restarts from space 0 after visiting all spaces

**Implementation**:
- Uses a stack to track the traversal path
- Maintains a visited set to avoid cycles
- Moves automatically after each turn ends

---

## 12. Generating Example Runs

### Method 1: Manual Input with Console Save

**In Eclipse**:
1. Run → Run Configurations
2. Set Arguments: `mansion.txt 100`
3. Run the program
4. Manually enter commands
5. Right-click Console → Save Console Output → `run_basics.txt`

**In Command Line**:
```bash
# Linux/Mac
java -jar KillDrLucky.jar mansion.txt 100 | tee run_basics.txt

# Windows PowerShell  
java -jar KillDrLucky.jar mansion.txt 100 | Tee-Object run_basics.txt
```

### Method 2: Automated Input from File (Recommended)

**Create input files**:

`input_basics.txt`:
```
add Alice 0 false 5
add Bob 1 false 5
add AIBot 8 true 5
start
look Alice
space Armory
movepet Billiard Room
look Alice
move Alice Drawing Room
look Alice
move Bob Armory
attack Alice
move Bob Billiard Room
attack Alice
quit
```

`input_ai_wins.txt`:
```
add SuperBot 0 true 10
start
quit
```

`input_escape.txt`:
```
add Alice 10 false 3
start
look Alice
look Alice
quit
```

**Generate output files**:
```bash
# Generate all three scenario files
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt 2>&1
java -jar KillDrLucky.jar mansion.txt 200 < input_ai_wins.txt > run_ai_wins.txt 2>&1
java -jar KillDrLucky.jar mansion.txt 3 < input_escape.txt > run_target_escapes.txt 2>&1
```

### Method 3: Batch Script (Most Efficient)

**Windows** (`generate_runs.bat`):
```batch
@echo off
echo Generating all example runs...
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt 2>&1
java -jar KillDrLucky.jar mansion.txt 200 < input_ai_wins.txt > run_ai_wins.txt 2>&1
java -jar KillDrLucky.jar mansion.txt 3 < input_escape.txt > run_target_escapes.txt 2>&1
echo Done! Check run_*.txt files.
pause
```

**Linux/Mac** (`generate_runs.sh`):
```bash
#!/bin/bash
echo "Generating all example runs..."
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt 2>&1
java -jar KillDrLucky.jar mansion.txt 200 < input_ai_wins.txt > run_ai_wins.txt 2>&1
java -jar KillDrLucky.jar mansion.txt 3 < input_escape.txt > run_target_escapes.txt 2>&1
echo "Done! Check run_*.txt files."
```

Run with:
```bash
# Windows
generate_runs.bat

# Linux/Mac
chmod +x generate_runs.sh
./generate_runs.sh
```

---

## 13. Example Run Scenarios

### Scenario Coverage

The provided example runs demonstrate all required features:

| Scenario | Description                                  | File                     |
| -------- | -------------------------------------------- | ------------------------ |
| 1        | Pet blocking visibility from neighbors       | `run_basics.txt`         |
| 2        | Player moving the pet                        | `run_basics.txt`         |
| 3        | Human player attacking target                | `run_basics.txt`         |
| 4        | Computer player attacking target             | `run_ai_wins.txt`        |
| 5        | Human player winning the game                | `run_basics.txt`         |
| 6        | Computer player winning the game             | `run_ai_wins.txt`        |
| 7        | Target escaping (max turns reached)          | `run_target_escapes.txt` |
| 8        | Pet DFS wandering (Extra Credit)             | `run_basics.txt`         |

### Sample Output Snippets

**Pet blocking visibility**:
```
look Alice
═══════════════════════════════════════════
👀 Looking around from: Armory
═══════════════════════════════════════════

🚪 Neighboring Spaces:
  • Billiard Room [1] - 🐾 Cannot see inside (pet is blocking view)
```

**Player moving pet**:
```
movepet Billiard Room
🐾 Moved Fortune the Cat from Armory to Billiard Room
```

**Human player attacking**:
```
attack Alice
⚔️ Alice attacked Doctor Lucky with a poke in the eye for 1 damage!
💚 Target health remaining: 1
```

**Human player winning**:
```
attack Alice
🎉🎉🎉 Alice WINS! 🎉🎉🎉
Alice killed Doctor Lucky with a poke in the eye for 1 damage!
The target is dead!
```

**Pet DFS wandering**:
```
Turn 3 | Player: Alice
...
→ Target moved to: Drawing Room
🐾 Fortune the Cat wandered to: Dining Hall

Turn 4 | Player: Bob
...
→ Target moved to: Foyer
🐾 Fortune the Cat wandered to: Trophy Room
```

---

## 14. Project Structure
```
KillDrLucky/
├── src/
│   └── killdrlucky/
│       ├── Driver.java                  # Entry point
│       ├── World.java                   # Main game model
│       ├── GameController.java          # MVC Controller
│       ├── Player.java                  # Human player
│       ├── ComputerPlayer.java          # AI player
│       ├── Target.java                  # Dr. Lucky
│       ├── Pet.java                     # Target's pet (NEW)
│       ├── Room.java                    # Space implementation
│       ├── Weapon.java                  # Item implementation
│       ├── Command.java                 # Command interface (NEW)
│       ├── MoveCommand.java             # Move action (NEW)
│       ├── PickUpCommand.java           # Pickup action (NEW)
│       ├── LookAroundCommand.java       # Look action (NEW)
│       ├── AttackCommand.java           # Attack action (NEW)
│       ├── MovePetCommand.java          # Move pet action (NEW)
│       ├── AddPlayerCommand.java        # Add player (NEW)
│       ├── DescribePlayerCommand.java   # Info query (NEW)
│       ├── DescribeSpaceCommand.java    # Info query (NEW)
│       ├── AttackStatus.java            # Attack result enum
│       ├── WorldParser.java             # File parser
│       ├── AxisAlignedVisibility.java   # Visibility strategy
│       └── ...                          # Interfaces and utilities
├── res/
│   ├── mansion.txt                      # World specification
│   ├── input_basics.txt                 # Input commands
│   ├── run_basics.txt                   # Example run output
│   └── world_map.png                    # Generated map
├── KillDrLucky.jar                      # Executable JAR
└── README.md                            # This file
```

---

## 15. Limitations and Assumptions

- **Text-based interface**: Graphical UI planned for future milestone
- **Single-threaded**: No concurrent player actions
- **File-based world**: World must be loaded from specification file
- **No save/load**: Game state cannot be saved mid-game
- **Linear turn order**: Players act in the order they were added

---

## 16. Extra Credit Implementation

### DFS Pet Wandering

The pet automatically moves after each turn following a **depth-first traversal** pattern:

**Algorithm**:
1. Use a stack to track the current traversal path
2. Mark visited spaces to avoid revisiting
3. At each step:
   - If current space has unvisited neighbors → push them onto stack, move to top
   - If no unvisited neighbors → pop stack (backtrack)
   - If stack is empty → restart DFS from space 0

**Evidence in output**:
```
Turn 1: Pet moves to space 2 (neighbor of 0)
Turn 2: Pet moves to space 5 (neighbor of 2)
Turn 3: Pet moves to space 8 (neighbor of 5)
Turn 4: Pet backtracks to space 5 (no unvisited neighbors from 8)
...
```

This creates a systematic traversal pattern rather than random movement, ensuring the pet eventually visits all spaces before restarting.

---

## 17. References

The following references were used during the design and implementation:

- **Java SE 17 API Documentation** – for `List`, `Stack`, `Set`, `Path`, `BufferedImage`, and I/O classes
- **Design Patterns: Elements of Reusable Object-Oriented Software** (Gang of Four) – for Command and Strategy patterns
- **Depth-First Search Algorithm** – standard graph traversal algorithm for pet wandering
- **Stack Overflow** – for troubleshooting JAR manifest issues and console output redirection
- **Eclipse Documentation** – for creating runnable JAR files and run configurations

---

## 18. Author Information

**Course**: CS 5010 - Program Design Paradigms  
**Milestone**: 3  
**Version**: 3.0  
**Date**: November 2024

---

## 19. Changes from Previous Milestones

### Milestone 2 → Milestone 3

**Added**:
- Pet class and parsing
- Attack mechanics with visibility checking
- Win/lose conditions
- Command pattern implementation
- MovePet and Attack commands
- Enhanced look around with neighbor details
- DFS pet wandering (extra credit)
- Updated AI to prioritize attacking

**Modified**:
- `visibleFrom()` now accounts for pet location
- `describeSpace()` includes pet and more details
- `lookAround()` shows neighboring space contents
- `autoAction()` includes attack logic

**Improved**:
- Better error messages
- More informative console output
- Clearer turn progression indicators

---

## 20. Quick Start Guide
```bash
# 1. Compile and create JAR in Eclipse (see section 4)

# 2. Verify JAR works
java -jar KillDrLucky.jar mansion.txt 50

# 3. Play interactively
# Enter commands like: add Alice 0 false 5, start, look Alice, etc.

# 4. Or generate example runs automatically
java -jar KillDrLucky.jar mansion.txt 100 < input_basics.txt > run_basics.txt
```

**That's it!** You're ready to play Kill Dr Lucky! 🎉