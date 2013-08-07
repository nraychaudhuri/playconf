package models.messages;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class UserRegistrationEvent {

	private JsonNode twitterJson;

	public UserRegistrationEvent(JsonNode twitterJson) {
		this.twitterJson = twitterJson;
	}
	
	
	public JsonNode json() {
		final ObjectNode result = Json.newObject();
		result.put("messageType", "registeredUser");
		result.put("name", twitterJson.findPath("name").asText());
		result.put("twitterId", twitterJson.findPath("screen_name")
				.asText());
		result.put("description", twitterJson.findPath("description")
				.asText());
		result.put("pictureUrl", twitterJson.findPath("profile_image_url")
				.asText());
		return result;

	}	
}
