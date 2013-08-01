package controllers;

import static models.Functions.jsonToResult;
import static models.EventPublisher.publisher;
import static models.Twitter.retriveRequestToken;
import static models.Twitter.userSettings;
import models.Submission;
import models.messages.CloseConnectionEvent;
import models.messages.NewConnectionEvent;

import org.codehaus.jackson.JsonNode;

import play.data.Form;
import play.libs.F.Callback0;
import play.libs.F.Promise;
import play.libs.F.Tuple;
import play.libs.OAuth.RequestToken;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.*;

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
		Promise<JsonNode> settings = userSettings(token, authVerifier);
		// notify that user signed.
		publisher.tell("New user signed", null);
		return async(settings.map(jsonToResult));

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
		return ok(index.render());
	}

	public static Result newProposal() {
		return ok(newProposal.render(form));
	}

	public static Result submitProposal() {
		Form<Submission> filledForm = form.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(newProposal.render(filledForm));
		} else {
			Submission s = filledForm.get();
			s.save();
			return redirect(routes.Application.index());
		}
	}


}
