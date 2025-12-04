# Kill Dr Lucky – Graphical User Interface (Milestone 4)

## 1. Overview

This milestone completes the Kill Doctor Lucky game by implementing a full **graphical user interface (GUI)** using Java Swing. The game maintains all features from previous milestones while adding an intuitive point-and-click interface with keyboard shortcuts. The system continues to follow the Model–View–Controller (MVC) architecture with clear separation between layers.

### New Features in Milestone 4

- **Welcome Screen**: Displays game title, creator credits, and external resources
- **Interactive GUI**: Point-and-click interface with visual world representation
- **Menu System**: JMenu for starting new games, restarting, and quitting
- **Graphical World Map**: Visual representation of all spaces with overlaid characters
- **Mouse Controls**: Click on spaces to move, click on players to view information
- **Keyboard Shortcuts**: P (pickup), L (look), A (attack), M (move pet)
- **Scrollable View**: Supports large worlds exceeding screen size
- **Real-time Updates**: Status bar shows current player, location, and target health
- **Dual Mode Support**: Both text-based and GUI modes available

### Core Gameplay

Players interact through the GUI by:

- **Clicking** on neighboring spaces to move
- **Clicking** on player icons to view detailed information
- **Pressing P** to pick up items in current space
- **Pressing L** to look around and see neighboring spaces
- **Pressing A** to attack the target (when in same space)
- **Pressing M** to move the pet to a different space

The game ends when:
- A player successfully kills the target character (that player wins)
- Maximum number of turns is reached (target escapes, no winner)

---

## 2. Architecture Overview

### Layer Responsibilities

| Layer          | Components                                                                                          | Description                                                                                  |
| -------------- | --------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| **Model**      | `World`, `Room`, `Item`, `Target`, `Pet`, `Player`, `ComputerPlayer`, `ActionResult`, `GameState`   | Represents game state and logic. Returns structured results instead of exposing objects.     |
| **View**       | `GameView`, `WorldPanel`, `GameViewInterface`, `WorldPanelInterface`                                | Swing-based GUI with welcome screen, game panel, and message display.                        |
| **Controller** | `GuiController`, `ControllerInterface`                                                               | Handles user input events, coordinates Model and View, manages game flow.                    |
| **Utility**    | `WorldParser`, `Rect`, `Point`, `VisibilityStrategy`, `AxisAlignedVisibility`, `AttackStatus`       | Parsing, geometry, visibility calculation, and enumerations.                                  |

### Design Patterns Used

- **Model–View–Controller (MVC)** – Complete separation between game logic, display, and control flow
- **Strategy Pattern** – Visibility algorithms via `VisibilityStrategy`
- **Observer Pattern** – View observes Model through `ReadOnlyWorld` interface
- **Dependency Inversion** – All layers depend on interfaces, not concrete implementations
- **Information Hiding** – Model returns `ActionResult` and `GameState` instead of exposing internal objects

---

## 3. List of Features

### Milestone 1-3 Features (All Preserved)
- Parse world specification file
- Add multiple players (human and computer)
- Move, pickup, look around mechanics
- Pet system affecting visibility
- Attack mechanics with visibility checking
- Win conditions and turn limits
- DFS pet wandering

### Milestone 4 Features (New)
- **Welcome Screen**: Credits creator and lists external resources
- **JMenu System**: File menu with New Game, Restart, and Quit options
- **Graphical World Map**: Visual representation with colored spaces
- **Character Overlays**: Target and players (up to 10) displayed on map
- **Mouse Click Movement**: Click neighboring spaces to move
- **Player Information**: Click player icons to view details
- **Keyboard Shortcuts**: P, L, A, M keys for actions
- **Scrollable Viewport**: Handles worlds larger than screen
- **Status Bar**: Shows current turn, player, location, and target health
- **Message Log**: Scrollable text area displaying action results
- **Resizable Window**: Maintains layout from 300x300 to full screen
- **Error Dialogs**: User-friendly error messages with retry options
- **Input Validation**: Prevents invalid player counts, duplicate names, etc.

---

## 4. How to Run

### Prerequisites

- Java 17 or higher
- `KillDrLucky.jar` (executable JAR file)
- World specification file (e.g., `mansion.txt`)

### Creating the JAR File (Eclipse)

1. **Ensure GuiDriver.java can run**:
   - Right-click `GuiDriver.java` → **Run As** → **Java Application**
   
2. **Export as Runnable JAR**:
   - Right-click project → **Export...**
   - Select **Java** → **Runnable JAR file** → **Next**
   
3. **Configure export**:
   - Launch configuration: `GuiDriver - KillDrLucky`
   - Export destination: `/path/to/KillDrLucky.jar`
   - Library handling: **Extract required libraries into generated JAR**
   - Click **Finish**

4. **Verify JAR**:
```bash
   java -jar KillDrLucky.jar mansion.txt 50
```

### Command-Line Execution

#### GUI Mode (Default)

**If JAR is in project root**:
```bash
java -jar KillDrLucky.jar <worldFile> <maxTurns>
```

**If JAR is in res/ folder** (recommended):
```bash
java -jar res/KillDrLucky.jar <worldFile> <maxTurns>
```

**Examples**:
```bash
# JAR in root directory
java -jar KillDrLucky.jar mansion.txt 50

# JAR in res/ folder (your setup)
java -jar res/KillDrLucky.jar res/mansion.txt 50

# JAR in res/, using relative path for world file
java -jar res/KillDrLucky.jar mansion.txt 50

# With explicit paths
java -jar res/KillDrLucky.jar res/mansion.txt 100
```

**What happens**:
1. GUI window launches
2. Welcome screen displays with credits
3. Click "Start New Game" to begin
4. Dialogs prompt for player setup
5. Game screen appears with interactive world map

#### Text-Based Mode (Legacy, Optional)
```bash
# Run text version (if you want the old console mode)
java -cp res/KillDrLucky.jar killdrlucky.Driver res/mansion.txt 50

# Or if JAR is in root
java -cp KillDrLucky.jar killdrlucky.Driver mansion.txt 50
```

### Arguments

| Argument       | Description                                                    | Default          |
| -------------- | -------------------------------------------------------------- | ---------------- |
| `<worldFile>`  | Path to world specification file (e.g., `mansion.txt` or `res/mansion.txt`) | `res/mansion.txt`|
| `<maxTurns>`   | Maximum number of turns before game ends                       | `50`             |

### File Path Notes

- If JAR is in `res/` folder, use `java -jar res/KillDrLucky.jar`
- World file path can be relative (`mansion.txt`) or absolute (`res/mansion.txt`)
- Recommended structure:
  ```
  project/
  ├── res/
  │   ├── KillDrLucky.jar
  │   └── mansion.txt
  ```
  Run with: `java -jar res/KillDrLucky.jar res/mansion.txt 50`

---

## 5. GUI User Guide

### Starting a New Game

1. **Launch Application**: Run the JAR file
2. **Welcome Screen**: Read game information and credits
3. **Click "Start New Game"** or use File → New Game
4. **Enter Player Count**: Input number of players (1-10)
5. **Setup Each Player**:
   - Enter player name
   - Select starting space (0 to max space index)
   - Choose human or computer controlled
6. **Game Begins**: World map appears with all players

### Playing the Game

#### Mouse Controls
- **Click neighboring space**: Move current player to that space
- **Click player icon**: View player's detailed information

#### Keyboard Controls
- **P key**: Pick up an item in current space (shows item selection dialog)
- **L key**: Look around current and neighboring spaces
- **A key**: Attack the target (only when in same space)
- **M key**: Move the pet to a different space

#### Menu Options
- **File → New Game (New World)**: Start fresh with different world file
- **File → Restart Game (Current World)**: Restart with same world, new players
- **File → Quit**: Exit application

### Understanding the Display

#### World Map
- **Colored rectangles**: Represent spaces/rooms
- **Space labels**: Show room name and index number [0]
- **Red circle with 'T'**: Target character (Dr. Lucky)
- **Green circles**: Human players (labeled with first initial)
- **Blue circles**: Computer players (labeled with first initial)
- **Multiple players in same space**: Offset horizontally

#### Status Bar (Top)
Shows: `Turn X/Y | Player: Name (Type) | Location: SpaceName | Target Health: Z`

#### Message Area (Bottom)
- Scrollable log of all actions and results
- Color-coded feedback (success/error messages)
- Action history for reference

---

## 6. Available Actions

### Movement
- **How**: Click on a neighboring space
- **Requirements**: Must be adjacent (shares edge)
- **Result**: Player moves to new space, turn advances

### Pick Up Item
- **How**: Press P key
- **Requirements**: Items must exist in current space, inventory not full
- **Result**: Dialog shows available items, selected item added to inventory

### Look Around
- **How**: Press L key
- **Requirements**: None
- **Result**: Message area shows detailed information about current and visible neighboring spaces

### Attack Target
- **How**: Press A key
- **Requirements**: Must be in same space as target, not seen by others
- **Result**: Dialog prompts for weapon choice, damage dealt if successful

### Move Pet
- **How**: Press M key
- **Requirements**: None
- **Result**: Prompt for space name, pet moves to specified location

### View Player Info
- **How**: Click on player icon on map
- **Requirements**: None
- **Result**: Player's information displayed in message area

---

## 7. Model Refactoring (Milestone 3 → Milestone 4)

### Changes Made to Model

To separate the text-based controller logic from the model, we moved all game state validation and action execution logic into the model layer. Previously, the `GameController` handled command parsing and directly called model methods like `movePlayer()`, `pickUpItem()`, `lookAround()`, and `attackTarget()` which returned simple strings. The controller was responsible for determining turn advancement and checking win conditions. 

Now, we've enhanced the model with new methods that return structured `ActionResult` objects: `executeAction()` which handles all action types internally. We also added turn management methods `getGameState()` and `advanceTurn()` to the `GameModelApi` interface. The model now maintains a `currentPlayerIndex` to track whose turn it is and provides complete game state management through the immutable `GameState` class. This refactoring allows the same model to work with both text-based and GUI controllers without code duplication, as all game logic resides in the model layer where it belongs.

### New Model Components

#### `ActionResult` Class
Structured result object returned by all game actions:
- `success`: Whether action succeeded
- `message`: User-friendly result description
- `isTurnAction`: Whether action consumes a turn

#### `GameState` Class
Immutable snapshot of current game state:
- Current player information (name, type, location)
- Target information (location, health)
- Pet location
- Turn count
- Game over status and winner

These classes prevent the Controller and View from accessing internal Model objects directly.

---

## 8. GUI Design

### Welcome Screen Layout

```
┌──────────────────────────────────────┐
│                                      │
│     KILL DOCTOR LUCKY                │
│   A Strategic Board Game             │
│                                      │
│   Created by: [Your Name]            │
│                                      │
│   Resources Used:                    │
│   • Java Swing GUI Framework         │
│   • MVC Design Pattern               │
│                                      │
│      [Start New Game]                │
│                                      │
│   Click: Move | P: Pick | L: Look   │
│   A: Attack | M: Move pet            │
│                                      │
└──────────────────────────────────────┘
```

### Main Game Screen Layout

```
┌──────────────────────────────────────────────────┐
│ File  Help                                       │
├──────────────────────────────────────────────────┤
│ Turn 5/50 | Player: Alice (Human) | Location:    │
│ Kitchen | Target Health: 45                      │
├──────────────────────────────────────────────────┤
│                                                  │
│  ┌────────────────────────────────────────┐    │
│  │         Game World (Scrollable)        │    │
│  │                                        │    │
│  │   ┌─────────┬─────────┬─────────┐    │    │
│  │   │Kitchen  │ Hallway │ Library │    │    │
│  │   │  [0]    │   [1]   │   [2]   │    │    │
│  │   │  A T    │         │    B    │    │    │
│  │   └─────────┴─────────┴─────────┘    │    │
│  │                                        │    │
│  └────────────────────────────────────────┘    │
│                                                  │
├──────────────────────────────────────────────────┤
│ Messages:                                        │
│ Alice moved to Kitchen                           │
│ Bob picked up Knife                              │
│ Alice attacked target for 3 damage!              │
└──────────────────────────────────────────────────┘

Legend: T=Target, A/B=Players (Green=Human, Blue=AI)
```

---

## 9. Class Diagram Summary

### Model Layer (Enhanced)
- **World**: Implements `GameModelApi`, `ReadOnlyWorld`, `WorldModel`
  - New field: `currentPlayerIndex` (tracks turn)
  - New field: `winnerName` (tracks winner)
  - New method: `executeAction()` (unified action handler)
  - New method: `getGameState()` (returns state snapshot)
  - New method: `advanceTurn()` (advances turn counter)
- **ActionResult**: Encapsulates action execution results
- **GameState**: Immutable game state snapshot

### View Layer (New)
- **GameViewInterface**: Defines view contract
  - `setClickListener()`, `setKeyListener()`
  - `updateStatus()`, `addMessage()`
  - `refresh()`, `promptInput()`, `showMessage()`
- **GameView**: Main JFrame implementation
  - Welcome panel with CardLayout
  - Game panel with world rendering
  - Status bar and message area
  - Menu bar with File menu
- **WorldPanelInterface**: Defines panel contract
  - `getSpaceAt()`, `refresh()`
- **WorldPanel**: JPanel for world rendering
  - Draws spaces, target, and players
  - Handles coordinate translation
  - Supports scrolling for large worlds

### Controller Layer (Redesigned)
- **ControllerInterface**: Defines controller contract
  - `handleClick()`, `handleKey()`
  - `executeAction()`, `updateView()`
- **GuiController**: Implements Features interface
  - Registers event listeners
  - Converts user actions to model calls
  - Manages game flow and turn progression
  - Auto-plays computer turns with Timer

### Interface Dependencies

```
Model (GameModelApi) ← Controller → View (GameViewInterface)
        ↓                               ↓
   ReadOnlyWorld                   WorldPanel
```

- Model **does not** depend on View or Controller
- Controller depends on **interfaces** only
- View observes Model through **ReadOnlyWorld** (read-only access)

---

## 10. How It Works

### Game Startup Flow

```
1. GuiDriver.main()
   ↓
2. Parse world file → Create World model
   ↓
3. Create GameView (shows welcome screen)
   ↓
4. Create GuiController(model, view, maxTurns)
   ↓
5. Controller registers listeners on view
   ↓
6. User clicks "Start New Game"
   ↓
7. Dialogs prompt for player setup
   ↓
8. View switches to game screen
   ↓
9. Game begins with first player's turn
```

### Turn Execution Flow

```
Human Player Turn:
  User clicks space → MouseListener → handleClick()
    ↓
  Controller validates neighbor → model.executeAction("move", ...)
    ↓
  Model returns ActionResult → Controller displays message
    ↓
  If turn action: moveTarget(), movePetDfs(), advanceTurn()
    ↓
  Controller calls updateView() → refresh display
    ↓
  Check if next player is AI → auto-execute if so

Computer Player Turn:
  Controller detects AI player → schedules Timer (1 second delay)
    ↓
  Timer fires → model.autoAction()
    ↓
  Display AI action result → advance turn
    ↓
  Chain to next AI player if applicable
```

### Data Flow Example: Moving a Player

```
1. User clicks neighboring space at pixel (150, 200)
2. GamePanel.getSpaceAt(150, 200) → returns spaceIndex=5
3. GuiController.handleClick(150, 200)
4. Controller gets GameState (doesn't access Player object directly)
5. Controller validates: neighborsOf(currentPlayerSpace).contains(5)
6. Controller calls: model.executeAction("Alice", "move", "Kitchen")
7. Model executes: movePlayer() internally
8. Model returns: ActionResult(success=true, "Alice moved to Kitchen", isTurnAction=true)
9. Controller updates view: view.addMessage(result.message)
10. Controller advances game: moveTarget(), movePetDfs(), advanceTurn()
11. View refreshes: worldPanel.repaint()
```

---

## 11. Key Design Decisions

### Why ActionResult Instead of Returning Objects?

**Problem**: Controller shouldn't directly access Player or Target objects (breaks encapsulation)

**Solution**: Model returns `ActionResult` with:
- Success/failure status
- User-friendly message
- Whether action consumed a turn

**Benefits**:
- Controller doesn't need to know internal Model structure
- Easy to add new return information without changing interfaces
- Type-safe communication between layers

### Why GameState Instead of getCurrentPlayer()?

**Problem**: Returning `Iplayer` object exposes mutable internal state

**Solution**: Model returns immutable `GameState` snapshot with:
- Player name, type, location (not the object itself)
- Target/pet locations and health
- Game status

**Benefits**:
- View/Controller cannot accidentally modify Model state
- Clear data contract between layers
- Thread-safe (immutable)

### Why No Command Pattern in GUI Controller?

**Text-based Controller** (Milestone 3):
```
"move Alice Kitchen" → Parse → CreateCommand → Execute
```
Needs Command pattern for parsing complex text

**GUI Controller** (Milestone 4):
```
Click event → handleClick() → model.executeAction()
```
Direct event handling is simpler and more appropriate

### GUI vs Text Mode Support

Both modes supported through **same Model**:
- `GameController` (text) → uses Commands
- `GuiController` (GUI) → uses event handlers
- Both use same `GameModelApi` interface

---

## 12. GUI Components

### GameView Components

| Component | Type | Purpose |
|-----------|------|---------|
| `welcomePanel` | JPanel | Shows title, credits, start button |
| `gamePanel` | JPanel | Contains world rendering and status |
| `worldPanel` | WorldPanel | Renders world map with characters |
| `statusLabel` | JLabel | Displays current turn info |
| `messageArea` | JTextArea | Scrollable action log |
| `menuBar` | JMenuBar | File menu with New/Restart/Quit |

### Layout Managers Used

| Panel | Layout | Reason |
|-------|--------|--------|
| Main frame | CardLayout | Switch between welcome/game screens |
| Welcome panel | GridBagLayout | Center components with spacing |
| Game panel | BorderLayout | Status (North), World (Center), Messages (South) |
| World panel | Custom painting | Precise control over world rendering |

### Resizing Behavior

- **Minimum size**: 300x300 pixels (enforced)
- **World panel**: Uses preferred size based on world dimensions
- **Scrolling**: Automatic when world exceeds viewport
- **Layout**: BorderLayout ensures components resize proportionally

---

## 13. Testing

### Test Coverage

#### Model Tests (No Mocks)
- `ActionResultTest`: 7 tests for ActionResult class
- `GameStateTest`: 5 tests for GameState class
- `WorldExecuteActionTest`: 11 tests for executeAction() method
- `WorldGameStateTest`: 9 tests for getGameState() method
- `WorldAdvanceTurnTest`: 6 tests for advanceTurn() method
- `WorldPlayerLimitTest`: 5 tests for 10-player maximum
- `WorldWinnerTrackingTest`: 3 tests for winner recording

**Total Model Tests**: 46 tests

#### Controller Tests (With Manual Mocks)
- `MockGameModel`: Manual mock implementation of GameModelApi
- `MockGameView`: Manual mock implementation of GameViewInterface
- `MockWorldPanel`: Manual mock for WorldPanel
- `GuiControllerManualMockTest`: Tests controller in isolation

**Total Controller Tests**: 10+ tests

#### View Tests
Not required per assignment specifications.

### Running Tests

```bash
# Compile tests
javac -cp .:junit-4.13.2.jar -d bin test/killdrlucky/*.java src/killdrlucky/*.java

# Run all model tests
java -cp bin:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
  killdrlucky.ActionResultTest \
  killdrlucky.GameStateTest \
  killdrlucky.WorldExecuteActionTest \
  killdrlucky.WorldGameStateTest \
  killdrlucky.WorldAdvanceTurnTest \
  killdrlucky.WorldPlayerLimitTest \
  killdrlucky.WorldWinnerTrackingTest

# Run controller tests
java -cp bin:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
  killdrlucky.GuiControllerManualMockTest
```

---

## 14. Example Gameplay Scenarios

### Scenario 1: Human Player Wins

```
1. Start game with Alice (human) at space 0
2. Click neighboring space to move toward target
3. Press L to look around and locate target
4. Navigate to target's space
5. Press P to pick up a weapon
6. Press A to attack with weapon
7. Repeat attacks until target health reaches 0
8. Game Over dialog: "Winner: Alice"
```

### Scenario 2: Computer Player Wins

```
1. Start game with AI1 (computer) at space 0
2. AI automatically takes turns every 1 second
3. AI moves toward target, picks up items
4. AI attacks when in same space and not seen
5. AI wins after dealing enough damage
6. Game Over dialog: "Winner: AI1"
```

### Scenario 3: Target Escapes

```
1. Start game with maxTurns=10
2. Players take 10 turns without killing target
3. Turn counter reaches 10/10
4. Game Over dialog: "Target ESCAPED! Remaining health: X"
```

### Scenario 4: Mixed Players

```
1. Add Alice (human), Bob (human), AI1 (computer)
2. Turns alternate: Alice → Bob → AI1 → Alice → ...
3. Human turns: wait for user input
4. AI turns: auto-execute after 1 second delay
5. Game continues until win condition met
```

---

## 15. Input Validation

The GUI provides robust error handling:

| Invalid Input | Detection | User Feedback |
|---------------|-----------|---------------|
| Player count > 10 | On input | Error dialog: "Must be between 1 and 10" |
| Player count < 1 | On input | Error dialog |
| Non-numeric player count | On input | Error dialog: "Invalid input" |
| Duplicate player name | On add | Error dialog: "Player already exists", retry |
| Invalid space index | On input | Error dialog with valid range, retry |
| Non-numeric space index | On input | Error dialog, retry |
| Empty player name | On input | Error dialog, retry |
| Click non-neighbor | On click | Message: "Not a neighbor!" |
| Attack from different room | On 'A' key | Dialog: "Target not in same room" |
| Pickup with no items | On 'P' key | Message: "No items here" |
| Invalid space name (move pet) | On execute | Error message displayed |

---

## 16. Project Structure
```
KillDrLucky/
├── src/
│   └── killdrlucky/
│       ├── Driver.java                    # Text-mode entry point
│       ├── GuiDriver.java                 # GUI entry point (NEW)
│       ├── World.java                     # Main game model (MODIFIED)
│       ├── GameModelApi.java              # Model interface (MODIFIED)
│       ├── ActionResult.java              # Result class (NEW)
│       ├── GameState.java                 # State snapshot (NEW)
│       ├── GameController.java            # Text controller (PRESERVED)
│       ├── GuiController.java             # GUI controller (NEW)
│       ├── ControllerInterface.java       # Controller interface (NEW)
│       ├── GameView.java                  # Main view (NEW)
│       ├── GameViewInterface.java         # View interface (NEW)
│       ├── WorldPanel.java                # World renderer (NEW)
│       ├── WorldPanelInterface.java       # Panel interface (NEW)
│       ├── Player.java                    # Human player
│       ├── ComputerPlayer.java            # AI player
│       ├── Target.java                    # Dr. Lucky
│       ├── Pet.java                       # Target's pet
│       ├── Room.java                      # Space implementation
│       ├── Weapon.java                    # Item implementation
│       ├── Command.java                   # Command interface
│       ├── MoveCommand.java               # Move action
│       ├── PickUpCommand.java             # Pickup action
│       ├── LookAroundCommand.java         # Look action
│       ├── AttackCommand.java             # Attack action
│       ├── MovePetCommand.java            # Move pet action
│       ├── AddPlayerCommand.java          # Add player
│       ├── DescribePlayerCommand.java     # Info query
│       ├── DescribeSpaceCommand.java      # Info query
│       ├── AttackStatus.java              # Attack result enum
│       ├── WorldParser.java               # File parser
│       ├── ReadOnlyWorld.java             # Read-only interface (MODIFIED)
│       ├── AxisAlignedVisibility.java     # Visibility strategy
│       └── ...                            # Interfaces and utilities
├── test/
│   └── killdrlucky/
│       ├── ActionResultTest.java          # Test ActionResult (NEW)
│       ├── GameStateTest.java             # Test GameState (NEW)
│       ├── WorldExecuteActionTest.java    # Test executeAction (NEW)
│       ├── WorldGameStateTest.java        # Test getGameState (NEW)
│       ├── WorldAdvanceTurnTest.java      # Test advanceTurn (NEW)
│       ├── WorldPlayerLimitTest.java      # Test 10-player limit (NEW)
│       ├── WorldWinnerTrackingTest.java   # Test winner tracking (NEW)
│       ├── MockGameModel.java             # Mock for testing (NEW)
│       ├── MockGameView.java              # Mock for testing (NEW)
│       ├── MockWorldPanel.java            # Mock for testing (NEW)
│       └── GuiControllerManualMockTest.java # Controller tests (NEW)
├── res/
│   ├── mansion.txt                        # World specification
│   └── world_map.png                      # Generated map
├── design/
│   ├── milestone4_uml.png                 # UML class diagrams (NEW)
│   └── ui_sketches.png                    # UI mockups (NEW)
├── KillDrLucky.jar                        # Executable JAR
└── README.md                              # This file
```

---

## 17. Design Comparison: Text vs GUI

### Text-Based Controller (Milestone 3)

```java
// Uses Command Pattern with Factory
String input = "move Alice Kitchen";
Scanner parser = new Scanner(input);
String commandName = parser.next();

Command cmd = commandFactory.get(commandName).apply(parser);
String result = cmd.execute();  // Returns String
out.append(result);

if (cmd.isTurnAction()) {
    model.moveTarget();
    turnCount++;
}
```

### GUI Controller (Milestone 4)

```java
// Direct event handling
public void handleClick(int x, int y) {
    int spaceIdx = worldPanel.getSpaceAt(x, y);
    GameState state = model.getGameState();  // Get state snapshot
    
    if (neighbors.contains(spaceIdx)) {
        ActionResult result = model.executeAction(
            state.currentPlayerName, "move", spaceName);
        
        view.addMessage(result.message);
        
        if (result.isTurnAction()) {
            model.moveTarget();
            model.advanceTurn();
            updateView();
        }
    }
}
```

**Key Difference**: GUI uses direct event handlers instead of parsing text commands.

---

## 18. Example Runs

### GUI Mode Screenshots

Due to the graphical nature of Milestone 4, example runs are demonstrated through:

1. **Welcome Screen**: Shows on startup
2. **Player Setup**: Dialog sequence for adding players
3. **Active Gameplay**: World map with characters
4. **Game Over**: Victory or defeat dialog

### Demonstrating All Requirements

The GUI implementation demonstrates:

- ✅ Welcome screen with credits and resources
- ✅ JMenu with New Game (new world), Restart (current world), Quit
- ✅ Resizable window (300x300 minimum)
- ✅ Graphical world representation
- ✅ Target character overlay (red circle with 'T')
- ✅ Player overlays (green/blue circles, up to 10 players)
- ✅ No pet overlay (per requirement: "but not the pet")
- ✅ Scrollable view for large worlds
- ✅ Status bar showing current turn and player info
- ✅ Click player icon for description
- ✅ Click space to move (validates neighbors)
- ✅ Keyboard shortcuts: P, L, A, M
- ✅ Clear action result messages

---

## 19. Differences from Previous Milestones

### Milestone 3 → Milestone 4

**Added (Model)**:
- `ActionResult` class for structured results
- `GameState` class for state snapshots
- `executeAction()` unified action handler
- `getGameState()` for state queries
- `advanceTurn()` for turn management
- `winnerName` field for tracking winner
- 10-player maximum enforcement

**Added (View)**:
- Complete Swing GUI framework
- `GameView` with welcome and game screens
- `WorldPanel` for rendering world
- Menu bar with File menu
- Keyboard bindings for actions
- Mouse event handling
- Dialog prompts for user input

**Added (Controller)**:
- `GuiController` for GUI event handling
- `ControllerInterface` for abstraction
- Automatic computer turn execution
- Timer-based turn chaining
- Enhanced game over messages

**Modified**:
- `ReadOnlyWorld` interface (added `getPet()`, `getPlayers()`)
- `GameModelApi` interface (added new methods)
- `World` class (implemented new methods)

**Preserved**:
- All Milestone 1-3 functionality
- Text-based mode still available
- All Command classes
- All game rules and mechanics

---

## 20. External Resources Used

The following resources were used during development:

- **Java SE 17 API Documentation** – for Swing components (`JFrame`, `JPanel`, `JMenu`, `JOptionPane`), layout managers (`BorderLayout`, `CardLayout`, `GridBagLayout`), and event handling (`MouseListener`, `KeyListener`)
- **Design Patterns: Elements of Reusable Object-Oriented Software** (Gang of Four) – for MVC and Observer patterns
- **Java Swing Tutorial** (Oracle) – for GUI best practices and layout management
- **Stack Overflow** – for troubleshooting event listener focus issues and CardLayout usage
- **Eclipse IDE Documentation** – for creating runnable JAR files and debugging GUI applications

---

## 21. Known Limitations

- **Single window**: Only one game can run at a time
- **No undo**: Actions cannot be reversed
- **No save/load**: Game state cannot be saved mid-game
- **Fixed cell size**: World scaling is predetermined (cellSize=30)
- **No animations**: Character movements are instantaneous
- **Timer-based AI**: 1-second delay is hardcoded
- **Simple graphics**: Uses basic shapes and colors

---

## 22. Future Enhancements

Potential improvements for future milestones:

- **Improved graphics**: Custom sprites for characters and items
- **Animations**: Smooth transitions for movements
- **Sound effects**: Audio feedback for actions
- **Network play**: Multiplayer over network
- **Save/load game**: Persist game state to file
- **Replay mode**: Replay recorded games
- **Statistics**: Track player performance over multiple games
- **Difficulty levels**: Adjustable AI intelligence
- **Custom world editor**: GUI tool for creating worlds

---

## 23. Quick Start Guide

### First Time Setup
```bash
# 1. Ensure you have Java 17+
java -version

# 2. Navigate to project directory
cd /path/to/KillDrLucky

# 3. Run GUI version
java -jar res/KillDrLucky.jar res/mansion.txt 50
```

### Alternative: JAR in Root Directory
```bash
# If you move JAR to root
java -jar KillDrLucky.jar mansion.txt 50
```

### Common Run Commands

```bash
# Standard game (50 turns)
java -jar res/KillDrLucky.jar res/mansion.txt 50

# Short game (20 turns)
java -jar res/KillDrLucky.jar res/mansion.txt 20

# Long game (100 turns)
java -jar res/KillDrLucky.jar res/mansion.txt 100

# Using absolute paths
java -jar /full/path/to/res/KillDrLucky.jar /full/path/to/mansion.txt 50
```

### Typical Game Session
```
1. Application launches → Welcome screen appears
2. Click "Start New Game" button
3. Enter number of players (e.g., "2")
4. For each player:
   - Enter name (e.g., "Alice")
   - Enter starting space (e.g., "0")
   - Choose human or computer
5. Game screen appears with world map
6. Click neighboring spaces to move
7. Press P to pick up items
8. Press L to look around
9. Press A to attack when in target's space
10. Game ends when target dies or max turns reached
11. Click "Restart" to play again
```

---

## 24. Troubleshooting

### Common Issues

| Problem | Cause | Solution |
|---------|-------|----------|
| Keyboard not working | Focus lost after click | Click on window, keys should work again |
| Map too large | cellSize too big | Modify `WorldPanel.cellSize` to 20 or 25 |
| Map too small | cellSize too small | Increase `WorldPanel.cellSize` to 40 or 50 |
| Can't see all spaces | World exceeds viewport | Use scrollbars to navigate |
| Player count error | Entered > 10 | Enter value between 1-10 |
| Duplicate name error | Same name twice | Enter unique player names |
| Space index error | Invalid index | Use index from 0 to (spaces-1) |
| Game won't start | Cancelled player setup | Restart and complete all player dialogs |

---

## 25. Video Demonstration

A video demonstration of the GUI functionality has been created showing:

1. Welcome screen with credits
2. Menu system usage
3. Player setup process
4. Mouse-based movement
5. Keyboard shortcuts (P, L, A, M)
6. Clicking player icons for information
7. Computer player automation
8. Game over scenarios (win and escape)
9. Window resizing behavior
10. Restart game functionality

**Video file**: `milestone4_demo.mp4` (included in submission)

---

## 26. Design Document

Complete design documentation included in submission:

- **Design Document PDF**: Contains UML diagrams and design rationale
- **Model UML**: Shows ActionResult, GameState, and interface relationships
- **View UML**: Shows GameView, WorldPanel, and Swing components
- **Controller UML**: Shows GuiController and event handling
- **UI Sketches**: Hand-drawn mockups of welcome and game screens
- **Sequence Diagrams**: Data flow for key interactions

---

## 27. Compliance Checklist

### Required Features

- ✅ Welcome/about screen with creator credits and resources
- ✅ JMenu with New Game (new world), Restart (current world), Quit
- ✅ Resizable JFrame (minimum 300x300)
- ✅ Graphical world representation (majority of screen)
- ✅ Target character overlay
- ✅ Player overlays (supports up to 10 players)
- ✅ No pet overlay (per requirement: "but not the pet")
- ✅ Scrollable view for large worlds
- ✅ Current turn indicator
- ✅ Player location information
- ✅ Click player for description
- ✅ Click space to move
- ✅ Keyboard pickup (P key)
- ✅ Keyboard look around (L key)
- ✅ Keyboard attack (A key)
- ✅ Clear action result display
- ✅ Invalid move prevention

### Design Requirements

- ✅ Model refactored to separate from text controller
- ✅ Model UML with interface isolation
- ✅ UI sketches created
- ✅ View UML with custom classes
- ✅ Controller UML with interfaces
- ✅ Model tested in isolation
- ✅ Controller tested with mocks
- ✅ Text-based mode still supported

---

## 28. Author Information

**Course**: CS 5010 - Program Design Paradigms  
**Milestone**: 4  
**Version**: 4.0  
**Date**: December 2024

---

## 29. Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Oct 2024 | Initial world parsing and map generation |
| 2.0 | Oct 2024 | Added players, movement, and turn system |
| 3.0 | Nov 2024 | Added pet, attacks, win conditions, Command pattern |
| 4.0 | Dec 2024 | Added complete GUI with Swing, refactored Model |

---

**That's it!** You're ready to play Kill Dr Lucky with the new graphical interface! 🎮✨
