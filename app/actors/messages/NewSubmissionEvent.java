package actors.messages;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import models.Submission;

public class NewSubmissionEvent implements UserEvent {

	private final Submission submission;

	public NewSubmissionEvent(Submission s) {
		submission = s;
	}

	public JsonNode json() {		
		final ObjectNode result = Json.newObject();
		result.put("messageType", "newProposal");
		result.put("speakerName", submission.speaker.name);
		result.put("twitterId", submission.speaker.twitterId);
		result.put("title", submission.title);
		result.put("pictureUrl", submission.speaker.pictureUrl);
		return result;

	}
}
