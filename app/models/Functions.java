package models;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS.Response;
import play.mvc.Result;
import play.mvc.Results;

public class Functions {
	public static Function<Response, JsonNode> responseToJson = new Function<Response, JsonNode>() {
		public JsonNode apply(Response s) {
			return s.asJson();
		}
	};

	public static Function<JsonNode, Result> jsonToResult = new Function<JsonNode, Result>() {
		public Result apply(JsonNode s) {
			return Results.ok(s);
		}
	};
	
	public static Function<Throwable, JsonNode> error = new Function<Throwable, JsonNode>(){
		@Override
		public JsonNode apply(Throwable t) throws Throwable {
			Logger.error("Failed to fetch profile", t);
			return Json.parse("{\"error\": \"failed to fetch the profile of user\"}");
		}
	};

	public static Function<Submission, Promise<JsonNode>> makeResult = new Function<Submission, Promise<JsonNode>>() {
		@Override
		public Promise<JsonNode> apply(Submission s) throws Throwable {
			final ObjectNode result = Json.newObject();
			result.put("messageType", "proposalSubmission");
			result.put("title", s.title);
			result.put("proposal", s.proposal);
			result.put("name", s.speaker.name);
			result.put("pictureUrl", s.speaker.pictureUrl);
			result.put("twitterId", s.speaker.twitterId);
			return Promise.pure((JsonNode)result);
		}
	};

	public static Function<JsonNode, String> findTextElement(final String path) {
		return new Function<JsonNode, String>() {
			public String apply(JsonNode s) {
				return s.findPath(path).asText();
			}
		};
	}

}
