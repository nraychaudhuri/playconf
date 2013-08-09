package common;

import models.messages.NewSubmissionEvent;
import models.messages.RandomlySelectTalkEvent;
import models.messages.UserRegistrationEvent;

import org.codehaus.jackson.JsonNode;

import play.libs.Akka;
import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

public class UserActor extends UntypedActor {

	private Out<JsonNode> out;
	
	public static ActorSelection users() {
		return Akka.system().actorSelection("/user/user*");	
	}
	
	public UserActor(WebSocket.Out<JsonNode> out) {
		this.out = out;		
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof UserRegistrationEvent) {
			UserRegistrationEvent ure = (UserRegistrationEvent) message;
			out.write(ure.json());
		}		
		if (message instanceof RandomlySelectTalkEvent) {
			RandomlySelectTalkEvent rste = (RandomlySelectTalkEvent)message;
			out.write(rste.json());
		}		
		if (message instanceof NewSubmissionEvent) {
          NewSubmissionEvent nse = (NewSubmissionEvent)message;
          out.write(nse.json());
		} else
			unhandled(message);
	}
}
