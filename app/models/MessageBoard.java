package models;

import java.util.ArrayList;
import java.util.List;
import play.libs.F.Callback0;
import utils.ServerSentEvent;

public class MessageBoard {
	private static Integer counter = 1;

	private static List<ServerSentEvent> connections = new ArrayList<ServerSentEvent>();

	public static ServerSentEvent newBoard() {
		final ServerSentEvent sse = new ServerSentEvent() {
			@Override
			public void onConnected() {
				counter += 1;
				sendDataByName("visitors", "Total visitors: " + counter);
				notifyOthers(counter);
				onDisconnected(new Callback0() {
					@Override
					public void invoke() throws Throwable {
						connections.remove(this);
					}
				});
			}
		};
		connections.add(sse);
		return sse;
	}

	private static void notifyOthers(Integer counter) {
		for (ServerSentEvent sse : connections) {
			sse.sendDataByName("visitors", "Total visitors: " + counter);
		}
	}

	public static void userRegistrationEvent(String event) {
		for (ServerSentEvent sse : connections) {
			sse.sendDataByName("register", event);
		}
	}
}
