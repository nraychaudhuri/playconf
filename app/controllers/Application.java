package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.RegisteredUser;
import models.Proposal;

import com.fasterxml.jackson.databind.JsonNode;

import play.data.Form;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.OAuth.RequestToken;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;
import views.html.newProposal;
import actors.messages.CloseConnectionEvent;
import actors.messages.NewConnectionEvent;
import actors.messages.NewSubmissionEvent;
import actors.messages.UserRegistrationEvent;
import akka.actor.ActorRef;
import external.services.OAuthService;

@Singleton
public class Application extends Controller {

    private ActorRef publisher;
    private OAuthService oauth;

    @Inject
    public Application(ActorRef publisher, OAuthService oauth) {
        this.publisher = publisher;
        this.oauth = oauth;
    }

    private static Form<Proposal> form = Form.form(Proposal.class);

    public Result register() {
        String redirectURL = routes.Application.registerCallback().absoluteURL(request());
        Tuple<String, RequestToken> t = oauth.retreiveRequestToken(redirectURL);
        flash("request_token", t._2.token);
        flash("request_secret", t._2.secret);
        return redirect(t._1);
    }

    public Result registerCallback() {
        RequestToken token = new RequestToken(flash("request_token"),
                flash("request_secret"));        
        String authVerifier = request().getQueryString("oauth_verifier");
        Promise<JsonNode> userProfile = oauth.registeredUserProfile(token,
                authVerifier);
        userProfile.onRedeem(new Callback<JsonNode>() {
            @Override
            public void invoke(JsonNode twitterJson) throws Throwable {
                RegisteredUser ru = RegisteredUser.fromJson(twitterJson);
                ru.save();
                publisher.tell(new UserRegistrationEvent(ru), null);
            }
        });
        return redirect(routes.Application.index());
    }

    public WebSocket<JsonNode> buzz() {
        return new WebSocket<JsonNode>() {
            public void onReady(WebSocket.In<JsonNode> in,
                    WebSocket.Out<JsonNode> out) {
                final String uuid = java.util.UUID.randomUUID().toString();
                publisher.tell(new NewConnectionEvent(uuid , out), null);
                in.onClose(new Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        publisher.tell(new CloseConnectionEvent(uuid), null);
                    }
                });
            }
        };
    }

    public Result index() {
        Promise<Result> p = Proposal.findKeynote().map(new Function<Proposal, Result>() {
            @Override
            public Result apply(Proposal s) throws Throwable {
                return ok(index.render(s));
            }
        });
        return async(p);
    }

    public Result newProposal() {
        return ok(newProposal.render(form));
    }

    public Result recentUsers(int count) {
        RegisteredUser.recentUsers(count).onRedeem(new Callback<List<RegisteredUser>>() {
            @Override
            public void invoke(List<RegisteredUser> users) throws Throwable {
                for (RegisteredUser ru : users) {
                    publisher.tell(new UserRegistrationEvent(ru), null);
                }
            }
        });
        return ok();
    }

    public Result submitProposal() {
        Form<Proposal> filledForm = form.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(newProposal.render(filledForm));
        } else {
            final Proposal s = filledForm.get();
            Promise<Result> r = s.asyncSave().map(new Function<Void, Result>(){
                public Result apply(Void a) {
                    publisher.tell(new NewSubmissionEvent(s), null);
                    flash("message", "Thanks for submitting the proposal. We will get back to you soon.");
                    return redirect(routes.Application.index());                    
                }
            });
            return async(r);
        }
    }
}
