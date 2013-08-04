package models;

import static models.Functions.makeResult;

import java.util.HashMap;
import java.util.Map;

import models.messages.CloseConnectionEvent;
import models.messages.NewConnectionEvent;
import models.messages.RandomlySelectTalkEvent;
import models.messages.TalkSubmissionEvent;
import models.messages.UserRegistrationEvent;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Promise;
import play.mvc.WebSocket.Out;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class EventPublisher extends UntypedActor {

	public final static ActorRef publisher = Akka.system().actorOf(
			new Props(EventPublisher.class));

	private Map<String, Out<JsonNode>> connections = new HashMap<String, Out<JsonNode>>();

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof NewConnectionEvent) {
			final NewConnectionEvent nce = (NewConnectionEvent) message;
			connections.put(nce.uuid(), nce.out());
			Logger.info("New browser connected (" + connections.size()
					+ " browsers currently connected)");
		}
		if (message instanceof CloseConnectionEvent) {
			final CloseConnectionEvent cce = (CloseConnectionEvent) message;
			connections.remove(cce.uuid());
			Logger.info("Browser disconnected (" + connections.size()
					+ " browsers currently connected)");
		}
		if (message instanceof UserRegistrationEvent) {
			UserRegistrationEvent ure = (UserRegistrationEvent) message;
			for (Out<JsonNode> out : connections.values()) {
				out.write(ure.userInfo());
			}
		}
		if (message instanceof RandomlySelectTalkEvent) {
			Promise<JsonNode> promiseOfJson = 
					Submission.randomlyPickSession().flatMap(makeResult);
		    System.out.println("Select a proposal");
			promiseOfJson.onRedeem(new Callback<JsonNode>() {
				@Override
				public void invoke(JsonNode json) throws Throwable {
					for (Out<JsonNode> out : connections.values()) {
						out.write(json);
					}					
				}
			});
		}
		if (message instanceof TalkSubmissionEvent) {

		} else
			unhandled(message);
	}
}
