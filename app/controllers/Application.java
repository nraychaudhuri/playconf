package controllers;

import java.util.concurrent.Callable;

import models.Submission;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.ning.http.util.Base64;

public class Application extends Controller {

	private static Form<Submission> form = Form.form(Submission.class);

	public static Result index() {
		return ok(index.render(form));
	}

	public static Result submit() {
		Form<Submission> filledForm = form.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(index.render(filledForm));
		} else {
			Submission s = filledForm.get();
			s.save();
			return redirect(routes.Application.index());
		}
	}

	public static Result randomlyPickSession() {
		Promise<Submission> promiseOfSubmission = play.libs.Akka
				.future(new Callable<Submission>() {
					public Submission call() {
						// randomly select one if the first
						Long randomId = (long) (1 + Math.random() * (5 - 1));
						return Submission.find.byId(randomId);
					}
				});
		
		Promise<JsonNode> promiseOfJson = promiseOfSubmission.flatMap(new Function<Submission, Promise<JsonNode>>() {
					public Promise<JsonNode> apply(Submission s) {
					  return makeResult(s);
					}
				});
		return async(promiseOfJson
				.map(new Function<JsonNode, Result>() {
					public Result apply(JsonNode s) {
						return ok(s);
					}
				}));
	}
	
	
	private static Promise<JsonNode> makeResult(Submission s) {
		  final ObjectNode result = Json.newObject();
		  result.put("title", s.title);
		  result.put("proposal", s.proposal);
		  result.put("name", s.speaker.name);
		  result.put("twitterId", s.speaker.twitterId);
		  return _findFollowers(s.speaker.twitterId).map(new Function<Long, JsonNode>() {
			 public JsonNode apply(Long followerCount) {
			   result.put("followerCount", followerCount);
			   return result;
			 }		  
		  });
	}

	public static Result findFollowers(final String screenName) {
	  return async(_findFollowers(screenName).map(new Function<Long, Result>() {
		 public Result apply(Long s) {
	    	return ok(s.toString());
		 }
		}));	
	}

	private static Promise<Long> _findFollowers(final String screenName) {
		String consumerKey = "ZH15OspjNAfn5cyfLGm8KA";
		String consumerSecret = "IKgL5u3KORkRDh9Ay78iBhrl3N4JWbQXxazCJNc";
		String bearerToken = Base64.encode((consumerKey + ":" + consumerSecret)
				.getBytes());

		// getting the bearer token
		WSRequestHolder req = WS
				.url("https://api.twitter.com/oauth2/token")
				.setHeader("Authorization", "Basic " + bearerToken)
				.setContentType(
						"application/x-www-form-urlencoded;charset=UTF-8");
		Promise<Response> response = req.post("grant_type=client_credentials");

		return response.flatMap(new Function<Response, Promise<Long>>() {
					public Promise<Long> apply(Response s) {
						String accessToken = s.asJson()
								.findPath("access_token").asText();
						return getFollowerCount(screenName, accessToken);
					}
				});

	}

	private static Promise<Long> getFollowerCount(String screenName,
			String accessToken) {
		WSRequestHolder req = WS
				.url("https://api.twitter.com/1.1/users/show.json")
				.setQueryParameter("screen_name", screenName)
				.setHeader("Authorization", "Bearer " + accessToken);
		return req.get().map(new Function<Response, Long>() {
			public Long apply(Response s) {
				return s.asJson().findPath("followers_count").asLong();
			}
		});
	}

	// public static Result approvedSessions() {
	// Filter<Submission> onlyApprovedFilter =
	// Submission.find.filter().eq("isApproved", true);
	// List<Submission> a = onlyApprovedFilter.filter(Submission.find.all());
	// //Comet comet = new Comet
	// return ok("");
	// }

}
