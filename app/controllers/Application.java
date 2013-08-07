package controllers;

import static common.EventPublisher.publisher;
import static common.Twitter.registeredUserProfile;
import static common.Twitter.retriveRequestToken;
import models.RegisteredUser;
import models.Submission;
import models.messages.CloseConnectionEvent;
import models.messages.NewConnectionEvent;
import models.messages.NewSubmissionEvent;
import models.messages.UserRegistrationEvent;

import org.codehaus.jackson.JsonNode;

import play.data.Form;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.Json;
import play.libs.OAuth.RequestToken;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;
import views.html.newProposal;
import java.util.List;

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
		Promise<JsonNode> userProfile = registeredUserProfile(token, authVerifier);
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

	public static WebSocket<JsonNode> messageBoard(final String uuid) {
	  return new WebSocket<JsonNode>() {
		// Called when the Websocket Handshake is done.
		public void onReady(WebSocket.In<JsonNode> in,
				WebSocket.Out<JsonNode> out) {
			publisher.tell(new NewConnectionEvent(uuid, out), null);
			in.onClose(new Callback0() {
				@Override
				public void invoke() throws Throwable {
					publisher.tell(new CloseConnectionEvent(uuid), null);
				}
			});
		}
	  };
	}

	public static Result index() {
	  return ok(index.render(Submission.findKeynote()));
	}

	public static Result newProposal() {
	  return ok(newProposal.render(form));
	}
	
	public static Result recentUsers(int count) {
		List<RegisteredUser> users = RegisteredUser.recentUsers(count);
		for (RegisteredUser ru: users) {
			publisher.tell(new UserRegistrationEvent(ru), null);
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
			publisher.tell(new NewSubmissionEvent(s), null);
			return redirect(routes.Application.index());
		}
	}


}
