package killdrlucky;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock implementation of GameViewInterface for testing.
 */
public class MockGameView implements GameViewInterface {
  private List<String> methodCalls = new ArrayList<>();
  private List<String> messages = new ArrayList<>();
  private List<String> statusUpdates = new ArrayList<>();
  @SuppressWarnings("unused")
  private String lastPromptMessage;
  private String promptInputReturn;
  
  // For verification
  public boolean wasMethodCalled(String methodName) {
    return methodCalls.contains(methodName);
  }
  
  public int getMethodCallCount(String methodName) {
    return (int) methodCalls.stream().filter(m -> m.equals(methodName)).count();
  }
  
  public List<String> getMessages() {
    return new ArrayList<>(messages);
  }
  
  public String getLastMessage() {
    return messages.isEmpty() ? null : messages.get(messages.size() - 1);
  }
  
  public String getLastStatus() {
    return statusUpdates.isEmpty() ? null : statusUpdates.get(statusUpdates.size() - 1);
  }
  
  public void setPromptInputReturn(String value) {
    this.promptInputReturn = value;
  }
  
  /**
   * reset all method calls.
   */
  public void resetMethodCalls() {
    methodCalls.clear();
    messages.clear();
    statusUpdates.clear();
  }
  
  // Implement interface
  @Override
  public void setClickListener(MouseListener listener) {
    methodCalls.add("setClickListener");
  }
  
  @Override
  public void setKeyListener(KeyListener listener) {
    methodCalls.add("setKeyListener");
  }
  
  @Override
  public void updateStatus(String text) {
    methodCalls.add("updateStatus");
    statusUpdates.add(text);
  }
  
  @Override
  public void addMessage(String text) {
    methodCalls.add("addMessage");
    messages.add(text);
  }
  
  @Override
  public void refresh() {
    methodCalls.add("refresh");
  }
  
  @Override
  public String promptInput(String message) {
    methodCalls.add("promptInput");
    lastPromptMessage = message;
    return promptInputReturn;
  }
  
  @Override
  public void showMessage(String message) {
    methodCalls.add("showMessage");
    messages.add("DIALOG: " + message);
  }
}