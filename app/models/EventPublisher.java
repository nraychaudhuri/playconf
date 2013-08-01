package models;

import static models.Functions.findLongElement;
import static models.Twitter.userProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import models.messages.CloseConnectionEvent;
import models.messages.NewConnectionEvent;
import models.messages.RandomlySelectTalkEvent;
import models.messages.TalkSubmissionEvent;
import models.messages.UserRegistrationEvent;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Akka;
import play.libs.F;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.F.Callback;
import play.libs.Json;
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
				out.write(Json.newObject());
			}
		}
		if (message instanceof RandomlySelectTalkEvent) {
			Promise<JsonNode> promiseOfJson = randomlyPickSession();
			promiseOfJson.onRedeem(new Callback<JsonNode>() {
				@Override
				public void invoke(JsonNode json) throws Throwable {
					
				}
			});
		}
		if (message instanceof TalkSubmissionEvent) {

		} else
			unhandled(message);
	}

	public static F.Promise<JsonNode> randomlyPickSession() {
		Promise<Submission> promiseOfSubmission = play.libs.Akka
				.future(new Callable<Submission>() {
					public Submission call() {
						// randomly select one if the first
						Long randomId = (long) (1 + Math.random() * (5 - 1));
						return Submission.find.byId(randomId);
					}
				});

		Promise<JsonNode> promiseOfJson = promiseOfSubmission
				.flatMap(new Function<Submission, Promise<JsonNode>>() {
					public Promise<JsonNode> apply(Submission s) {
						return makeResult(s);
					}
				});
		return promiseOfJson;
	}

	private static Promise<JsonNode> makeResult(Submission s) {
		final ObjectNode result = Json.newObject();
		result.put("title", s.title);
		result.put("proposal", s.proposal);
		result.put("name", s.speaker.name);
		result.put("twitterId", s.speaker.twitterId);
		return findFollowers(s.speaker.twitterId).map(
				new Function<Long, JsonNode>() {
					public JsonNode apply(Long followerCount) {
						result.put("followerCount", followerCount);
						return result;
					}
				});
	}

	private static Promise<Long> findFollowers(final String screenName) {
		return userProfile(screenName).map(findLongElement("followers_count"));
	}

}
