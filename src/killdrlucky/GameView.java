package killdrlucky;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * Swing-based game view with welcome screen and menu.
 */
public class GameView extends JFrame implements GameViewInterface {
  private static final long serialVersionUID = 1L;
  
  private ReadOnlyWorld model;
  private JPanel mainPanel;
  private CardLayout cardLayout;
  private JPanel welcomePanel;
  private JPanel gamePanel;
  private WorldPanel worldPanel;
  private final JTextArea messageArea;
  private final JLabel statusLabel;
  private Runnable onStartNewGame;
  private Runnable onRestartGame;
  private java.util.function.Consumer<String> onStartNewGameWithNewWorld;
  
  /**
   * Creates game view with welcome screen.
   * 
   * @param modelParam the read-only world model
   */
  public GameView(ReadOnlyWorld modelParam) {
    super("Kill Doctor Lucky");
    this.model = modelParam;
    
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1000, 700);
    setMinimumSize(new Dimension(300, 300));
    
    // Create menu bar
    createMenuBar();
    
    // Create card layout for switching between welcome and game screens
    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);
    
    // Create welcome panel
    createWelcomePanel();
    
    // Create game panel
    statusLabel = new JLabel("Game Started");
    messageArea = new JTextArea(5, 50);
    messageArea.setEditable(false);
    messageArea.setFocusable(false);
    
    gamePanel = new JPanel(new BorderLayout());
    gamePanel.add(statusLabel, BorderLayout.NORTH);
    
    worldPanel = new WorldPanel(model);
    worldPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        requestFocusInWindow();
      }
    });
    
    gamePanel.add(new JScrollPane(worldPanel), BorderLayout.CENTER);
    gamePanel.add(new JScrollPane(messageArea), BorderLayout.SOUTH);
    
    // Add panels to card layout
    mainPanel.add(welcomePanel, "WELCOME");
    mainPanel.add(gamePanel, "GAME");
    
    add(mainPanel);
    
    setLocationRelativeTo(null);
    setVisible(true);
    
    // Show welcome screen by default
    showWelcomeScreen();
  }
  
  private void createMenuBar() {
    final JMenuBar menuBar = new JMenuBar();
    final JMenu fileMenu = new JMenu("File");
    
    // 1. New Game with NEW world specification 
    JMenuItem newGameNewWorldItem = new JMenuItem("New Game (New World)");
    newGameNewWorldItem.addActionListener(e -> {
      javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser("res/");
      fileChooser.setDialogTitle("Select World File");
      
      fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        @Override
        public boolean accept(java.io.File f) {
          return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
        }
        
        @Override
        public String getDescription() {
          return "World Files (*.txt)";
        }
      });
      
      int result = fileChooser.showOpenDialog(this);
      if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
        String newWorldFile = fileChooser.getSelectedFile().getAbsolutePath();
        if (onStartNewGameWithNewWorld != null) {
          onStartNewGameWithNewWorld.accept(newWorldFile);
        }
      }
    });
    
    // 2. Restart with CURRENT world specification 
    JMenuItem restartItem = new JMenuItem("Restart Game (Current World)");
    restartItem.addActionListener(e -> {
      if (onRestartGame != null) {
        onRestartGame.run();
      }
    });
    
    // 3. Quit
    JMenuItem quitItem = new JMenuItem("Quit");
    quitItem.addActionListener(e -> System.exit(0));
    
    fileMenu.add(newGameNewWorldItem);
    fileMenu.add(restartItem);
    fileMenu.addSeparator();
    fileMenu.add(quitItem);
    
    menuBar.add(fileMenu);
    setJMenuBar(menuBar);
  }
  
  private void createWelcomePanel() {
    welcomePanel = new JPanel(new GridBagLayout());
    welcomePanel.setBackground(new Color(240, 248, 255));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = new Insets(10, 10, 10, 10);
    
    // Title
    JLabel titleLabel = new JLabel("KILL DOCTOR LUCKY", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
    titleLabel.setForeground(new Color(222, 123, 111));
    gbc.insets = new Insets(30, 10, 20, 10);
    welcomePanel.add(titleLabel, gbc);
    
    // Subtitle
    JLabel subtitleLabel = new JLabel("A Strategic Board Game", SwingConstants.CENTER);
    subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
    gbc.insets = new Insets(0, 10, 30, 10);
    welcomePanel.add(subtitleLabel, gbc);
    
    JLabel subtitleLabelNext = new JLabel("Or B", SwingConstants.CENTER);
    subtitleLabelNext.setFont(new Font("Arial", Font.ITALIC, 18));
    gbc.insets = new Insets(0, 10, 30, 10);
    welcomePanel.add(subtitleLabelNext, gbc);
    
    // Credits
    JPanel creditsPanel = new JPanel();
    creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
    creditsPanel.setBackground(new Color(240, 248, 255));
    
    JLabel creatorLabel = new JLabel("Created by: Vesper Wang", SwingConstants.CENTER);
    creatorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    creatorLabel.setAlignmentX(CENTER_ALIGNMENT);
    
    JLabel resourcesLabel = new JLabel("Resources Used:", SwingConstants.CENTER);
    resourcesLabel.setFont(new Font("Arial", Font.BOLD, 14));
    resourcesLabel.setAlignmentX(CENTER_ALIGNMENT);
    
    JLabel resource1 = new JLabel("• Java Swing GUI Framework", SwingConstants.CENTER);
    resource1.setFont(new Font("Arial", Font.PLAIN, 12));
    resource1.setAlignmentX(CENTER_ALIGNMENT);
    
    JLabel resource2 = new JLabel("• MVC Design Pattern", SwingConstants.CENTER);
    resource2.setFont(new Font("Arial", Font.PLAIN, 12));
    resource2.setAlignmentX(CENTER_ALIGNMENT);
    
    creditsPanel.add(creatorLabel);
    creditsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    creditsPanel.add(resourcesLabel);
    creditsPanel.add(resource1);
    creditsPanel.add(resource2);
    
    gbc.insets = new Insets(10, 10, 30, 10);
    welcomePanel.add(creditsPanel, gbc);
    
    // Start button
    JButton startButton = new JButton("Start New Game");
    startButton.setFont(new Font("Arial", Font.BOLD, 16));
    startButton.setPreferredSize(new Dimension(200, 50));
    startButton.addActionListener(e -> {
      if (onStartNewGame != null) {
        onStartNewGame.run();
      }
    });
    gbc.insets = new Insets(20, 10, 10, 10);
    welcomePanel.add(startButton, gbc);
    
    // Instructions
    JLabel instructionsLabel = new JLabel(
        "<html><center>Click: Move to space | P: Pick up | L: Look around<br>"
        + "A: Attack | M: Move pet | Click player: View info</center></html>",
        SwingConstants.CENTER);
    instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    gbc.insets = new Insets(20, 10, 10, 10);
    welcomePanel.add(instructionsLabel, gbc);
  }
  
  /**
   * Show welcome screen.
   */
  public void showWelcomeScreen() {
    cardLayout.show(mainPanel, "WELCOME");
  }
  
  /**
   * Show game screen.
   */
  public void showGameScreen() {
    cardLayout.show(mainPanel, "GAME");
    requestFocusInWindow();
  }
  
  /**
   * Update the model reference.
   * 
   * @param newModel the new model
   */
  public void setModel(ReadOnlyWorld newModel) {
    this.model = newModel;
    if (worldPanel != null) {
      worldPanel.setModel(newModel);
    }
  }
  
  @Override
  public void setClickListener(MouseListener listener) {
    worldPanel.addMouseListener(listener);
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
  
  /**
   * Clear all messages from the message area.
   */
  public void clearMessages() {
    messageArea.setText("");
  }

  /**
   * Reset view to initial state.
   */
  public void resetView() {
    messageArea.setText("");
    statusLabel.setText("Game Started");
    repaint();
  }
  
  @Override
  public void refresh() {
    worldPanel.repaint();
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
  
  /**
   * Get the world panel.
   * 
   * @return the world panel
   */
  public WorldPanel getWorldPanel() {
    return worldPanel;
  }
  
  /**
   * Set callback for starting new game with existing world.
   * 
   * @param callback the callback to run
   */
  public void setOnStartNewGame(Runnable callback) {
    this.onStartNewGame = callback;
  }
  
  /**
   * Set callback for starting new game with new world file.
   * 
   * @param callback the callback accepting world file path
   */
  public void setOnStartNewGameWithNewWorld(java.util.function.Consumer<String> callback) {
    this.onStartNewGameWithNewWorld = callback;
  }
  
  /**
   * Set callback for restarting game.
   * 
   * @param callback the callback to run
   */
  public void setOnRestartGame(Runnable callback) {
    this.onRestartGame = callback;
  }
}