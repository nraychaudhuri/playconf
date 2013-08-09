package common;

import models.messages.Event;

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
		if (message instanceof Event) {
			Event e = (Event) message;
			out.write(e.json());
		} else {
			unhandled(message);			
		}
	}
}
