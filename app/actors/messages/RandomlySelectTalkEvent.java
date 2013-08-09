package actors.messages;

import models.Submission;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public final class RandomlySelectTalkEvent implements UserEvent {

	private final Submission submission;
	public RandomlySelectTalkEvent(Submission s) {
		this.submission = s;
	}
	
	public JsonNode json() {
		final ObjectNode result = Json.newObject();
		result.put("messageType", "proposalSubmission");
		result.put("title", submission.title);
		result.put("proposal", submission.proposal);
		result.put("name", submission.speaker.name);
		result.put("pictureUrl", submission.speaker.pictureUrl);
		result.put("twitterId", submission.speaker.twitterId);
		return result;
	}
}
