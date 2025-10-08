# Kill Dr Lucky – Model Implementation

## 1. Overview

This project implements the **Model** component of the *Kill Doctor Lucky* game as part of the milestone submission.  
The model provides a complete **in-memory representation** of the game world, designed to support both text-based and graphical extensions in future milestones.

### Core Functionalities
The model implements all major requirements specified in the milestone:
- **World representation:**  
  Parsing a world specification file into structured objects (spaces, items, and target characters).  
- **Spatial logic:**  
  Room boundaries, neighbor detection, and overlap validation.  
- **Descriptive queries:**  
  Reporting visible spaces, items contained in each room, and detailed space descriptions.  
- **Target movement:**  
  Simulated stepwise traversal of the target character through the map.  
- **Visibility and attack logic:**  
  Determines whether the target can be seen or attacked from a given location.  
- **Rendering:**  
  Graphical visualization of the world map using `BufferedImage`.

As this milestone focuses solely on the **model layer**, no interactive controller or graphical user interface (GUI) is included.  
A minimal **command-line driver** is provided to demonstrate and verify core functionalities.

---

## 2. System Architecture

The project follows a **modular object-oriented design**, with clear separation between world structure, entity behavior, and parsing logic.

### Key Packages and Classes

| Component | Description |
|------------|-------------|
| **`World`** | The main entry point managing all entities and interactions within the game world. Provides APIs for querying, describing, and rendering. |
| **`Room` / `Space`** | Represent physical areas of the map; each defined by upper-left and lower-right coordinates. Implements `Space` interface. |
| **`Item`** | Represents objects located within rooms, each associated with a damage value. |
| **`Target`** | The special non-player character (NPC) moving through the world according to a fixed path or rule set. |
| **`Rect`** | Utility class encapsulating coordinate geometry for rooms, overlap checking, and adjacency computation. |
| **`WorldParser`** | Responsible for reading the `.txt` world specification file, validating data integrity, and building a `WorldData` aggregate structure. |

---

## 3. How to Run

### Command-Line Execution

```bash
java -jar <spec-file> [--cell <int>] [--out <png>] [--space <idx>]
```

### Example:
```bash
java -jar res/KillDrLucky.jar res/ArrakisPalace.txt --cell 25 --out res/ArrakisPalace.png --space 5 > res/run_basics.txt
```

```
| Argument        | Description                                                                | Default     |
| --------------- | -------------------------------------------------------------------------- | ----------- |
| `<spec-file>`   | Path to the world specification text file (e.g., `mansion.txt`).           | *Required*  |
| `--cell <int>`  | Cell size in pixels for rendering the map. Larger = higher resolution.     | `20`        |
| `--out <path>`  | Path to save the generated map image.                                      | `world.png` |
| `--space <idx>` | Index of the space to demonstrate (`describeSpace()` and `neighborsOf()`). | `0`         |
```

## 4. Example Run

File: res/run_basics.txt

### Purpose:
Demonstrates all functionalities required for the model milestone, including:

- Successful world parsing and validation

- Space description and adjacency detection

- Target character initialization and movement

- Rendering to PNG image

- Correct console output for the requested space and neighbor list

### Comand Used:
```
java -jar res/KillDrLucky.jar res/ArrakisPalace.txt --cell 25 --out res/ArrakisPalace.png --space 5 > res/run_basics.txt
```

### Output Files Generated:
- res/run_basics.txt — textual demonstration log
- res/ArrakisPalace.png — visual map output

This example run shows a complete execution of the model using the ArrakisPalace.txt world specification file.
