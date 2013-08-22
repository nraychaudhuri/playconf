package controllers;

import static helpers.TestSetup.testHttpContext;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.status;

import org.codehaus.jackson.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import play.libs.F;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.Json;
import play.libs.OAuth.RequestToken;
import play.mvc.Http.Context;
import play.mvc.Http.Flash;
import play.mvc.Http.Status;
import play.mvc.Result;
import akka.actor.ActorRef;
import external.services.OAuthService;

public class ApplicationTest {

    @Before
    public void setUpHttpContext() {        
       Context.current.set(testHttpContext());
    }
    
    @Test
    public void redirectToOAuthProviderForRegister() {
        OAuthService oauth = mock(OAuthService.class);
        Tuple<String, RequestToken> t = new F.Tuple<String, RequestToken>(
                "twitter.redirect.url", new RequestToken("twitter.token", "twitter.secret"));
        when(oauth.retreiveRequestToken(anyString())).thenReturn(t);

        Application app = new Application(mock(ActorRef.class), oauth);
        Result result = app.register();
        assertThat(status(result)).isEqualTo(Status.SEE_OTHER);
        assertThat(redirectLocation(result)).isEqualTo("twitter.redirect.url");
        
        Flash flash = Context.current().flash();
        assertThat(flash.get("request_token")).isEqualTo("twitter.token");
        assertThat(flash.get("request_secret")).isEqualTo("twitter.secret");
        
    }
    
    @Test
    public void registerUser() {
        Context ctx = Context.current();
        ctx.flash().put("request_token", "foo");
        ctx.flash().put("request_secret", "bar");
        
        OAuthService oauth = mock(OAuthService.class);
        JsonNode emptyJson = Json.newObject();
        
        ArgumentCaptor<RequestToken> rtArg = ArgumentCaptor.forClass(RequestToken.class);
        when(oauth.registeredUserProfile(rtArg.capture(), anyString())).thenReturn(Promise.pure(emptyJson));        
        
        Application app = new Application(mock(ActorRef.class), oauth);
        Result result = app.registerCallback();
        assertThat(status(result)).isEqualTo(303);
        assertThat(redirectLocation(result)).isEqualTo("/");
        
        assertThat(rtArg.getValue().token).isEqualTo("foo");
        assertThat(rtArg.getValue().secret).isEqualTo("bar");
    }
    
    
    
}