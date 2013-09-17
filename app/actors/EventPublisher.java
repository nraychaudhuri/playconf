package actors;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Akka;
import play.mvc.WebSocket.Out;
import actors.messages.CloseConnectionEvent;
import actors.messages.NewConnectionEvent;
import actors.messages.UserEvent;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.dispatch.Foreach;
import scala.Option;

public class EventPublisher extends UntypedActor {

    public final static ActorRef publisher = Akka.system().actorOf(
            new Props(EventPublisher.class));

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof NewConnectionEvent) {
            final NewConnectionEvent nce = (NewConnectionEvent) message;
            createUserActor(nce.uuid(), nce.out());
            Logger.info("New browser connected " + nce.uuid());
        }
        if (message instanceof CloseConnectionEvent) {
            final CloseConnectionEvent cce = (CloseConnectionEvent) message;
            final String uuid = cce.uuid();
            Option<ActorRef> child = getContext().child("user" + uuid);
            if(child.isDefined()) {
                getContext().stop(child.get());
                Logger.info("Browser " + uuid + "is disconnected");
            }
        }
        if (message instanceof UserEvent) {
            broadcastEvent((UserEvent) message);
        } else {
            unhandled(message);
        }
    }

    private ActorRef createUserActor(String uuid, final Out<JsonNode> out) {
        @SuppressWarnings("serial")
        ActorRef userActor = getContext().actorOf(
                new Props(new UntypedActorFactory() {
                    public UntypedActor create() {
                        return new UserActor(out);
                    }
                }), "user" + uuid);

        return userActor;
    }

    private void broadcastEvent(final UserEvent ure) {
        getContext().children().foreach(new akka.dispatch.Foreach<ActorRef>() {
            @Override
            public void each(ActorRef ref) throws Throwable {
                ref.forward(ure, context());
            }
        });
    }
}
