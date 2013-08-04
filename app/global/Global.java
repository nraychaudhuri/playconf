package global;

import java.util.concurrent.TimeUnit;

import models.EventPublisher;
import models.messages.RandomlySelectTalkEvent;
import play.Application;
import play.GlobalSettings;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		super.onStart(app);
		Akka.system()
				.scheduler()
				.schedule(Duration.create(1, TimeUnit.SECONDS),
						Duration.create(10, TimeUnit.SECONDS),
						new Runnable() {
							public void run() {
								EventPublisher.publisher.tell(new RandomlySelectTalkEvent(), null);
							}
						}, 
						Akka.system().dispatcher());

	}
}
