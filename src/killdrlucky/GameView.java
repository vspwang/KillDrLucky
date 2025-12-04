package killdrlucky;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Swing-based game view.
 */
public class GameView extends JFrame implements GameViewInterface {
  private final ReadOnlyWorld model;
  private JPanel gamePanel;
  private final JTextArea messageArea;
  private final JLabel statusLabel;
  
  /**
   * Creates game view.
   */
  public GameView(ReadOnlyWorld model) {
    super("Kill Doctor Lucky");
    this.model = model;
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1000, 700);
    setMinimumSize(new Dimension(300, 300));
    setLayout(new BorderLayout());
    
    // Top: status
    statusLabel = new JLabel("Game Started");
    add(statusLabel, BorderLayout.NORTH);
    
    // Center: game world
    gamePanel = new WorldPanel(model);
    
    gamePanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();  
      }
    });
    
    add(new JScrollPane(gamePanel), BorderLayout.CENTER);
    
    // Bottom: messages
    messageArea = new JTextArea(5, 50);
    messageArea.setEditable(false);
    messageArea.setFocusable(false);  
    add(new JScrollPane(messageArea), BorderLayout.SOUTH);
    
    setLocationRelativeTo(null);
    setVisible(true);
    
    requestFocusInWindow();
  }
  
  @Override
  public void setClickListener(MouseListener listener) {
    gamePanel.addMouseListener(listener);
  }
  
  @Override
  public void setKeyListener(KeyListener listener) {
    addKeyListener(listener);
    setFocusable(true);
    requestFocusInWindow();  
  }
  
  @Override
  public void updateStatus(String text) {
    statusLabel.setText(text);
    requestFocusInWindow(); 
  }
  
  @Override
  public void addMessage(String text) {
    messageArea.append(text + "\n");
    messageArea.setCaretPosition(messageArea.getDocument().getLength());
    requestFocusInWindow(); 
  }
  
  @Override
  public void refresh() {
    gamePanel.repaint();
    requestFocusInWindow(); 
  }
  
  @Override
  public String promptInput(String message) {
    String result = JOptionPane.showInputDialog(this, message);
    requestFocusInWindow(); 
    return result;
  }
  
  @Override
  public void showMessage(String message) {
    JOptionPane.showMessageDialog(this, message);
    requestFocusInWindow(); 
  }
}