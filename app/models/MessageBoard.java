package models;

import java.util.ArrayList;
import java.util.List;

import play.libs.Comet;
import play.libs.F.Callback0;

public class MessageBoard {	
	private static Integer counter = 1;
	
	private static List<Comet> connections = new ArrayList<Comet>();
	public static Comet newBoard() {
		final Comet comet = new Comet("parent.messageBoard") {
			@Override
			public void onConnected() {
				counter += 1;
				sendMessage("Total visitors: " + counter);
				notifyOthers(counter);
				onDisconnected(new Callback0() {			
					@Override
					public void invoke() throws Throwable {
						connections.remove(this);
					}
				});
			}
		};	
		connections.add(comet);		
		return comet;
	}
	
	private static void notifyOthers(Integer counter) {
		for (Comet comet : connections) {
		  comet.sendMessage("Total visitors: " + counter);
		}
	}
}
