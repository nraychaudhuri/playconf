package models;

import org.codehaus.jackson.JsonNode;

import play.libs.F.Function;
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

}
