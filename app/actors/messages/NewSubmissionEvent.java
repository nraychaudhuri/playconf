package actors.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

import models.Proposal;

public class NewSubmissionEvent implements UserEvent {

    private final Proposal submission;

    public NewSubmissionEvent(Proposal s) {
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
