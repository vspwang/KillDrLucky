# Kill Dr Lucky – Model Implementation

## Overview
This project implements the **Model** component of the *Kill Dr Lucky* game.  
The model provides a complete in-memory representation of the game world, including:
- World layout (spaces, items, characters)
- Neighbor detection
- Space descriptions (items + visible spaces)
- Target movement logic
- Basic attack and visibility checks
- Graphical rendering of the world map (`BufferedImage`)

As this milestone focuses on the **model layer**, no interactive controller or GUI is included.  
A simple command-line **driver class** is provided to demonstrate the model functionality.

---

## How to Run

### Command-Line
```bash
java -jar <spec-file> [--cell <int>] [--out <png>] [--space <idx>]
java -jar res/KillDrLucky.jar res/ArrakisPalace.txt



| Argument        | Description                                                                | Default     |
| --------------- | -------------------------------------------------------------------------- | ----------- |
| `<spec-file>`   | Path to the world specification text file (e.g., `mansion.txt`).           | *Required*  |
| `--cell <int>`  | Cell size in pixels for rendering the map. Larger = higher resolution.     | `20`        |
| `--out <path>`  | Path to save the generated map image.                                      | `world.png` |
| `--space <idx>` | Index of the space to demonstrate (`describeSpace()` and `neighborsOf()`). | `0`         |
```

## Example Run – run_basic.txt

File: res/run_basic.txt

Purpose: Demonstrates all required functionalities of the model as listed in the milestone specification.

Command used:
java -jar res/KillDrLucky.jar res/ArrakisPalace.txt --cell 25 --out res/ArrakisPalace.png --space 5 > res/run_basics.txt

This example run shows a complete execution of the model using the ArrakisPalace.txt world specification file.
