package common;

import java.util.HashMap;
import java.util.Map;

import models.messages.CloseConnectionEvent;
import models.messages.NewConnectionEvent;
import models.messages.NewSubmissionEvent;
import models.messages.RandomlySelectTalkEvent;
import models.messages.UserRegistrationEvent;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Akka;
import play.mvc.WebSocket.Out;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class EventPublisher extends UntypedActor {

	private final static ActorRef publisher = Akka.system().actorOf(
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
			sendEvent(ure.json());
		}
		if (message instanceof RandomlySelectTalkEvent) {
			RandomlySelectTalkEvent rste = (RandomlySelectTalkEvent)message;
			sendEvent(rste.json());
		}
		if (message instanceof NewSubmissionEvent) {
          NewSubmissionEvent nse = (NewSubmissionEvent)message;
          sendEvent(nse.json());
		} else
			unhandled(message);
	}

	private void sendEvent(JsonNode userInfo) {
		for (Out<JsonNode> out : connections.values()) {
			out.write(userInfo);
		}
	}
}
