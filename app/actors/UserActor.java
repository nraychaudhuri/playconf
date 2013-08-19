package actors;

import org.codehaus.jackson.JsonNode;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;
import actors.messages.UserEvent;
import akka.actor.UntypedActor;

public class UserActor extends UntypedActor {

    private Out<JsonNode> out;

    public UserActor(WebSocket.Out<JsonNode> out) {
        this.out = out;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof UserEvent) {
            UserEvent e = (UserEvent) message;
            out.write(e.json());
        } else {
            unhandled(message);
        }
    }
}