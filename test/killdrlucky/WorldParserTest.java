package killdrlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class WorldParserTest {

  private String sampleWorld() {
    // Your exact format sample
    return String.join("\n",
        "36 30 Doctor Lucky's Mansion",
        "50 Doctor Lucky",
        "21",
        "22 19 23 26 Armory",
        "16 21 21 28 Billiard Room",
        "28 0 35 5 Carriage House",
        "12 11 21 20 Dining Hall",
        "22 13 25 18 Drawing Room",
        "26 13 27 18 Foyer",
        "28 26 35 29 Green House",
        "30 20 35 25 Hedge Maze",
        "16 3 21 10 Kitchen",
        "0 3 5 8 Lancaster Room",
        "4 23 9 28 Library",
        "2 9 7 14 Lilac Room",
        "2 15 7 22 Master Suite",
        "0 23 3 28 Nursery",
        "10 5 15 10 Parlor",
        "28 12 35 19 Piazza",
        "6 3 9 8 Servants' Quarters",
        "8 11 11 20 Tennessee Room",
        "10 21 15 26 Trophy Room",
        "22 5 23 12 Wine Cellar",
        "30 6 35 11 Winter Garden",
        "20",
        "8 3 Crepe Pan",
        "4 2 Letter Opener",
        "12 2 Shoe Horn",
        "8 3 Sharp Knife",
        "0 3 Revolver",
        "15 3 Civil War Cannon",
        "2 4 Chain Saw",
        "16 2 Broom Stick",
        "1 2 Billiard Cue",
        "19 2 Rat Poison",
        "6 2 Trowel",
        "2 4 Big Red Hammer",
        "6 2 Pinking Shears",
        "18 3 Duck Decoy",
        "13 2 Bad Cream",
        "18 2 Monkey Hand",
        "11 2 Tight Hat",
        "19 2 Piece of Rope",
        "9 3 Silken Cord",
        "7 2 Loud Noise"
    );
  }

  @Test
  void parse_happyPath_countsAndKeyFields() throws IOException {
    WorldParser parser = new WorldParser();
    WorldParser.WorldData data = parser.parse(new StringReader(sampleWorld()));

    assertEquals("Doctor Lucky's Mansion", data.worldName);
    assertEquals(36, data.rows);
    assertEquals(30, data.cols);

    // Target
    assertEquals("Doctor Lucky", data.target.getName());
    assertEquals(50, data.target.getHealth());
    assertEquals(0, data.target.getCurrentSpaceIndex()); // starts in space 0

    // Rooms
    assertEquals(21, data.rooms.size());
    Room r0 = data.rooms.get(0); // first room line is index 0
    assertEquals("Armory", r0.getName());
    assertEquals(22, r0.getArea().getUpperLeft().getRow());
    assertEquals(19, r0.getArea().getUpperLeft().getCol());
    assertEquals(23, r0.getArea().getLowerRight().getRow());
    assertEquals(26, r0.getArea().getLowerRight().getCol());

    // Items
    assertEquals(20, data.items.size());
    Item first = data.items.get(0);
    assertEquals("Crepe Pan", first.getName());
    assertEquals(3, first.getDamage());
    assertEquals(8, first.getRoomIndex());
  }

  @Test
  void parse_rejectsOverlappingRooms() {
    String bad = String.join("\n",
        "10 10 Mansion",
        "10 Doctor Lucky",
        "2",
        "0 0 5 5 RoomA",
        "3 3 7 7 RoomB", // overlaps RoomA with positive area
        "0",             // items
        ""
    );
    WorldParser parser = new WorldParser();
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(new StringReader(bad)));
  }

  @Test
  void parse_rejectsRoomOutOfBounds() {
    String bad = String.join("\n",
        "6 6 Mansion",
        "10 Doctor Lucky",
        "1",
        "0 0 6 6 TooBigRoom", // lr row/col exceed bounds (>= rows/cols)
        "0",
        ""
    );
    WorldParser parser = new WorldParser();
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(new StringReader(bad)));
  }

  @Test
  void parse_rejectsInvalidItemRoomIndex() {
    String bad = String.join("\n",
        "10 10 Mansion",
        "10 Doctor Lucky",
        "1",
        "0 0 1 1 RoomA",
        "1",
        "5 3 Knife" // roomIndex=5 is invalid (only room 0 exists)
    );
    WorldParser parser = new WorldParser();
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(new StringReader(bad)));
  }

  @Test
  void parse_rejectsNegativeDamage() {
    String bad = String.join("\n",
        "10 10 Mansion",
        "10 Doctor Lucky",
        "1",
        "0 0 1 1 RoomA",
        "1",
        "0 -1 BadKnife" // negative damage not allowed
    );
    WorldParser parser = new WorldParser();
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(new StringReader(bad)));
  }

  @Test
  void parse_requiresTargetLine() {
    String bad = String.join("\n",
        "10 10 Mansion",
        // MISSING target line here
        "1",
        "0 0 1 1 RoomA",
        "0"
    );
    WorldParser parser = new WorldParser();
    assertThrows(IllegalArgumentException.class,
        () -> parser.parse(new StringReader(bad)));
  }
}
