package external.services;

import org.codehaus.jackson.JsonNode;

import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.OAuth.RequestToken;

public interface OAuthService {
    public Tuple<String, RequestToken> retreiveRequestToken(String callback);

    public Promise<JsonNode> registeredUserProfile(RequestToken token,
            String authVerifier);
}
