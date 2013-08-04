package models.messages;

import org.codehaus.jackson.JsonNode;

public class UserRegistrationEvent {

	private JsonNode userInfo;

	public UserRegistrationEvent(JsonNode userInfo) {
		this.userInfo = userInfo;
	}
	
	
	public JsonNode userInfo() {
		return userInfo;
	}
}
