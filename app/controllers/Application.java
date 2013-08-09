package controllers;

//import static common.EventPublisher.publisher;
import static common.Twitter.registeredUserProfile;
import static common.Twitter.retriveRequestToken;

import java.util.List;

import models.RegisteredUser;
import models.Submission;
import models.messages.NewSubmissionEvent;
import models.messages.UserRegistrationEvent;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.data.Form;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.OAuth.RequestToken;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;
import views.html.index;
import views.html.newProposal;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;

import common.UserActor;

public class Application extends Controller {

	private static Form<Submission> form = Form.form(Submission.class);

	public static Result register() {
		Tuple<String, RequestToken> t = retriveRequestToken("http://"
				+ request().host() + "/register_callback");
		flash("request_token", t._2.token);
		flash("request_secret", t._2.secret);
		return redirect(t._1);
	}

	public static Result register_callback() {
		RequestToken token = new RequestToken(flash("request_token"),
				flash("request_secret"));
		String authVerifier = request().getQueryString("oauth_verifier");
		Promise<JsonNode> userProfile = registeredUserProfile(token,
				authVerifier);
		userProfile.onRedeem(new Callback<JsonNode>() {
			@Override
			public void invoke(JsonNode twitterJson) throws Throwable {
				RegisteredUser ru = RegisteredUser.fromJson(twitterJson);
				ru.save();
				UserActor.users().tell(new UserRegistrationEvent(ru));
			}
		});
		return redirect(routes.Application.index());
	}

	public static WebSocket<JsonNode> messageBoard(final String uuid) {
	  return new WebSocket<JsonNode>() {
			public void onReady(WebSocket.In<JsonNode> in,
					WebSocket.Out<JsonNode> out) {
				final ActorRef userActor = createUserActor(uuid, out);				
				in.onClose(new Callback0() {
					@Override
					public void invoke() throws Throwable {
						Logger.info("Browser disconnected");
						userActor.tell(PoisonPill.getInstance(), null);
					}
				});
			}
		  };
	}

	private static ActorRef createUserActor(String uuid, final Out<JsonNode> out) {
		ActorRef userActor = Akka.system().actorOf(
		  new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new UserActor(out);
			}
		}), "user" + uuid);
		
		return userActor;
	}
	
	public static Result index() {
		return ok(index.render(Submission.findKeynote()));
	}

	public static Result newProposal() {
		return ok(newProposal.render(form));
	}

	public static Result recentUsers(int count) {
		List<RegisteredUser> users = RegisteredUser.recentUsers(count);
		for (RegisteredUser ru : users) {
			UserActor.users().tell(new UserRegistrationEvent(ru));
		}		
		return ok("Done");
	}

	public static Result submitProposal() {
		Form<Submission> filledForm = form.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(newProposal.render(filledForm));
		} else {
			Submission s = filledForm.get();
			s.save();
			UserActor.users().tell(new NewSubmissionEvent(s));
			return redirect(routes.Application.index());
		}
	}

}
