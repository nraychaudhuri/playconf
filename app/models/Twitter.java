package models;

import java.util.concurrent.Callable;

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.ning.http.util.Base64;

import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;

import static models.Functions.*;

import static play.libs.Akka.*;

public class Twitter {
	private static String consumerKey = "ZH15OspjNAfn5cyfLGm8KA";
	private static String consumerSecret = "IKgL5u3KORkRDh9Ay78iBhrl3N4JWbQXxazCJNc";

	public static F.Tuple<Token, String> getAuthorizationUrlAndRequestToken(
			String callback) {
		OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(consumerKey).apiSecret(consumerSecret)
				.callback(callback).build();
		Token token = service.getRequestToken();
		String authorizationUrl = service.getAuthorizationUrl(token);

		return new F.Tuple<Token, String>(token, authorizationUrl);
	}
	
	public static Promise<JsonNode> userSettings(Token requestToken, Verifier v) {
		final OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
				.apiKey(consumerKey).apiSecret(consumerSecret).build();
		final Token accessToken = service.getAccessToken(requestToken, v);		
		Promise<JsonNode> promiseOfSettings = future(new Callable<JsonNode>() {
			@Override
			public JsonNode call() {
				OAuthRequest request = new OAuthRequest(Verb.GET,
						"https://api.twitter.com/1.1/account/settings.json");
				service.signRequest(accessToken, request);
				return Json.parse(request.send().getBody());
			}
		});
		return promiseOfSettings;
	}
	
	public static Promise<JsonNode> userProfile(final String screenName) {
		Promise<Response> response = twitterAccessToken();
		return response.flatMap(new Function<Response, Promise<JsonNode>>() {
			public Promise<JsonNode> apply(Response s) {
				String accessToken = s.asJson().findPath("access_token")
						.asText();
				return userProfile(screenName, accessToken);
			}
		});
	}

	private static Promise<JsonNode> userProfile(final String screenName,
			String accessToken) {
		WSRequestHolder req = WS
				.url("https://api.twitter.com/1.1/users/show.json")
				.setQueryParameter("screen_name", screenName)
				.setHeader("Authorization", "Bearer " + accessToken);
		Promise<Response> promise = req.get();
		return promise.map(responseToJson);
	}

	private static Promise<Response> twitterAccessToken() {
		String bearerToken = Base64.encode((consumerKey + ":" + consumerSecret)
				.getBytes());
		// getting the bearer token
		WSRequestHolder req = WS
				.url("https://api.twitter.com/oauth2/token")
				.setHeader("Authorization", "Basic " + bearerToken)
				.setContentType(
						"application/x-www-form-urlencoded;charset=UTF-8");
		Promise<Response> response = req.post("grant_type=client_credentials");
		return response;
	}
}
