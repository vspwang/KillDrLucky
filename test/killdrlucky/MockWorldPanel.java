package killdrlucky;

/**
 * Mock WorldPanel for testing.
 */
public class MockWorldPanel implements WorldPanelInterface {
  private int spaceAtReturn = -1;
  private String playerAtReturn = null;
  
  public void setSpaceAtReturn(int spaceIndex) {
    this.spaceAtReturn = spaceIndex;
  }
  
  public void setPlayerAtReturn(String playerName) {
    this.playerAtReturn = playerName;
  }
  
  @Override
  public int getSpaceAt(int x, int y) {
    return spaceAtReturn;
  }
  
  public String getPlayerAt(int x, int y) {
    return playerAtReturn;
  }
  
  @Override
  public void refresh() {
    // Do nothing
  }
}