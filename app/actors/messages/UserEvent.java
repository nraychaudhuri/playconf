package actors.messages;

import org.codehaus.jackson.JsonNode;

public interface UserEvent {
  public JsonNode json();
}
