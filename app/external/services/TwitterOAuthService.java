package external.services;

import static common.Functions.fetchUserError;
import static common.Functions.findTextElement;
import static common.Functions.responseToJson;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.F;
import play.libs.OAuth;
import play.libs.WS;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.OAuth.ConsumerKey;
import play.libs.OAuth.OAuthCalculator;
import play.libs.OAuth.RequestToken;
import play.libs.OAuth.ServiceInfo;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;

import com.ning.http.util.Base64;

@Singleton
public class TwitterOAuthService implements OAuthService {

    private final String consumerKey;
    private final String consumerSecret;
    private final OAuth oauthHelper;
    private final ConsumerKey key;

    public TwitterOAuthService(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.key = new ConsumerKey(consumerKey, consumerSecret);
        this.oauthHelper = new OAuth(new ServiceInfo(
                "https://api.twitter.com/oauth/request_token",
                "https://api.twitter.com/oauth/access_token",
                "https://api.twitter.com/oauth/authorize", key));

    }

    public Tuple<String, RequestToken> retrieveRequestToken(String callback) {
        RequestToken tr = oauthHelper.retrieveRequestToken(callback);
        return new F.Tuple<String, RequestToken>(
                oauthHelper.redirectUrl(tr.token), tr);
    }

    public Promise<JsonNode> registeredUserProfile(RequestToken token,
            String authVerifier) {
        RequestToken rt = oauthHelper.retrieveAccessToken(token, authVerifier);
        WSRequestHolder req = WS.url(
                "https://api.twitter.com/1.1/account/settings.json").sign(
                new OAuthCalculator(key, rt));
        Promise<String> screenName = req.get().map(responseToJson)
                .map(findTextElement("screen_name"));
        return screenName.flatMap(userProfile);
    }

    public Function<String, Promise<JsonNode>> userProfile = new Function<String, Promise<JsonNode>>() {
        public Promise<JsonNode> apply(final String screenName) {
            Promise<String> response = authenticateApplication().map(
                    responseToJson).map(findTextElement("access_token"));
            return response.flatMap(fetchProfile(screenName)).recover(fetchUserError);
        }
    };

    private static Function<String, Promise<JsonNode>> fetchProfile(
            final String screenName) {
        return new Function<String, Promise<JsonNode>>() {
            public Promise<JsonNode> apply(String accessToken) {
                WSRequestHolder req = WS
                        .url("https://api.twitter.com/1.1/users/show.json")
                        .setQueryParameter("screen_name", screenName)
                        .setHeader("Authorization", "Bearer " + accessToken);
                Promise<Response> promise = req.get();
                return promise.map(responseToJson);
            }
        };
    }

    private Promise<Response> authenticateApplication() {
        WSRequestHolder req = WS
                .url("https://api.twitter.com/oauth2/token")
                .setHeader("Authorization", "Basic " + bearerToken())
                .setContentType(
                        "application/x-www-form-urlencoded;charset=UTF-8");
        return req.post("grant_type=client_credentials");
    }

    private String bearerToken() {
        return Base64.encode((consumerKey + ":" + consumerSecret).getBytes());
    }

}
