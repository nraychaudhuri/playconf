package actors.messages;

import models.RegisteredUser;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class UserRegistrationEvent implements UserEvent {

	private RegisteredUser user;

	public UserRegistrationEvent(RegisteredUser ru) {
		this.user = ru;
	}
	
	
	public JsonNode json() {
		final ObjectNode result = Json.newObject();
		result.put("messageType", "registeredUser");
		result.put("name", user.name);
		result.put("twitterId", user.twitterId);
		result.put("description", user.description);
		result.put("pictureUrl", user.pictureUrl);
		return result;

	}	
}
