package controllers;

import static models.Twitter.*;

import java.util.concurrent.Callable;

import models.MessageBoard;
import models.Submission;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import play.data.Form;
import play.libs.F.Callback;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import static models.Functions.*;

public class Application extends Controller {

	private static Form<Submission> form = Form.form(Submission.class);

	public static Result register() {
		Tuple<Token, String> t = getAuthorizationUrlAndRequestToken("http://" + request().host() + "/register_callback");
		Token token = t._1;
		String authorizationUrl = t._2;
		flash("request_token", token.getToken());
		flash("request_secret", token.getSecret());
		return redirect(authorizationUrl);
	}

	public static Result register_callback() {
		String authVerifier = request().getQueryString("oauth_verifier");
		final Token requestToken = new Token(flash("request_token"),
				flash("request_secret"));		
		Promise<JsonNode> promiseOfSettings = userSettings(requestToken, new Verifier(authVerifier));
		Callback<JsonNode> notifyRegisteredUser = null;
		promiseOfSettings.onRedeem(notifyRegisteredUser);

		return ok("You are successfully registered");

	}

	public static Result messageBoard(final String name) {
		return ok(MessageBoard.newBoard());
	}

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

		Promise<JsonNode> promiseOfJson = promiseOfSubmission
				.flatMap(new Function<Submission, Promise<JsonNode>>() {
					public Promise<JsonNode> apply(Submission s) {
						return makeResult(s);
					}
				});
		return async(promiseOfJson.map(jsonToResult));
	}

	private static Promise<JsonNode> makeResult(Submission s) {
		final ObjectNode result = Json.newObject();
		result.put("title", s.title);
		result.put("proposal", s.proposal);
		result.put("name", s.speaker.name);
		result.put("twitterId", s.speaker.twitterId);
		return findFollowers(s.speaker.twitterId).map(
				new Function<Long, JsonNode>() {
					public JsonNode apply(Long followerCount) {
						result.put("followerCount", followerCount);
						return result;
					}
				});
	}

	private static Promise<Long> findFollowers(final String screenName) {		
		return userProfile(screenName).map(new Function<JsonNode, Long>() {
			public Long apply(JsonNode s) {
				return s.findPath("followers_count").asLong();
			}
		});

	}

}
