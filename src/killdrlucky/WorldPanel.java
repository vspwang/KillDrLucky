package killdrlucky;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;

/**
 * Panel that renders the game world.
 */
public class WorldPanel extends JPanel implements WorldPanelInterface {
  private static final long serialVersionUID = 1L;
  private final ReadOnlyWorld model;
  private final int cellSize = 20;
  
  /**
   * Creates world panel.
   * 
   * @param modelParam ReadOnlyWorld model
   */
  public WorldPanel(ReadOnlyWorld modelParam) {
    this.model = modelParam;
    int w = model.getCols() * cellSize;
    int h = model.getRows() * cellSize;
    setPreferredSize(new Dimension(w, h));
    setBackground(Color.WHITE);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    drawSpaces(g2d);
    drawTarget(g2d);
    drawPet(g2d);
    drawPlayers(g2d);
  }
  
  private void drawSpaces(Graphics2D g2d) {
    for (Space space : model.getSpaces()) {
      Rect area = space.getArea();
      int x = area.getUpperLeft().getCol() * cellSize;
      int y = area.getUpperLeft().getRow() * cellSize;
      int w = area.width() * cellSize;
      int h = area.height() * cellSize;
      
      // Fill with light color
      g2d.setColor(new Color(220, 235, 245));
      g2d.fillRect(x, y, w, h);
      
      // Draw border
      g2d.setColor(Color.BLACK);
      g2d.drawRect(x, y, w, h);
      
      // Draw name and index
      g2d.setFont(new Font("Arial", Font.PLAIN, 10));
      g2d.drawString(space.getName(), x + 5, y + 15);
      g2d.drawString("[" + space.getIndex() + "]", x + 5, y + 28);
    }
  }
  
  private void drawTarget(Graphics2D g2d) {
    Target target = model.getTarget();
    drawIcon(g2d, target.getCurrentSpaceIndex(), "T", Color.RED, 0);
  }
  
  private void drawPet(Graphics2D g2d) {
    Pet pet = model.getPet();
    drawIcon(g2d, pet.getCurrentSpaceIndex(), "P", new Color(139, 69, 19), 15);
  }
  
  private void drawPlayers(Graphics2D g2d) {
    List<Iplayer> players = model.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      Iplayer player = players.get(i);
      String label = player.getName().substring(0, 1);
      Color color = player.isComputerControlled() ? Color.BLUE : Color.GREEN;
      drawIcon(g2d, player.getCurrentSpaceIndex(), label, color, i * 12 + 30);
    }
  }
  
  private void drawIcon(Graphics2D g2d, int spaceIdx, String label, Color color, int offset) {
    Space space = model.getSpace(spaceIdx);
    Rect area = space.getArea();
    int x = area.getUpperLeft().getCol() * cellSize + cellSize / 2 + offset;
    int y = area.getUpperLeft().getRow() * cellSize + cellSize / 2;
    
    g2d.setColor(color);
    g2d.fillOval(x - 8, y - 8, 16, 16);
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, 10));
    g2d.drawString(label, x - 4, y + 4);
  }
  
  @Override
  public int getSpaceAt(int x, int y) {
    int row = y / cellSize;
    int col = x / cellSize;
    
    for (Space space : model.getSpaces()) {
      Rect area = space.getArea();
      if (area.contains(new Point(row, col))) {
        return space.getIndex();
      }
    }
    return -1;
  }
  
  @Override
  public void refresh() {
    repaint();
  }
}