package external.services;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.OAuth.RequestToken;

public interface OAuthService {
    public Tuple<String, RequestToken> retrieveRequestToken(String callback);

    public Promise<JsonNode> registeredUserProfile(RequestToken token,
            String authVerifier);
}
